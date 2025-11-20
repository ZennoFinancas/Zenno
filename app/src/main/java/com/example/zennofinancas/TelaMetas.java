package com.example.zennofinancas;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.example.zennofinancas.classes.clsDadosUsuario;

public class TelaMetas extends ActivityBase {

    Button btnAbrirCadastro;
    LinearLayout containerMetas;
    String idUsuario;
    TextView subtituloMetas;
    ScrollView scrollMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_metas);

        btnAbrirCadastro = findViewById(R.id.btnMetas);
        containerMetas = findViewById(R.id.containerMetas);
        subtituloMetas = findViewById(R.id.subtituloMetas);
        scrollMetas = findViewById(R.id.scrollMetas);


        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaMetas.this);
        if (usuario != null) {
            idUsuario = usuario.getIdUsuario();
        } else {
            Toast.makeText(TelaMetas.this, "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Busca as metas ja cadastradas
        buscarMetas();

        btnAbrirCadastro.setOnClickListener(v -> mostrarDialogCadastrarMeta());
    }

    private void mostrarDialogCadastrarMeta() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_cadastrar_meta, null);

        EditText txtNomeDaMeta = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorMeta = view.findViewById(R.id.txtValorReceita);
        Button btnCadastrarMeta = view.findViewById(R.id.btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnCadastrarMeta.setOnClickListener(v -> {
            String nome = txtNomeDaMeta.getText().toString().trim();
            String valor = txtValorMeta.getText().toString().trim();

            if (nome.isEmpty() || valor.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria e exibe as metas cadastradas
            clsMetodos.inserirObjetivo(TelaMetas.this, idUsuario,nome,valor,"");
            buscarMetas();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void adicionarMetaNaTela(String idMeta, String nome, String valorStr) {

        // infla o item_meta.xml (nome do seu layout do card)
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_meta, containerMetas, false);

        // pega os elementos do card (IDs do seu item_meta.xml)
        TextView tituloNomedaMeta = cardView.findViewById(R.id.tituloNomedaMeta);
        TextView valorMeta = cardView.findViewById(R.id.valorMeta);
        ProgressBar progressoMetas = cardView.findViewById(R.id.progressoMetas);
        ImageView imgEditarMeta = cardView.findViewById(R.id.imgEditarMeta);

        // seta o nome
        tituloNomedaMeta.setText(nome);

        // formata o valorNecessario para mostrar com duas casas decimais (R$ x,xx)
        double valorNecessario = parseCurrencyToDouble(valorStr);
        String valorFormatado = String.format(java.util.Locale.getDefault(), "R$%.2f", valorNecessario);
        // mostra: R$00,00  |  R$X,XX  (onde o primeiro é zero atual por enquanto)
        valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  %s", 0.0, valorFormatado));

        // configura a progress bar em CENTAVOS para suportar decimais
        int maxCents = (int) Math.round(valorNecessario * 100.0);
        if (maxCents <= 0) maxCents = 100; // fallback mínimo
        progressoMetas.setMax(maxCents);
        progressoMetas.setProgress(0); // por enquanto zero poupado

        // abrir o dialog de editar meta
        imgEditarMeta.setOnClickListener(v -> abrirDialogEditarMeta(
                tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario));

        // esconde o subtítulo inicial e mostra o ScrollView
        subtituloMetas.setVisibility(View.GONE);
        scrollMetas.setVisibility(View.VISIBLE);

        // adiciona o card no container
        containerMetas.addView(cardView);
    }

    private void abrirDialogEditarMeta(TextView tituloNomedaMeta, TextView valorMeta, ProgressBar progressoMetas, double valorNecessario) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editar_meta, null);

        EditText txtNomeDaMeta = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorMeta = view.findViewById(R.id.txtValorReceita);
        EditText txtValorAGuardar = view.findViewById(R.id.txtValorAGuardar);
        Button btnSalvarMeta = view.findViewById(R.id.btnSalvar);

        // Preenche os campos com os dados atuais
        txtNomeDaMeta.setText(tituloNomedaMeta.getText().toString());
        txtValorMeta.setText(String.format(java.util.Locale.getDefault(), "%.2f", valorNecessario));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnSalvarMeta.setOnClickListener(v -> {
            String novoNome = txtNomeDaMeta.getText().toString().trim();
            String novoValorNecessarioStr = txtValorMeta.getText().toString().trim();
            String valorGuardarStr = txtValorAGuardar.getText().toString().trim();

            if (novoNome.isEmpty() || novoValorNecessarioStr.isEmpty() || valorGuardarStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            double novoValorNecessario = parseCurrencyToDouble(novoValorNecessarioStr);
            double valorGuardar = parseCurrencyToDouble(valorGuardarStr);

            // Atualiza os valores na tela
            tituloNomedaMeta.setText(novoNome);

            // Atualiza o progresso da meta
            int novoMax = (int) Math.round(novoValorNecessario * 100.0);
            progressoMetas.setMax(novoMax);

            int progressoAtual = progressoMetas.getProgress() + (int) Math.round(valorGuardar * 100.0);
            if (progressoAtual > novoMax) progressoAtual = novoMax;
            progressoMetas.setProgress(progressoAtual);

            // Atualiza texto R$ atual | R$ total
            double valorAtual = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  R$%.2f", valorAtual, novoValorNecessario));

            // ✅ Verifica se a meta foi concluída
            if (progressoAtual >= novoMax) {
                Toast.makeText(this, "🎉 Meta concluída! Parabéns!", Toast.LENGTH_LONG).show();

                // Remove o card da tela
                View card = (View) tituloNomedaMeta.getParent(); // pega o card pai
                containerMetas.removeView(card);

                // Se não sobrar nenhuma meta, mostra o subtítulo de "nenhuma meta"
                if (containerMetas.getChildCount() == 0) {
                    subtituloMetas.setVisibility(View.VISIBLE);
                    scrollMetas.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Valor guardado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Atualiza as metas
    private void buscarMetas(){
        // LIMPA ANTES DE CARREGAR DE NOV
        containerMetas.removeAllViews();

        clsMetodos.buscarObjetivos(this, idUsuario, (e, metas) -> {
            if (e != null || metas == null) return;

            for (String[] meta : metas) {
                String idMeta = meta[0];     // ✔ ID da meta
                String nome = meta[1];
                String valor = meta[2];

                adicionarMetaNaTela(idMeta, nome, valor);
            }

        });

    }


    private double parseCurrencyToDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;

        s = s.trim();

        // Remove símbolos monetários
        s = s.replace("R$", "")
                .replace("R", "")
                .replace("$", "")
                .trim();

        // Se contiver vírgula, significa que está no formato BR "33,50"
        if (s.contains(",")) {
            s = s.replace(".", "");   // remove separador de milhar
            s = s.replace(",", ".");  // converte decimal
        }

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

}
