package com.example.zennofinancas.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zennofinancas.R;
import com.example.zennofinancas.TelaConversao;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragmento extends Fragment {

    private TextView lblSaldoAtual;
    private EditText txtReceita, txtDespesa;
    private ImageView imgAddReceita, imgAddDespesa;

    Button btnAddReceita;

    private final OkHttpClient client = new OkHttpClient();

    private static final String SUPABASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co";
    private static final String SUPABASE_KEY = "";
    private static final String USER_ID = "";
    private static final String USER_TOKEN = "";

    private final String API_PERFIS_ENDPOINT = SUPABASE_URL + "/rest/v1/perfis";
    private final String API_RPC_ENDPOINT = SUPABASE_URL + "/rest/v1/rpc/modificar_saldo";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lblSaldoAtual = view.findViewById(R.id.lblSaldoAtual);
        txtReceita = view.findViewById(R.id.txtReceita);
        txtDespesa = view.findViewById(R.id.txtDespesa);
        imgAddReceita = view.findViewById(R.id.imgAddReceita);
        btnAddReceita = view.findViewById(R.id.btnAddReceita);
        imgAddDespesa = view.findViewById(R.id.imgAddDespesa);

        //carregarSaldo();


        imgAddReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valorTexto = txtReceita.getText().toString();
                //adicionarTransacao(valorTexto, "receita");
            }
        });


        imgAddDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valorTexto = txtDespesa.getText().toString();
                //adicionarTransacao(valorTexto, "despesa");
            }
        });

        btnAddReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_add_gasto, null);

        EditText edtName = view.findViewById(R.id.edtNome);
        EditText edtEmail = view.findViewById(R.id.edtData);

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Salvar", (dialog, id) -> {
                    String name = edtName.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
                    Toast.makeText(getContext(), mensagem, Toast.LENGTH_LONG).show();
                }
            });
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lblSaldoAtual = null;
        txtReceita = null;
        txtDespesa = null;
        imgAddReceita = null;
        imgAddDespesa = null;
    }
}