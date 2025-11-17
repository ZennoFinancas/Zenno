package com.example.zennofinancas.ui.home;

import android.app.AlertDialog;

import com.example.zennofinancas.TelaMetas;
import com.example.zennofinancas.classes.clsDadosUsuario;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    TextView txtSaldoAtual;

    ImageView btnAddReceita, btnAddDespesa, btnMetas;

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
        btnAddReceita = view.findViewById(R.id.btnReceitasHome);
        btnAddDespesa = view.findViewById(R.id.btnDespesasHome);
        btnMetas = view.findViewById(R.id.Metas);

        //carregarSaldo();




        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(requireContext());
        if (usuario != null) {
            idUsuario = usuario.getIdUsuario();
        } else {
            Toast.makeText(requireContext(), "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Busca o total de receitas cadastradas
        clsMetodos.buscarSaldo(getContext(),idUsuario);

        // Recebe os valores enviados via Intent
        Bundle intent = requireActivity().getIntent().getExtras();

        if (intent != null) {
            totalReceitas = intent.getString("totalReceitas");
        }

        txtSaldoAtual.setText("R$" + totalReceitas);


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
                    "");

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




    /*
    private void carregarSaldo() {
        if (USER_ID.isEmpty() || USER_TOKEN.isEmpty() || SUPABASE_URL.isEmpty() || SUPABASE_KEY.isEmpty()) {
            mostrarToast("Configure as credenciais do Supabase e dados do usuário no código!");
            return;
        }

        String url = API_PERFIS_ENDPOINT + "?select=saldo&id=eq." + USER_ID;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + USER_TOKEN)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                mostrarToast("Falha ao carregar saldo: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        if (jsonArray.length() > 0) {
                            JSONObject perfil = jsonArray.getJSONObject(0);
                            final double saldo = perfil.getDouble("saldo");
                            if (getActivity() != null) {
                                // CORRIGIDO: Convertido para sintaxe antiga (sem lambda)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        atualizarTextoSaldo(saldo);
                                    }
                                });
                            }
                        } else {
                            mostrarToast("Perfil de usuário não encontrado.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarToast("Erro ao processar dados do saldo.");
                    }
                } else {
                    mostrarToast("Erro ao carregar saldo: " + response.message());
                }
            }
        });
    }

    private void adicionarTransacao(final String valorTexto, final String tipo) {
        if (valorTexto.isEmpty()) {
            mostrarToast("Por favor, insira um valor.");
            return;
        }
        double valor;
        try {
            valor = Double.parseDouble(valorTexto.replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarToast("Valor inválido.");
            return;
        }
        if (valor <= 0) {
            mostrarToast("O valor deve ser positivo.");
            return;
        }

        if (tipo.equals("despesa")) {
            valor = -valor;
        }

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("valor_transacao", valor);
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(API_RPC_ENDPOINT)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + USER_TOKEN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                mostrarToast("Erro ao salvar transação: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (getActivity() != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tipo.equals("receita")) {
                                    txtReceita.getText().clear();
                                } else {
                                    txtDespesa.getText().clear();
                                }
                                String tipoCapitalizado = tipo.substring(0, 1).toUpperCase() + tipo.substring(1);
                                Toast.makeText(getContext(), tipoCapitalizado + " adicionada com sucesso!", Toast.LENGTH_SHORT).show();

                                carregarSaldo();
                            }
                        });
                    }
                } else {
                    mostrarToast("Erro na transação: " + response.message());
                }
            }
        });
    }

    private void atualizarTextoSaldo(double valor) {
        Locale localeBR = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localeBR);
        if (lblSaldoAtual != null) {
            lblSaldoAtual.setText(formatoMoeda.format(valor));
        }
    }

    private void mostrarToast(final String mensagem) {
        if (getActivity() != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(gzetContext(), mensagem, Toast.LENGTH_LONG).show();
                }
            });
        }
    }*/

}