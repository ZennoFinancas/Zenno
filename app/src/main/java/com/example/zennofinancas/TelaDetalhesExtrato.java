package com.example.zennofinancas;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zennofinancas.classes.clsDadosUsuario;
import com.example.zennofinancas.classes.clsDespesas;
import com.example.zennofinancas.clsMetodos;
import com.example.zennofinancas.classes.clsReceitas;
import com.example.zennofinancas.ui.home.HomeFragmento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TelaDetalhesExtrato extends ActivityBase {

    private EditText edtDescricao, edtValor, edtData;
    private Button btnSalvar, btnExcluir;
    private ImageView btnVoltar;

    // Novos componentes
    private RadioGroup rgTipo;
    private RadioButton rbReceita, rbDespesa;
    private Spinner spCategoria, spRepeticao;
    private Switch swtRepetir;

    private int idTransacao;
    private String tipoTransacaoOriginal;
    private String tabela;
    private String idUsuario;

    // Para controlar a categoria selecionada
    private ArrayList<String[]> listaCategoriasAtual = new ArrayList<>();
    private String idCategoriaSelecionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_detalhes_extrato);

        inicializarComponentes();
        recuperarDadosUsuario();
        configurarSpinnerRepeticao();

        // 1. Aplica a máscara ANTES de setar o texto
        HomeFragmento.aplicarValidacaoData(edtData);

        // Listener para troca de Receita/Despesa COM ANIMAÇÃO "POP" NO BOTÃO
        rgTipo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbReceita) {
                carregarCategorias("receita");
                animarSelecaoBotao(rbReceita); // Anima o botão Receita
            } else {
                carregarCategorias("gasto");
                animarSelecaoBotao(rbDespesa); // Anima o botão Despesa
            }
        });

        // Recupera dados
        recuperarDadosIntent();

        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        btnExcluir.setOnClickListener(v -> confirmarExclusao());
    }

    // --- NOVA ANIMAÇÃO CRIATIVA ---
    // Faz o botão selecionado dar um "pulo" elástico (Pop Effect)
    private void animarSelecaoBotao(View view) {
        // Escala X (Largura)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.1f, 1.0f);
        // Escala Y (Altura)
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.1f, 1.0f);

        // Overshoot cria o efeito de "passar do ponto e voltar" (elástico)
        scaleX.setInterpolator(new OvershootInterpolator(1.5f));
        scaleY.setInterpolator(new OvershootInterpolator(1.5f));

        scaleX.setDuration(400);
        scaleY.setDuration(400);

        scaleX.start();
        scaleY.start();
    }

    private void inicializarComponentes() {
        edtDescricao = findViewById(R.id.edtDescricaoDetalhes);
        edtValor = findViewById(R.id.edtValorDetalhes);
        edtData = findViewById(R.id.edtDataDetalhes);
        btnSalvar = findViewById(R.id.btnSalvarAlteracoes);
        btnExcluir = findViewById(R.id.btnExcluirTransacao);
        btnVoltar = findViewById(R.id.btnVoltarDetalhes);

        rgTipo = findViewById(R.id.rgTipoTransacao);
        rbReceita = findViewById(R.id.rbReceita);
        rbDespesa = findViewById(R.id.rbDespesa);
        spCategoria = findViewById(R.id.spCategoriaDetalhes);
        swtRepetir = findViewById(R.id.swtRepetirDetalhes);
        spRepeticao = findViewById(R.id.spRepeticaoDetalhes);

        // CORREÇÃO: Botão Salvar SEMPRE VERDE (#1C9D6A)
        btnSalvar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1C9D6A")));
    }

    private void recuperarDadosUsuario() {
        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(this);
        if (usuario != null) {
            idUsuario = usuario.getIdUsuario().toString();
        }
    }

    private void carregarCategorias(String tipo) {
        if (idUsuario == null) return;

        clsMetodos.buscarCategorias(this, idUsuario, tipo, (e, categorias) -> {
            if (e != null || categorias == null) return;

            listaCategoriasAtual = categorias;
            ArrayList<String> nomes = new ArrayList<>();
            for (String[] cat : categorias) {
                nomes.add(cat[1]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    nomes
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategoria.setAdapter(adapter);

            spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position < listaCategoriasAtual.size()) {
                        idCategoriaSelecionada = listaCategoriasAtual.get(position)[0];
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void recuperarDadosIntent() {
        if (getIntent() != null) {
            idTransacao = getIntent().getIntExtra("ID", 0);
            String descricao = getIntent().getStringExtra("DESCRICAO");
            double valor = getIntent().getDoubleExtra("VALOR", 0.0);
            String dataISO = getIntent().getStringExtra("DATA");
            tipoTransacaoOriginal = getIntent().getStringExtra("TIPO");

            edtDescricao.setText(descricao);
            edtValor.setText(String.format(Locale.US, "%.2f", valor));

            if (dataISO != null && !dataISO.isEmpty()) {
                String dataBR = clsMetodos.converterDataParaBR(dataISO);
                edtData.setText(dataBR);
            }

            // Configura o botão inicial (sem animação na abertura)
            if (tipoTransacaoOriginal != null && tipoTransacaoOriginal.equalsIgnoreCase("receita")) {
                tabela = "receitas";
                rbReceita.setChecked(true);
            } else {
                tabela = "despesas";
                rbDespesa.setChecked(true);
            }
        }
    }

    private void salvarAlteracoes() {
        String novaDescricao = edtDescricao.getText().toString().trim();
        String novoValorStr = edtValor.getText().toString().trim();
        String novaDataBR = edtData.getText().toString().trim();

        String novoTipo = rbReceita.isChecked() ? "receita" : "despesa";

        if (novaDescricao.isEmpty() || novoValorStr.isEmpty() || novaDataBR.length() < 10) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String novaDataISO = clsMetodos.converterDataParaISO(novaDataBR);
        String novoValor = novoValorStr.replace(",", ".");

        if (novoTipo.equalsIgnoreCase(tipoTransacaoOriginal)) {
            clsMetodos.atualizarTransacao(this, tabela, idTransacao, novaDescricao, novoValor, novaDataISO, new clsMetodos.CallbackSimples() {
                @Override
                public void onSucesso() {
                    verificarRepeticao(novaDescricao, novoValor, novaDataBR, novoTipo);
                }
                @Override
                public void onErro(String erro) { Toast.makeText(TelaDetalhesExtrato.this, "Erro: " + erro, Toast.LENGTH_SHORT).show(); }
            });
        } else {
            trocarTipoTransacao(novoTipo, novaDescricao, novoValor, novaDataBR);
        }
    }

    private void trocarTipoTransacao(String novoTipo, String desc, String val, String dataBR) {
        clsMetodos.excluirTransacao(this, tabela, idTransacao, new clsMetodos.CallbackSimples() {
            @Override
            public void onSucesso() {
                if (novoTipo.equals("receita")) {
                    clsReceitas.inserirReceita(TelaDetalhesExtrato.this, 1, idUsuario, idCategoriaSelecionada, val, desc, dataBR, new clsReceitas.ReceitaCallback() {
                        @Override
                        public void onSucesso(String msg, int qtd) { finalizarSucesso(); }
                        @Override
                        public void onErro(String erro) { Toast.makeText(TelaDetalhesExtrato.this, "Erro ao criar nova: " + erro, Toast.LENGTH_SHORT).show(); }
                    });
                } else {
                    clsDespesas.inserirDespesa(TelaDetalhesExtrato.this, 1, idUsuario, idCategoriaSelecionada, val, desc, dataBR);
                    finalizarSucesso();
                }
            }
            @Override
            public void onErro(String erro) {
                Toast.makeText(TelaDetalhesExtrato.this, "Erro ao trocar tipo: " + erro, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarRepeticao(String descricao, String valor, String dataBR, String tipoAtual) {
        if (swtRepetir.isChecked()) {
            int totalRepeticoes = obterNumeroRepeticoes();

            if (totalRepeticoes > 1) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(sdf.parse(dataBR));

                    for (int i = 1; i < totalRepeticoes; i++) {
                        cal.add(java.util.Calendar.MONTH, 1);
                        String novaDataBR = sdf.format(cal.getTime());

                        if (tipoAtual.equalsIgnoreCase("receita")) {
                            clsReceitas.inserirReceita(
                                    this,
                                    1,
                                    idUsuario,
                                    idCategoriaSelecionada,
                                    valor,
                                    descricao,
                                    novaDataBR,
                                    new clsReceitas.ReceitaCallback() {
                                        @Override
                                        public void onSucesso(String m, int q) { }
                                        @Override
                                        public void onErro(String e) { }
                                    }
                            );
                        } else {
                            clsDespesas.inserirDespesa(
                                    this,
                                    1,
                                    idUsuario,
                                    idCategoriaSelecionada,
                                    valor,
                                    descricao,
                                    novaDataBR
                            );
                        }
                    }
                    finalizarSucesso();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao calcular datas das parcelas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else { finalizarSucesso(); }
        } else { finalizarSucesso(); }
    }

    private void finalizarSucesso() {
        Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Deseja excluir este item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    clsMetodos.excluirTransacao(this, tabela, idTransacao, new clsMetodos.CallbackSimples() {
                        @Override
                        public void onSucesso() {
                            Toast.makeText(TelaDetalhesExtrato.this, "Excluído!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onErro(String erro) { }
                    });
                })
                .setNegativeButton("Não", null).show();
    }

    private void configurarSpinnerRepeticao() {
        spRepeticao = findViewById(R.id.spRepeticaoDetalhes);
        findViewById(R.id.containerSpRepeticaoDetalhes).setVisibility(View.GONE);
        ArrayList<String> numeros = new ArrayList<>();
        numeros.add("1x (Não repetir)");
        for (int i = 2; i <= 24; i++) { numeros.add(i + " Meses"); }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, numeros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRepeticao.setAdapter(adapter);

        swtRepetir.setOnCheckedChangeListener((bv, isChecked) -> {
            findViewById(R.id.containerSpRepeticaoDetalhes).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private int obterNumeroRepeticoes() {
        try {
            String sel = spRepeticao.getSelectedItem().toString();
            return Integer.parseInt(sel.replaceAll("^\\D*(\\d+).*$", "$1"));
        } catch (Exception e) { return 1; }
    }
}