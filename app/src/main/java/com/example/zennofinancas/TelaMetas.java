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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zennofinancas.classes.clsDadosUsuario;
// Mantenha seus imports de ActivityBase e clsMetodos aqui

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

        buscarMetas();

        btnAbrirCadastro.setOnClickListener(v -> mostrarDialogCadastrarMeta());
    }

    private void mostrarDialogCadastrarMeta() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_cadastrar_meta, null);

        // Tentei usar os IDs novos. Se o seu dialog_cadastrar for antigo, mude para txtNomeReceita aqui
        EditText txtNomeDaMeta = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorMeta = view.findViewById(R.id.txtValorMeta);
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

            clsMetodos.inserirObjetivo(TelaMetas.this, idUsuario, nome, valor, "");
            buscarMetas();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void adicionarMetaNaTela(String idMeta, String nome, String valorStr) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_meta, containerMetas, false);

        TextView tituloNomedaMeta = cardView.findViewById(R.id.lblEditarMeta);
        TextView valorMeta = cardView.findViewById(R.id.valorMeta);
        ProgressBar progressoMetas = cardView.findViewById(R.id.progressoMetas);
        ImageView imgGuardarDinheiro = cardView.findViewById(R.id.imgGuardarDinheiro);

        tituloNomedaMeta.setText(nome);

        double valorNecessario = parseCurrencyToDouble(valorStr);
        String valorFormatado = String.format(java.util.Locale.getDefault(), "R$%.2f", valorNecessario);

        // Exibe R$0.00 guardado | R$ Total
        valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  %s", 0.0, valorFormatado));

        int maxCents = (int) Math.round(valorNecessario * 100.0);
        if (maxCents <= 0) maxCents = 100;
        progressoMetas.setMax(maxCents);
        progressoMetas.setProgress(0);

        // --- LÓGICA CORRIGIDA ---

        // 1. Clicar no CIFRÃO ($) -> Abre 'item_guardar_meta' para depositar valor
        imgGuardarDinheiro.setOnClickListener(v -> abrirDialogGuardarDinheiro(
                tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario));

        // 2. Clicar no NOME DA META -> Abre 'dialog_editar_meta' para mudar nome/valor total
        tituloNomedaMeta.setOnClickListener(v -> abrirDialogEditarMeta(
                tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario));

        // ------------------------

        subtituloMetas.setVisibility(View.GONE);
        scrollMetas.setVisibility(View.VISIBLE);
        containerMetas.addView(cardView);
    }

    // Abre o layout item_guardar_meta.xml
    private void abrirDialogGuardarDinheiro(TextView tituloNomedaMeta, TextView valorMeta, ProgressBar progressoMetas, double valorNecessario) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_guardar_meta, null);

        // CORREÇÃO: No seu XML item_guardar_meta, o campo de digitar valor está com ID txtNomeMeta
        EditText txtValorAGuardar = view.findViewById(R.id.txtNomeReceita);
        Button btnGuardar = view.findViewById(R.id.btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnGuardar.setOnClickListener(v -> {
            String valorGuardarStr = txtValorAGuardar.getText().toString().trim();

            if (valorGuardarStr.isEmpty()) {
                Toast.makeText(this, "Informe o valor a guardar!", Toast.LENGTH_SHORT).show();
                return;
            }

            double valorGuardar = parseCurrencyToDouble(valorGuardarStr);

            // Soma o novo valor ao progresso atual
            int progressoAtual = progressoMetas.getProgress() + (int) Math.round(valorGuardar * 100.0);
            int maximo = progressoMetas.getMax();

            if (progressoAtual > maximo) progressoAtual = maximo;
            progressoMetas.setProgress(progressoAtual);

            double valorAtualReais = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  R$%.2f", valorAtualReais, valorNecessario));

            if (progressoAtual >= maximo) {
                Toast.makeText(this, "🎉 Meta concluída! Parabéns!", Toast.LENGTH_LONG).show();

                // Remove o card
                View card = (View) tituloNomedaMeta.getParent().getParent(); // RelativeLayout -> CardView
                if (card != null && card.getParent() instanceof LinearLayout) {
                    ((LinearLayout) card.getParent()).removeView(card);
                } else if (tituloNomedaMeta.getParent() instanceof View) {
                    // Fallback caso a hierarquia seja diferente
                    containerMetas.removeView((View) tituloNomedaMeta.getParent());
                }

                if (containerMetas.getChildCount() == 0) {
                    subtituloMetas.setVisibility(View.VISIBLE);
                    scrollMetas.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Valor guardado com sucesso!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    // Abre o layout dialog_editar_meta.xml
    private void abrirDialogEditarMeta(TextView tituloNomedaMeta, TextView valorMeta, ProgressBar progressoMetas, double valorNecessarioAntigo) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editar_meta, null);

        // CORREÇÃO: Usando os IDs corretos do seu XML dialog_editar_meta
        EditText txtNomeDaMeta = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorTotalMeta = view.findViewById(R.id.txtValorMeta);
        Button btnSalvarEdicao = view.findViewById(R.id.btnSalvar);

        txtNomeDaMeta.setText(tituloNomedaMeta.getText().toString());
        txtValorTotalMeta.setText(String.format(java.util.Locale.getDefault(), "%.2f", valorNecessarioAntigo));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnSalvarEdicao.setOnClickListener(v -> {
            String novoNome = txtNomeDaMeta.getText().toString().trim();
            String novoValorTotalStr = txtValorTotalMeta.getText().toString().trim();

            if (novoNome.isEmpty() || novoValorTotalStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            double novoValorTotal = parseCurrencyToDouble(novoValorTotalStr);

            tituloNomedaMeta.setText(novoNome);

            // Recalcula a barra de progresso com o novo total
            int novoMax = (int) Math.round(novoValorTotal * 100.0);
            int progressoAtual = progressoMetas.getProgress();

            progressoMetas.setMax(novoMax);

            if (progressoAtual > novoMax) progressoAtual = novoMax;
            progressoMetas.setProgress(progressoAtual);

            double valorGuardadoReais = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  R$%.2f", valorGuardadoReais, novoValorTotal));

            Toast.makeText(this, "Meta atualizada!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void buscarMetas(){
        containerMetas.removeAllViews();
        clsMetodos.buscarObjetivos(this, idUsuario, (e, metas) -> {
            if (e != null || metas == null) return;
            for (String[] meta : metas) {
                adicionarMetaNaTela(meta[0], meta[1], meta[2]);
            }
        });
    }

    private double parseCurrencyToDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        s = s.trim().replace("R$", "").replace("R", "").replace("$", "").trim();
        if (s.contains(",")) {
            s = s.replace(".", "").replace(",", ".");
        }
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }
}