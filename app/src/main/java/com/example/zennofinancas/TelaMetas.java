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
import com.example.zennofinancas.classes.clsMetas;

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

            clsMetas.inserirObjetivo(TelaMetas.this, idUsuario, nome, valor, "");
            buscarMetas();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void adicionarMetaNaTela(String idMeta, String nome, String valorStr) {
        LayoutInflater inflater = LayoutInflater.from(this);
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

        // 🔥 BUSCAR OS APORTES CADASTRADOS PARA ESTA META
        int finalMaxCents = maxCents;
        clsMetodos.buscarAportesObjetivo(this, idMeta, (e, totalAportes) -> {
            if (e != null) {
                Toast.makeText(TelaMetas.this, "Erro ao buscar aportes", Toast.LENGTH_SHORT).show();
                return;
            }

            // Converter o total de aportes para centavos
            int progressoAtual = (int) Math.round(totalAportes * 100.0);

            // Se ultrapassar o máximo, limitar ao máximo
            if (progressoAtual > finalMaxCents) progressoAtual = finalMaxCents;

            progressoMetas.setProgress(progressoAtual);

            // Atualizar o texto exibindo os valores
            double valorGuardadoReais = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  %s", valorGuardadoReais, valorFormatado));
        });

        imgGuardarDinheiro.setOnClickListener(v -> abrirDialogGuardarDinheiro(
                cardView, tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario, idMeta));

        tituloNomedaMeta.setOnClickListener(v -> abrirDialogEditarMeta(
                cardView, tituloNomedaMeta, valorMeta, progressoMetas, valorNecessario, idMeta));

        imgExcluirMeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clsMetas.excluirObjetivo(TelaMetas.this, idMeta);
                buscarMetas();
            }
        });

        subtituloMetas.setVisibility(View.GONE);
        scrollMetas.setVisibility(View.VISIBLE);
        containerMetas.addView(cardView);
    }

    private void abrirDialogGuardarDinheiro(View cardView, TextView tituloNomedaMeta, TextView valorMeta,
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
            String valorGuardarStr = txtValorAGuardar.getText().toString().trim();

            if (valorGuardarStr.isEmpty()) {
                Toast.makeText(this, "Informe o valor a guardar!", Toast.LENGTH_SHORT).show();
                return;
            }

            double valorGuardar = parseCurrencyToDouble(valorGuardarStr);

            // CORREÇÃO 3: Usar o idMeta recebido como parâmetro
            if (idMeta == null || idMeta.isEmpty()) {
                Toast.makeText(this, "Erro: ID da meta não encontrado!", Toast.LENGTH_SHORT).show();
                return;
            }

            int progressoAtual = progressoMetas.getProgress() + (int) Math.round(valorGuardar * 100.0);
            int maximo = progressoMetas.getMax();

            if (progressoAtual > maximo) progressoAtual = maximo;
            progressoMetas.setProgress(progressoAtual);

            double valorAtualReais = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  R$%.2f", valorAtualReais, valorNecessario));

            if (progressoAtual >= maximo) {
                Toast.makeText(this, "🎉 Meta concluída! Parabéns!", Toast.LENGTH_LONG).show();

                if (cardView.getParent() instanceof LinearLayout) {
                    ((LinearLayout) cardView.getParent()).removeView(cardView);
                }

                if (containerMetas.getChildCount() == 0) {
                    subtituloMetas.setVisibility(View.VISIBLE);
                    scrollMetas.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Valor guardado com sucesso!", Toast.LENGTH_SHORT).show();

                // CORREÇÃO 4: Passar o idMeta correto para inserir o aporte
                clsMetas.inserirAporteObjetivo(TelaMetas.this, idMeta, valorGuardarStr);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void abrirDialogEditarMeta(View cardView, TextView tituloNomedaMeta, TextView valorMeta,
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

            double valorGuardadoReais = progressoAtual / 100.0;
            valorMeta.setText(String.format(java.util.Locale.getDefault(), "R$%.2f  |  R$%.2f", valorGuardadoReais, novoValorTotal));

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