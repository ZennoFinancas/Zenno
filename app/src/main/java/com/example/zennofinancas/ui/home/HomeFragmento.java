package com.example.zennofinancas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zennofinancas.R;
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

public class HomeFragmento extends Fragment { // Permanece como Fragment

    private TextView lblSaldoAtual;
    private EditText txtReceita, txtDespesa;
    private ImageView imgAddReceita, imgAddDespesa;


    private final OkHttpClient client = new OkHttpClient();

    private static final String SUPABASE_URL = "";
    private static final String SUPABASE_KEY = "";
    private static final String USER_ID = "";
    private static final String USER_TOKEN = "";

    private final String API_PERFIS_ENDPOINT = SUPABASE_URL + "/rest/v1/perfis";
    private final String API_RPC_ENDPOINT = SUPABASE_URL + "/rest/v1/rpc/modificar_saldo";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout XML e o retorna. O binding não é mais usado.
        return inflater.inflate(R.layout.fragmento_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lblSaldoAtual = view.findViewById(R.id.lblSaldoAtual);
        txtReceita = view.findViewById(R.id.txtReceita);
        txtDespesa = view.findViewById(R.id.txtDespesa);
        imgAddReceita = view.findViewById(R.id.imgAddReceita);
        imgAddDespesa = view.findViewById(R.id.imgAddDespesa);

        carregarSaldo();

        imgAddReceita.setOnClickListener(v -> {
            String valorTexto = txtReceita.getText().toString();
            adicionarTransacao(valorTexto, "receita");
        });

        imgAddDespesa.setOnClickListener(v -> {
            String valorTexto = txtDespesa.getText().toString();
            adicionarTransacao(valorTexto, "despesa");
        });
    }

    private void carregarSaldo() {
        if (USER_ID.isEmpty() || USER_TOKEN.isEmpty()) {
            mostrarToast("Configure o ID e Token do usuário no código!");
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
                            double saldo = perfil.getDouble("saldo");
                            // Em um Fragment, usamos getActivity() para rodar na UI thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> atualizarTextoSaldo(saldo));
                            }
                        } else {
                            mostrarToast("Perfil de usuário não encontrado.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarToast("Erro ao processar dados do saldo.");
                    }
                }
            }
        });
    }

    private void adicionarTransacao(String valorTexto, String tipo) {
        if (valorTexto.isEmpty()) {
            mostrarToast("Por favor, insira um valor.");
            return;
        }
        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
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
                    final String resultado = response.body().string();
                    try {
                        double novoSaldo = Double.parseDouble(resultado);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                atualizarTextoSaldo(novoSaldo);
                                if (tipo.equals("receita")) {
                                    txtReceita.getText().clear();
                                } else {
                                    txtDespesa.getText().clear();
                                }
                                String tipoCapitalizado = tipo.substring(0, 1).toUpperCase() + tipo.substring(1);
                                Toast.makeText(getContext(), tipoCapitalizado + " adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (NumberFormatException e) {
                        mostrarToast("Erro ao processar a resposta do servidor.");
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

    private void mostrarToast(String mensagem) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), mensagem, Toast.LENGTH_LONG).show());
        }
    }

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