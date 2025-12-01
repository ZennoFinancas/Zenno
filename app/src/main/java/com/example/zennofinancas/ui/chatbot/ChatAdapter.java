package com.example.zennofinancas.ui.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.R;

// IMPORTANTE: Importar o Markwon (Certifique-se de ter adicionado no build.gradle)
import io.noties.markwon.Markwon;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<Message> messageList;

    // Variável do renderizador de Markdown
    private Markwon markwon;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? 1 : 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inicializa o Markwon se ainda não foi criado
        if (markwon == null) {
            markwon = Markwon.create(parent.getContext());
        }

        int layout = (viewType == 1)
                ? R.layout.item_message_user
                : R.layout.item_message_bot;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String texto = messageList.get(position).getText();

        // Usa o Markwon para formatar (Negrito, Itálico, etc)
        markwon.setMarkdown(holder.textMessage, texto);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        MessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}