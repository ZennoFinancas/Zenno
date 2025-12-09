package com.example.zennofinancas.ui.chatbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

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

    // Identificador da mensagem temporária
    private static final String MSG_DIGITANDO_BASE = "Digitando";

    // Handler para controlar o tempo e animação
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable animacaoDigitandoRunnable;
    private boolean isBotDigitando = false;

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
                    "Seja conciso e direto nas respostas." +
                    "Não responder com textos em negrito " +
                    "Criadores do Zenno Finanças: " +
                    "Lincoln Silva Vieira - Programador Backend do site. " +
                    "Matheus De Lima Ribeiro - Programador Backend do app. " +
                    "Matheus Norberto Dos Reis - Programador Frontend do app. " +
                    "Pedro Costa Pereira - Programador Frontend do app. " +
                    "Vinicius Alves De Souza - Programador Backend do app. " +
                    "Vinicius Csetneky De Araujo - Programador Frontend do site.";


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

        // Ajuste Teclado
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        loadChatHistory();

        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        recyclerChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                scrollToBottom();
            }
        });

        if (!messageList.isEmpty()) {
            scrollToBottom();
        }

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void scrollToBottom() {
        if (!messageList.isEmpty()) {
            recyclerChat.postDelayed(() -> recyclerChat.smoothScrollToPosition(messageList.size() - 1), 100);
        }
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // 1. Adiciona mensagem do Usuário
        messageList.add(new Message(text, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
        etMessage.setText("");

        saveChatHistory();

        // 2. DELAY para parecer que o bot está "lendo" (800ms)
        handler.postDelayed(() -> {
            // Adiciona o placeholder "Digitando..." e inicia animação
            mostrarIndicadorDigitando();
        }, 800);

        // Chama a API
        callGeminiApi(text);
    }

    private void mostrarIndicadorDigitando() {
        if (isBotDigitando) return; // Já está mostrando

        isBotDigitando = true;
        messageList.add(new Message(MSG_DIGITANDO_BASE + "...", false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();

        // ANIMAÇÃO DOS PONTINHOS (...)
        animacaoDigitandoRunnable = new Runnable() {
            int pontos = 0;
            @Override
            public void run() {
                if (!isBotDigitando || messageList.isEmpty()) return;

                int lastIndex = messageList.size() - 1;
                // Proteção para não crashar se a lista estiver vazia ou índice inválido
                if (lastIndex < 0) return;

                Message msg = messageList.get(lastIndex);

                // Só anima se a última mensagem for a do bot digitando
                if (!msg.isUser() && msg.getText().startsWith(MSG_DIGITANDO_BASE)) {
                    pontos = (pontos % 3) + 1;
                    StringBuilder sb = new StringBuilder(MSG_DIGITANDO_BASE);
                    for (int i = 0; i < pontos; i++) sb.append(".");

                    // Atualiza o texto na lista (Simula animação)
                    try {
                        messageList.set(lastIndex, new Message(sb.toString(), false));
                        chatAdapter.notifyItemChanged(lastIndex);
                    } catch (Exception e) {
                        // Ignora erro se a lista mudou rápido demais
                    }

                    // Repete a cada 400ms
                    handler.postDelayed(this, 400);
                }
            }
        };
        handler.post(animacaoDigitandoRunnable);
    }

    private void updateUi(String text) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // 1. Para a animação
                isBotDigitando = false;
                if (animacaoDigitandoRunnable != null) {
                    handler.removeCallbacks(animacaoDigitandoRunnable);
                }

                // 2. Procura a mensagem "Digitando..." para substituir
                int indexDigitando = -1;
                if (!messageList.isEmpty()) {
                    int lastIndex = messageList.size() - 1;
                    Message lastMsg = messageList.get(lastIndex);
                    if (!lastMsg.isUser() && lastMsg.getText().startsWith(MSG_DIGITANDO_BASE)) {
                        indexDigitando = lastIndex;
                    }
                }

                if (indexDigitando != -1) {
                    // Substitui "Digitando..." pela resposta real
                    messageList.set(indexDigitando, new Message(text, false));
                    chatAdapter.notifyItemChanged(indexDigitando);
                } else {
                    // Se por acaso não tiver o "Digitando", adiciona no fim
                    messageList.add(new Message(text, false));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                }

                scrollToBottom();
                saveChatHistory();
            });
        }
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
            updateUi("Erro interno.");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(urlFixa).post(body).build();

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
                            String resposta = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
                            updateUi(resposta);
                        } else {
                            updateUi("O assistente ficou em silêncio.");
                        }
                    } catch (Exception e) {
                        updateUi("Erro ao processar resposta.");
                    }
                } else {
                    updateUi("Erro no servidor: " + response.code());
                }
            }
        });
    }

    private void saveChatHistory() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        try {
            for (Message message : messageList) {
                // Não salva mensagens de "Digitando..."
                if (!message.isUser() && message.getText().startsWith(MSG_DIGITANDO_BASE)) continue;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("text", message.getText());
                jsonObject.put("isUser", message.isUser());
                jsonArray.put(jsonObject);
            }
            prefs.edit().putString(KEY_CHAT_HISTORY, jsonArray.toString()).putLong(KEY_TIMESTAMP, System.currentTimeMillis()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastSavedTime = prefs.getLong(KEY_TIMESTAMP, 0);
        if (System.currentTimeMillis() - lastSavedTime > EXPIRATION_TIME) {
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