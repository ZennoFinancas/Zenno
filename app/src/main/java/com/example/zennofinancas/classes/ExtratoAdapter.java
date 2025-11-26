package com.example.zennofinancas.classes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.R;

import java.util.List;

public class ExtratoAdapter extends RecyclerView.Adapter<ExtratoAdapter.ViewHolder> {

    private List<ExtratoItem> lista;
    private OnItemClickListener listener;

    // Interface para cliques nos itens
    public interface OnItemClickListener {
        void onItemClick(ExtratoItem item);
    }

    public ExtratoAdapter(List<ExtratoItem> lista) {
        this.lista = lista;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcone;
        TextView txtNome, txtValor;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIcone = itemView.findViewById(R.id.imgIcone);
            txtNome = itemView.findViewById(R.id.txtNomeExtrato);
            txtValor = itemView.findViewById(R.id.txtValor);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_extrato, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExtratoItem item = lista.get(position);

        // Define nome (categoria ou descriÃ§Ã£o)
        String nome = item.getNomeCategoria();
        if (nome == null || nome.isEmpty()) {
            nome = item.getDescricao();
        }
        if (nome == null || nome.isEmpty()) {
            nome = item.isReceita() ? "Receita" : "Despesa";
        }
        holder.txtNome.setText(nome);

        // Define valor formatado
        holder.txtValor.setText(item.getValorFormatado());

        // Define Ã­cone e cor baseado no tipo
        if (item.isReceita()) {
            holder.imgIcone.setImageResource(R.drawable.receita);
            holder.txtValor.setTextColor(Color.parseColor("#00BF63"));
        } else {
            holder.imgIcone.setImageResource(R.drawable.despesa);
            holder.txtValor.setTextColor(Color.parseColor("#ff3131"));
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    // MÃ©todo para atualizar a lista
    public void atualizarLista(List<ExtratoItem> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }
}