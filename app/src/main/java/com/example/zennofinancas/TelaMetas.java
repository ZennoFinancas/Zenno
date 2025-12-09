package com.example.zennofinancas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.zennofinancas.classes.clsMetas;

public class TelaMetas extends ActivityBase {

    Button btnAbrirCadastro;
    ImageView btnVoltar; // 1. Variável para o botão voltar
    LinearLayout containerMetas;
    String idUsuario;
    TextView subtituloMetas;
    ScrollView scrollMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_metas);

        btnAbrirCadastro = findViewById(R.id.btnMetas);
        btnVoltar = findViewById(R.id.btnVoltarMetas); // 2. Vincular ID
        containerMetas = findViewById(R.id.containerMetas);
        subtituloMetas = findViewById(R.id.subtituloMetas);
        scrollMetas = findViewById(R.id.scrollMetas);

        // 3. Ação do botão voltar
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(TelaMetas.this, TelaInicial.class);
            startActivity(intent); // Fecha a tela de metas e volta para a Home
        });

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

            clsMetas.inserirObjetivo(TelaMetas.this, idUsuario, nome, valor, "", (erro, idMetaCriada) -> {
                if (erro != null) {
                    Toast.makeText(this, "Erro ao salvar meta!", Toast.LENGTH_SHORT).show();
                    return;
                }
                buscarMetas();
                dialog.dismiss();
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    private void adicionarMetaNaTela(String idMeta, String nome, String valorStr) {
        LayoutInflater inflater = LayoutInflater.from(this);
        // Inflamos o cardView
        View cardView = inflater.inflate(R.layout.item_meta, containerMetas, false);

        cardView.setTag("meta_id_" + idMeta);

        TextView tituloNomedaMeta = cardView.findViewById(R.id.lblEditarMeta);
        TextView valorMeta = cardView.findViewById(R.id.valorMeta);
        ProgressBar progressoMetas = cardView.findViewById(R.id.progressoMetas);
        ImageView imgGuardarDinheiro = cardView.findViewById(R.id.imgGuardarDinheiro);
        ImageView imgExcluirMeta = cardView.findViewById(R.id.imgExcluirMeta);

        tituloNomedaMeta.setText(nome);

        double valorNecessario = parseCurrencyToDouble(valorStr);
        String valorFormatado = String.format(java.util.Locale.getDefault(), "R$%.2f", valorNecessario);

        int maxCents = (int) Math.round(valorNecessario * 100.0);
        if (maxCents <= 0) maxCents = 100;
        progressoMetas.setMax(maxCents);

        int finalMaxCents = maxCents;
        clsMetas.buscarAportesObjetivo(this, idMeta, (e, totalAportes) -> {
            if (e != null) {
                Toast.makeText(TelaMetas.this, "Erro ao buscar aportes", Toast.LENGTH_SHORT).show();
                return;
            }
            int progressoAtual = (int) Math.round(totalAportes * 100.0);
            progressoMetas.setProgress(Math.min(progressoAtual, finalMaxCents));
            double valorGuardado = totalAportes;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  %s", valorGuardado, valorFormatado));
        });

        imgGuardarDinheiro.setOnClickListener(v -> abrirDialogGuardarDinheiro(
                tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario, idMeta));

        tituloNomedaMeta.setOnClickListener(v -> abrirDialogEditarMeta(
                tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario, idMeta));

        // 4. LÓGICA DE EXCLUSÃO VISUAL IMEDIATA
        imgExcluirMeta.setOnClickListener(v -> {
            // Manda excluir no banco
            clsMetas.excluirObjetivo(TelaMetas.this, idMeta);

            // Remove visualmente da tela NA HORA, sem esperar recarregar
            containerMetas.removeView(cardView);
            Toast.makeText(TelaMetas.this, "Meta excluída!", Toast.LENGTH_SHORT).show();

            // Verifica se não sobrou nenhuma meta para mostrar o texto "Nenhuma meta cadastrada"
            if (containerMetas.getChildCount() == 0) {
                subtituloMetas.setVisibility(View.VISIBLE);
                scrollMetas.setVisibility(View.GONE);
            }
        });

        subtituloMetas.setVisibility(View.GONE);
        scrollMetas.setVisibility(View.VISIBLE);
        containerMetas.addView(cardView);
    }

    private void abrirDialogGuardarDinheiro(TextView tituloNomedaMeta, TextView valorMeta,
                                            ProgressBar progressoMetas, double valorNecessario, String idMeta) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_guardar_meta, null);

        EditText txtValorAGuardar = view.findViewById(R.id.txtNomeReceita);
        Button btnGuardar = view.findViewById(R.id.btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnGuardar.setOnClickListener(v -> {
            String valorStr = txtValorAGuardar.getText().toString().trim();

            if (valorStr.isEmpty()) {
                Toast.makeText(this, "Informe o valor a guardar!", Toast.LENGTH_SHORT).show();
                return;
            }

            double valor = parseCurrencyToDouble(valorStr);

            int aporteCents = (int) Math.round(valor * 100.0);
            int progressoAtual = progressoMetas.getProgress();
            int progressoMaximo = progressoMetas.getMax();

            if (progressoAtual + aporteCents > progressoMaximo) {
                double valorPermitido = (progressoMaximo - progressoAtual) / 100.0;
                Toast.makeText(this, "Você só pode guardar até R$" + String.format("%.2f", valorPermitido), Toast.LENGTH_LONG).show();
                return;
            }

            progressoAtual += aporteCents;
            progressoMetas.setProgress(progressoAtual);

            clsMetas.inserirAporteObjetivo(TelaMetas.this, idMeta, valorStr, idUsuario);

            double valorGuardado = progressoAtual / 100.0;
            valorMeta.setText(String.format("R$%.2f  |  R$%.2f", valorGuardado, valorNecessario));

            dialog.dismiss();
        });

        dialog.show();
    }

    private void abrirDialogEditarMeta(TextView tituloNomedaMeta, TextView valorMeta,
                                       ProgressBar progressoMetas, double valorNecessarioAntigo, String idMeta) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editar_meta, null);

        EditText txtNomeDaMeta = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorTotalMeta = view.findViewById(R.id.txtValorReceita);
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

            int novoMax = (int) Math.round(novoValorTotal * 100.0);
            int progressoAtual = progressoMetas.getProgress();

            progressoMetas.setMax(novoMax);

            if (progressoAtual > novoMax) progressoAtual = novoMax;

            progressoMetas.setProgress(progressoAtual);

            double valorGuardado = progressoAtual / 100.0;
            valorMeta.setText(String.format("R$%.2f  |  R$%.2f", valorGuardado, novoValorTotal));

            Toast.makeText(this, "Meta atualizada!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void buscarMetas(){
        containerMetas.removeAllViews();
        clsMetas.buscarObjetivos(this, idUsuario, (e, metas) -> {
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