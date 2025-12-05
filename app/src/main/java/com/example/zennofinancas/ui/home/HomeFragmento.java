package com.example.zennofinancas.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.R;
import com.example.zennofinancas.TelaAnalise;
import com.example.zennofinancas.TelaMetas;
import com.example.zennofinancas.TelaMeuPerfil;
import com.example.zennofinancas.classes.AnimacaoUtils;
import com.example.zennofinancas.classes.ExtratoAdapter;
import com.example.zennofinancas.classes.ExtratoItem;
import com.example.zennofinancas.classes.SupabaseHelper;
import com.example.zennofinancas.classes.clsDadosUsuario;
import com.example.zennofinancas.classes.clsDespesas;
import com.example.zennofinancas.classes.clsReceitas;
import com.example.zennofinancas.clsMetodos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragmento extends Fragment {
    TextView txtSaldoAtual, txtReceitasHome, txtDespesasHome;
    androidx.cardview.widget.CardView btnCardAnalise;
    ImageView btnAddReceita, btnAddDespesa, btnMetas, imgFotoUsuario, btnVerNumerosHome;
    RecyclerView rvExtrato;
    boolean isSaldoVisivel = true;
    private String idUsuario;

    // Lista e adapter para o RecyclerView
    private List<ExtratoItem> listaDespesasProximas = new ArrayList<>();
    private ExtratoAdapter adapterDespesas;

    clsMetodos supabase = new clsMetodos();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtSaldoAtual = view.findViewById(R.id.txtSaldoAtual);
        txtReceitasHome = view.findViewById(R.id.txtReceitasHome);
        txtDespesasHome = view.findViewById(R.id.txtDespesasHome);
        btnAddReceita = view.findViewById(R.id.btnReceitasHome);
        btnAddDespesa = view.findViewById(R.id.btnDespesasHome);
        btnMetas = view.findViewById(R.id.Metas);
        rvExtrato = view.findViewById(R.id.rvExtrato);
        imgFotoUsuario = view.findViewById(R.id.imgVoltar); // Referencia o ícone do perfil
        btnVerNumerosHome = view.findViewById(R.id.btnVerNumerosHome);
        btnCardAnalise = view.findViewById(R.id.cardAnalise);

        btnCardAnalise.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TelaAnalise.class);
            startActivity(intent);
        });

        // Configura o RecyclerView
        configurarRecyclerView();

        btnVerNumerosHome.setOnClickListener(v -> {
            if (isSaldoVisivel) {
                txtSaldoAtual.setText("R$ ****,**");
                txtReceitasHome.setText("R$ ****,**");
                txtDespesasHome.setText("R$ ****,**");
                btnVerNumerosHome.setImageResource(R.drawable.nao_vizualizar);
                isSaldoVisivel = false;
            } else {
                calcularSaldo();
                btnVerNumerosHome.setImageResource(R.drawable.vizualizar);
                isSaldoVisivel = true;
            }
        });

        // --- CARREGAMENTO DO USUÁRIO E FOTO ---
        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(requireContext());

        if (usuario != null) {
            idUsuario = usuario.getIdUsuario().toString();

            // CORREÇÃO: Agora chamando getFotoUsuario() para bater com sua classe
            String fotoBase64 = usuario.getFotoUsuario();

            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                Bitmap bitmapUsuario = getBitmapFromBase64(fotoBase64);
                if (bitmapUsuario != null) {
                    imgFotoUsuario.setImageBitmap(bitmapUsuario);
                }
            }
            // Se não tiver foto, ele mantém o src que está no XML (o ícone padrão)

        } else {
            Toast.makeText(requireContext(), "Falha ao carregar usuário.", Toast.LENGTH_SHORT).show();
        }

        // Carrega os dados
        calcularSaldo();
        carregarReceitasRecentes();

        btnMetas.setOnClickListener(v -> {
            Intent metas = new Intent(getActivity(), TelaMetas.class);
            startActivity(metas);
        });

        btnAddReceita.setOnClickListener(v -> exibirPopupReceita());

        imgFotoUsuario.setOnClickListener(v -> {
            Intent imgFotoMetas = new Intent(getActivity(), TelaMeuPerfil.class);
            startActivity(imgFotoMetas);
        });

        btnAddDespesa.setOnClickListener(v -> exibirPopupDespesa());
    }

    private void configurarRecyclerView() {
        rvExtrato.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDespesas = new ExtratoAdapter(listaDespesasProximas);

        adapterDespesas.setOnItemClickListener(item -> {
            Toast.makeText(getContext(),
                    "Despesa: " + item.getNomeCategoria() + " - " + item.getValorFormatado(),
                    Toast.LENGTH_SHORT).show();
        });

        rvExtrato.setAdapter(adapterDespesas);
    }

    /**
     * Busca receitas dos últimos 15 dias e ordena pelo ID (Último cadastrado primeiro)
     */
    private void carregarReceitasRecentes() {
        if (getContext() == null || idUsuario == null) return;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -15);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date dataLimite = cal.getTime();

        SupabaseHelper.buscarExtrato(
                getContext(),
                idUsuario,
                null,
                null,
                "receita",
                (e, result) -> {
                    if (e != null) return;

                    if (result != null && !result.isEmpty()) {
                        List<ExtratoItem> receitasFiltradas = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        for (ExtratoItem item : result) {
                            try {
                                Date dataItem = sdf.parse(item.getDataTransacao());
                                if (dataItem != null && !dataItem.before(dataLimite)) {
                                    receitasFiltradas.add(item);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        receitasFiltradas.sort((item1, item2) ->
                                Integer.compare(item2.getIdTransacao(), item1.getIdTransacao())
                        );

                        if (receitasFiltradas.size() > 5) {
                            receitasFiltradas = receitasFiltradas.subList(0, 5);
                        }

                        adapterDespesas.atualizarLista(receitasFiltradas);

                        if (receitasFiltradas.isEmpty()) {
                            rvExtrato.setVisibility(View.GONE);
                        } else {
                            rvExtrato.setVisibility(View.VISIBLE);
                        }

                    } else {
                        listaDespesasProximas.clear();
                        adapterDespesas.atualizarLista(listaDespesasProximas);
                        rvExtrato.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void exibirPopupReceita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_receita, null);
        builder.setView(view);

        EditText txtNomeReceita = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorReceita = view.findViewById(R.id.txtValorReceita);
        EditText txtDataReceita = view.findViewById(R.id.txtDataReceita);

        aplicarValidacaoData(txtDataReceita);

        Button btnSalvar = view.findViewById(R.id.btnSalvar);
        Spinner spCategoria = view.findViewById(R.id.spTipoReceita);
        Spinner spRepeticao = view.findViewById(R.id.spRepeticao);
        ImageView imgSetaCategoria = view.findViewById(R.id.imgCategorias);

        AnimacaoUtils.configurarSetaSpinner(spCategoria, imgSetaCategoria);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        clsMetodos.buscarCategorias(requireContext(), idUsuario, "receita", (e, categorias) -> {
            if (e != null || categorias == null) {
                Toast.makeText(requireContext(), "Falha ao carregar categorias.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> nomesCategorias = new ArrayList<>();
            for (String[] item : categorias) nomesCategorias.add(item[1]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.spinner_item,
                    nomesCategorias
            );


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategoria.setAdapter(adapter);

            spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                    String idCategoriaSelecionada = categorias.get(position)[0];
                    spCategoria.setTag(idCategoriaSelecionada);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });

        // Inserindo numeros no spiner de repeticao
        ArrayList<String> numeros = new ArrayList<>();
        numeros.add("1x (Não repetir)"); // Primeira opção
        for (int i = 2; i <= 24; i++) {
            numeros.add(i + " Meses");
        }

        // Configura adapter
        ArrayAdapter<String> adapterRepeticao = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                numeros
        );
        adapterRepeticao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRepeticao.setAdapter(adapterRepeticao);


        btnSalvar.setOnClickListener(v -> {
            String nomeReceita = txtNomeReceita.getText().toString().trim();
            String valorReceita = txtValorReceita.getText().toString().trim();
            String dataReceita = txtDataReceita.getText().toString().trim();
            String idCategoria = (String) spCategoria.getTag();

            if (valorReceita.isEmpty()) {
                Toast.makeText(requireContext(), "Digite o valor da receita.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idCategoria == null) {
                Toast.makeText(requireContext(), "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pega o valor de repeticoes selecionado
            int repeticoes = 1;
            try {
                Object sel = spRepeticao.getSelectedItem();
                if (sel != null) {
                    String selStr = sel.toString().trim(); // ex: "1x (Não repetir)" ou "3 Meses"
                    // tenta extrair número no começo da string
                    String digits = selStr.replaceAll("^\\D*(\\d+).*$", "$1");
                    repeticoes = Integer.parseInt(digits);
                } else {
                    repeticoes = spRepeticao.getSelectedItemPosition() + 1;
                }
            } catch (Exception ex) {
                repeticoes = spRepeticao.getSelectedItemPosition() + 1;
            }

            clsReceitas.inserirReceita(
                    requireActivity(),
                    repeticoes,
                    idUsuario,
                    idCategoria,
                    valorReceita,
                    nomeReceita,
                    dataReceita,
                    new clsReceitas.ReceitaCallback() {
                        @Override
                        public void onSucesso(String mensagem, int quantidadeInserida) {
                            Log.d("SUPABASE", "Inserido: " + quantidadeInserida);

                            calcularSaldo();
                            carregarReceitasRecentes();

                        }

                        @Override
                        public void onErro(String erro) {
                            Log.e("SUPABASE", "Erro: " + erro);
                        }
                    }
            );

            dialog.dismiss();


        });
    }

    private void exibirPopupDespesa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_despesa, null);
        builder.setView(view);

        EditText txtNomeDespesa = view.findViewById(R.id.txtNomeDespesa);
        EditText txtValorDespesa = view.findViewById(R.id.txtValorDespesa);
        EditText txtDataDespesa = view.findViewById(R.id.txtDataDespesa);

        aplicarValidacaoData(txtDataDespesa);

        Button btnSalvar = view.findViewById(R.id.btnSalvar);
        Spinner spCategoria = view.findViewById(R.id.spTipoDespesa);
        Spinner spRepeticao = view.findViewById(R.id.spRepeticao);
        ImageView imgSetaCategoria = view.findViewById(R.id.imgCategorias);

        AnimacaoUtils.configurarSetaSpinner(spCategoria, imgSetaCategoria);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        clsMetodos.buscarCategorias(requireContext(), idUsuario, "gasto", (e, categorias) -> {
            if (e != null || categorias == null) {
                Toast.makeText(requireContext(), "Falha ao carregar categorias.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> nomesCategorias = new ArrayList<>();
            for (String[] item : categorias) nomesCategorias.add(item[1]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.spinner_item,
                    nomesCategorias
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategoria.setAdapter(adapter);

            spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                    String idCategoriaSelecionada = categorias.get(position)[0];
                    spCategoria.setTag(idCategoriaSelecionada);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });

        // Inserindo numeros no spiner de repeticao
        ArrayList<String> numeros = new ArrayList<>();
        numeros.add("1x (Não repetir)"); // Primeira opção
        for (int i = 2; i <= 24; i++) {
            numeros.add(i + " Meses");
        }

        // Configura adapter
        ArrayAdapter<String> adapterRepeticao = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                numeros
        );
        adapterRepeticao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRepeticao.setAdapter(adapterRepeticao);


        btnSalvar.setOnClickListener(v -> {
            String nomeDespesa = txtNomeDespesa.getText().toString().trim();
            String valorDespesa = txtValorDespesa.getText().toString().trim();
            String dataDespesa = txtDataDespesa.getText().toString().trim();
            String idCategoria = (String) spCategoria.getTag();

            if (valorDespesa.isEmpty()) {
                Toast.makeText(requireContext(), "Digite o valor da despesa.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idCategoria == null) {
                Toast.makeText(requireContext(), "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pega o valor de repeticoes selecionado
            int repeticoes = 1;
            try {
                Object sel = spRepeticao.getSelectedItem();
                if (sel != null) {
                    String selStr = sel.toString().trim(); // ex: "1x (Não repetir)" ou "3 Meses"
                    // tenta extrair número no começo da string
                    String digits = selStr.replaceAll("^\\D*(\\d+).*$", "$1");
                    repeticoes = Integer.parseInt(digits);
                } else {
                    repeticoes = spRepeticao.getSelectedItemPosition() + 1;
                }
            } catch (Exception ex) {
                repeticoes = spRepeticao.getSelectedItemPosition() + 1;
            }


            clsDespesas.inserirDespesa(
                    requireActivity(),
                    repeticoes,
                    idUsuario,
                    idCategoria,
                    valorDespesa,
                    nomeDespesa,
                    dataDespesa);

            calcularSaldo();
            carregarReceitasRecentes();
            dialog.dismiss();
        });
    }

    private void calcularSaldo() {
        clsMetodos.buscarSaldo(requireContext(), idUsuario, receitas -> {
            clsMetodos.buscarDespesas(requireContext(), idUsuario, despesas -> {
                txtReceitasHome.setText("R$" + receitas);
                txtDespesasHome.setText("R$" + despesas);

                double saldoFinal = receitas - despesas;
                String saldoFormatado = String.format("R$ %.2f", saldoFinal);

                txtSaldoAtual.setText(saldoFormatado);
            });
        });
    }

    // Método utilitário para converter Base64 em Bitmap
    private Bitmap getBitmapFromBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void aplicarValidacaoData(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String input = s.toString();

                if (input.length() < current.length()) {
                    current = input;
                    return;
                }

                String clean = input.replaceAll("[^\\d]", "");

                if (clean.isEmpty()) {
                    current = "";
                    isUpdating = true;
                    editText.setText("");
                    return;
                }

                StringBuilder formatted = new StringBuilder();
                int length = clean.length();

                if (length >= 1) {
                    String diaStr = clean.substring(0, Math.min(2, length));
                    int dia = Integer.parseInt(diaStr);

                    if (diaStr.length() == 1) {
                        if (dia > 3) {
                            formatted.append("0").append(dia);
                            if (length > 1) formatted.append("/");
                        } else {
                            formatted.append(diaStr);
                        }
                    } else {
                        if (dia == 0) dia = 1;
                        if (dia > 31) dia = 31;
                        formatted.append(String.format("%02d", dia));
                        if (length > 2) formatted.append("/");
                    }
                }

                if (length >= 3) {
                    String mesStr = clean.substring(2, Math.min(4, length));
                    int mes = Integer.parseInt(mesStr);

                    if (mesStr.length() == 1) {
                        if (mes > 1) {
                            formatted.append("0").append(mes);
                            if (length > 3) formatted.append("/");
                        } else {
                            formatted.append(mesStr);
                        }
                    } else {
                        if (mes == 0) mes = 1;
                        if (mes > 12) mes = 12;
                        formatted.append(String.format("%02d", mes));
                        if (length > 4) formatted.append("/");
                    }
                }

                if (length >= 5) {
                    String ano = clean.substring(4, Math.min(8, length));
                    formatted.append(ano);

                    if (length == 8) {
                        try {
                            String diaFinal = clean.substring(0, 2);
                            String mesFinal = clean.substring(2, 4);
                            String anoFinal = clean.substring(4, 8);

                            int d = Integer.parseInt(diaFinal);
                            int m = Integer.parseInt(mesFinal);
                            int a = Integer.parseInt(anoFinal);

                            int diaMaximo = getDiaMaximoDoMes(m, a);

                            if (d > diaMaximo) {
                                d = diaMaximo;
                                formatted = new StringBuilder(String.format("%02d/%02d/%s", d, m, anoFinal));
                            }
                        } catch (Exception e) {
                            // Ignora erros
                        }
                    }
                }

                current = formatted.toString();
                isUpdating = true;
                editText.setText(current);
                editText.setSelection(current.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}

            private int getDiaMaximoDoMes(int mes, int ano) {
                switch (mes) {
                    case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                        return 31;
                    case 4: case 6: case 9: case 11:
                        return 30;
                    case 2:
                        boolean bissexto = (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0);
                        return bissexto ? 29 : 28;
                    default:
                        return 31;
                }
            }
        });
    }
}