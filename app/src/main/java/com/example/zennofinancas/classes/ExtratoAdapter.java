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

    public ExtratoAdapter(List<ExtratoItem> lista) {
        this.lista = lista;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcone;
        TextView txtNome, txtValor;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIcone = itemView.findViewById(R.id.imgIcone);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtValor = itemView.findViewById(R.id.txtValor);
        }
    }

    @Override
    public ExtratoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_extrato, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExtratoAdapter.ViewHolder holder, int position) {
        ExtratoItem item = lista.get(position);

        holder.txtNome.setText(item.getNome());
        holder.txtValor.setText(item.getValor());

        if (item.getTipo().equals("receita")) {
            holder.imgIcone.setImageResource(R.drawable.receita);
            holder.txtValor.setTextColor(Color.parseColor("#00BF63"));
        } else {
            holder.imgIcone.setImageResource(R.drawable.despesa);
            holder.txtValor.setTextColor(Color.parseColor("#ff3131"));
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}