package com.example.zennofinancas.ui.home;

import android.app.AlertDialog;

import com.example.zennofinancas.TelaMetas;
import com.example.zennofinancas.TelaMeuPerfil;
import com.example.zennofinancas.TelaNotificacoes;
import com.example.zennofinancas.classes.clsDadosUsuario;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.zennofinancas.R;
import com.example.zennofinancas.clsMetodos;

import java.util.ArrayList;

public class HomeFragmento extends Fragment {

    TextView txtSaldoAtual, txtReceitasHome, txtDespesasHome;

    ImageView btnAddReceita, btnAddDespesa, btnMetas, imgFotoMetas;

    private String idUsuario;



    // Verificando se o usuário está logado


    // Instanciando classe do Banco de Dados
    clsMetodos supabase = new clsMetodos();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Variaveis de controle
        String totalReceitas = "";


        txtSaldoAtual = view.findViewById(R.id.txtSaldoAtual);
        txtReceitasHome = view.findViewById(R.id.txtReceitasHome);
        txtDespesasHome = view.findViewById(R.id.txtDespesasHome);
        btnAddReceita = view.findViewById(R.id.btnReceitasHome);
        btnAddDespesa = view.findViewById(R.id.btnDespesasHome);
        btnMetas = view.findViewById(R.id.Metas);
        imgFotoMetas = view.findViewById(R.id.imgFotoMetas);

        //carregarSaldo();




        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(requireContext());
        if (usuario != null) {
            idUsuario = usuario.getIdUsuario();
        } else {
            Toast.makeText(requireContext(), "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Busca o total de receitas cadastradas
        calcularSaldo();




        btnMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent metas = new Intent(getActivity(), TelaMetas.class);
                startActivity(metas);
            }
        });


        // Evento para add receita. Constrói um Alert Dialog com os campos requeridos
        btnAddReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirPopupReceita();

            }
        });

        imgFotoMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgFotoMetas = new Intent(getActivity(), TelaMeuPerfil.class);
                startActivity(imgFotoMetas);
            }
        });


        // Evento para add despesa. Constrói um Alert Dialog com os campos requeridos
        btnAddDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirPopupDespesa();
            }
        });

    }

    private void exibirPopupReceita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_receita, null);
        builder.setView(view);

        // Campos do layout
        EditText txtNomeReceita = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorReceita = view.findViewById(R.id.txtValorReceita);
        EditText txtDataReceita = view.findViewById(R.id.txtData);

        txtDataReceita.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", "");

                StringBuilder formatted = new StringBuilder();
                int len = str.length();

                if (len > 0) {
                    formatted.append(str.substring(0, Math.min(2, len)));
                    if (len >= 3) {
                        formatted.append("/").append(str.substring(2, Math.min(4, len)));
                    }
                    if (len >= 5) {
                        formatted.append("/").append(str.substring(4, Math.min(8, len)));
                    }
                }

                isUpdating = true;
                txtDataReceita.setText(formatted.toString());
                txtDataReceita.setSelection(formatted.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button btnSalvar = view.findViewById(R.id.btnSalvar);

        Spinner spCategoria = view.findViewById(R.id.spCategoria);

        // Cria o diálogo
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // Busca as categorias do banco
        clsMetodos.buscarCategorias(requireContext(), idUsuario, "receita", (e, categorias) -> {
            if (e != null || categorias == null) {
                Toast.makeText(requireContext(), "Falha ao carregar categorias.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> nomesCategorias = new ArrayList<>();
            for (String[] item : categorias) nomesCategorias.add(item[1]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
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
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        });


        // Eventos dos botões
        btnSalvar.setOnClickListener(v -> {
            String nomeReceita = txtNomeReceita.getText().toString().trim();
            String valorReceita = txtValorReceita.getText().toString().trim();
            String dataReceita = txtDataReceita.getText().toString().trim();
            String idCategoria = (String) spCategoria.getTag(); // pegamos o ID da categoria selecionada


            if (valorReceita.isEmpty()) {
                Toast.makeText(requireContext(), "Digite o valor da receita.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idCategoria == null) {
                Toast.makeText(requireContext(), "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
                return;
            }

            //
            supabase.inserirReceita(
                    requireActivity(),
                    idUsuario,
                    idCategoria,
                    valorReceita,
                    nomeReceita,
                    dataReceita);

            calcularSaldo();

            dialog.dismiss();
        });


    }

    private void exibirPopupDespesa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_receita, null);
        builder.setView(view);

        // Campos do layout
        EditText txtNomeReceita = view.findViewById(R.id.txtNomeReceita);
        EditText txtValorReceita = view.findViewById(R.id.txtValorReceita);


        Button btnSalvar = view.findViewById(R.id.btnSalvar);

        // Cria o diálogo
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // Eventos dos botões
        btnSalvar.setOnClickListener(v -> {
            String nomeReceita = txtNomeReceita.getText().toString().trim();
            String valorReceita = txtValorReceita.getText().toString().trim();

            supabase.inserirReceita(getActivity(), "22", "1", "210", "Ifood", "");
            Toast.makeText(requireContext(), "Salvo: " + nomeReceita + " - " + valorReceita, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });


    }


    private void calcularSaldo() {

        clsMetodos.buscarSaldo(requireContext(), idUsuario, receitas -> {

            clsMetodos.buscarDespesas(requireContext(), idUsuario, despesas -> {

                // Exibe o total de receitas
                txtReceitasHome.setText("R$" + receitas);
                // Exibe o total de despesas
                txtDespesasHome.setText("R$" + despesas);


                double saldoFinal = receitas - despesas;
                String saldoFormatado = String.format("R$ %.2f", saldoFinal);



                // Exibe o saldo final
                txtSaldoAtual.setText(saldoFormatado);
            });

        });
    }


}