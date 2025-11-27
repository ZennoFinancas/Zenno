package com.example.zennofinancas.ui.chatbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.R;

import com.example.zennofinancas.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBotFragmento extends Fragment {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private final List<Message> messageList = new ArrayList<>();


    private static final String API_KEY = new String(
            Base64.decode(BuildConfig.GEMINI_KEY_ENCODED, Base64.DEFAULT)
    );


    private static final String PREFS_NAME = "ZennoChatPrefs";
    private static final String KEY_CHAT_HISTORY = "chat_history";
    private static final String KEY_TIMESTAMP = "last_saved_time";

    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    private static final String PROMPT_SISTEMA =
            "Você é o assistente virtual oficial do aplicativo 'Zenno Finanças'. " +
                    "Seu tom é profissional, mas amigável e motivador. Use emojis 💰📊. " +
                    "Ajude o usuário a organizar gastos, entender economia básica e dicas de poupança. " +
                    "Fale sempre em Português (PT-BR) e use valores em Reais (R$). " +
                    "Se perguntarem algo que não seja sobre finanças ou o app, diga educadamente: 'Desculpe, como assistente do Zenno, só posso ajudar com suas finanças.' " +
                    "Seja conciso e direto nas respostas."+
                    "Não responder com textos em negrito";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public ChatBotFragmento() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_chat_bot, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        loadChatHistory();

        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        if (!messageList.isEmpty()) {
            recyclerChat.scrollToPosition(messageList.size() - 1);
        }

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        messageList.add(new Message(text, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerChat.scrollToPosition(messageList.size() - 1);

        etMessage.setText("");

        saveChatHistory();
        callGeminiApi(text);
    }

    private void callGeminiApi(String question) {

        String urlFixa = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY.trim();

        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject part = new JSONObject();
            String promptFinal = PROMPT_SISTEMA + "\n\nPergunta do usuário: " + question;
            part.put("text", promptFinal);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            jsonBody.put("contents", contents);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(urlFixa)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateUi("Erro de conexão. Verifique sua internet.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");

                        if (candidates.length() > 0) {
                            JSONObject first = candidates.getJSONObject(0);
                            String resposta = "";
                            if (first.has("content")) {
                                resposta = first.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
                            }
                            if (resposta.isEmpty()) resposta = "Não consegui formular uma resposta.";
                            updateUi(resposta);
                        } else {
                            updateUi("O assistente ficou em silêncio.");
                        }
                    } catch (Exception e) {
                        updateUi("Erro ao processar resposta.");
                    }
                } else {
                    Log.e("ZennoBot", "Erro: " + responseBody);
                    updateUi("Erro no servidor: " + response.code());
                }
            }
        });
    }

    private void updateUi(String text) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                messageList.add(new Message(text, false));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerChat.scrollToPosition(messageList.size() - 1);
                saveChatHistory();
            });
        }
    }

    private void saveChatHistory() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        try {
            for (Message message : messageList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("text", message.getText());
                jsonObject.put("isUser", message.isUser());
                jsonArray.put(jsonObject);
            }

            prefs.edit()
                    .putString(KEY_CHAT_HISTORY, jsonArray.toString())
                    .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        long lastSavedTime = prefs.getLong(KEY_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSavedTime > EXPIRATION_TIME) {
            prefs.edit().clear().apply();
            return;
        }

        String jsonString = prefs.getString(KEY_CHAT_HISTORY, null);
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                messageList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String text = obj.getString("text");
                    boolean isUser = obj.getBoolean("isUser");
                    messageList.add(new Message(text, isUser));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}