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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView txtNome, txtValor, txtData, txtCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIcone = itemView.findViewById(R.id.imgIcone);
            txtNome = itemView.findViewById(R.id.txtNomeExtrato);
            txtValor = itemView.findViewById(R.id.txtValorExtrato);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaExtrato);
            txtData = itemView.findViewById(R.id.txtDataExtrato);
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

        // Define os atributos aos texts
        String nome = item.getDescricao();
        String categoria = item.getNomeCategoria();
        String data = item.getDataTransacao();

        if (nome == null || nome.isEmpty()) {
            nome = item.getNomeCategoria();
        }
        if (nome == null || nome.isEmpty()) {
            nome = item.isReceita() ? "Receita" : "Despesa";
        }

        holder.txtNome.setText(nome);
        holder.txtCategoria.setText(categoria);

        // Converte a data para o formato brasileiro
        holder.txtData.setText(converterDataParaBrasileiro(data));

        // Define valor formatado
        holder.txtValor.setText(item.getValorFormatado());

        // Define o ícone e cor baseado no tipo
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

    /**
     * Método para atualizar a lista
     */
    public void atualizarLista(List<ExtratoItem> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    private String converterDataParaBrasileiro(String dataISO) {
        if (dataISO == null || dataISO.trim().isEmpty()) {
            return "Sem data";
        }

        try {
            // Formato de entrada (ISO): yyyy-MM-dd
            SimpleDateFormat formatoISO = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // Formato de saída (Brasileiro): dd/MM/yyyy
            SimpleDateFormat formatoBR = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

            // Converte
            Date data = formatoISO.parse(dataISO);

            if (data != null) {
                return formatoBR.format(data);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Se falhar, retorna a data original
        return dataISO;
    }
}