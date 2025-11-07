package com.example.zennofinancas.ui.chatbot;

import android.os.Bundle;
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

import com.example.zennofinancas.R;

import java.util.ArrayList;
import java.util.List;

public class ChatBotFragmento extends Fragment {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private final List<Message> messageList = new ArrayList<Message>();

    public ChatBotFragmento() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_chat_bot, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        // Configura RecyclerView
        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        // Enviar mensagem
        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String userMessage = etMessage.getText().toString().trim();
        if (userMessage.isEmpty()) return;

        // Adiciona mensagem do usuário
        messageList.add(new Message(userMessage, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerChat.scrollToPosition(messageList.size() - 1);
        etMessage.setText("");

        // Resposta simples
        String botReply;
        if (userMessage.equalsIgnoreCase("oi")) {
            botReply = "olá 👋";
        } else {
            botReply = "Desculpe, ainda estou aprendendo!";
        }

        // Adiciona resposta do bot
        recyclerChat.postDelayed(() -> {
            messageList.add(new Message(botReply, false));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerChat.scrollToPosition(messageList.size() - 1);
        }, 500);
    }
}