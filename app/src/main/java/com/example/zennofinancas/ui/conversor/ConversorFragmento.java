package com.example.zennofinancas.ui.conversor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zennofinancas.Moeda;
import com.example.zennofinancas.R;

import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConversorFragmento extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private EditText txtValor;
    private Spinner spinnerMoedaOrigem;
    private Spinner spinnerMoedaDestino;
    private Button btnConverter;
    private TextView lblResultado;

    public ConversorFragmento() {

    }

    public static ConversorFragmento newInstance(String param1, String param2) {
        ConversorFragmento fragment = new ConversorFragmento();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_conversor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtValor = view.findViewById(R.id.txtValor);
        spinnerMoedaOrigem = view.findViewById(R.id.spinnerMoedaOrigem);
        spinnerMoedaDestino = view.findViewById(R.id.spinnerMoedaDestino);
        btnConverter = view.findViewById(R.id.btnSalvarAlteracoes);
        lblResultado = view.findViewById(R.id.lblResultado);

        Moeda[] moedas = {
                new Moeda("BRL", "Real Brasileiro"),
                new Moeda("USD", "Dólar Americano"),
                new Moeda("EUR", "Euro"),
                new Moeda("BTC", "Bitcoin"),
                new Moeda("ETH", "Ethereum"),
                new Moeda("XRP", "XRP (Ripple)"),
                new Moeda("LTC", "Litecoin"),
                new Moeda("JPY", "Iene Japonês"),
                new Moeda("GBP", "Libra Esterlina"),
                new Moeda("CHF", "Franco Suíço"),
                new Moeda("CAD", "Dólar Canadense"),
                new Moeda("AUD", "Dólar Australiano"),
                new Moeda("ARS", "Peso Argentino"),
                new Moeda("CLP", "Peso Chileno"),
                new Moeda("UYU", "Peso Uruguaio"),
                new Moeda("COP", "Peso Colombiano"),
                new Moeda("PYG", "Guarani Paraguaio"),
                new Moeda("MXN", "Peso Mexicano"),
                new Moeda("PEN", "Sol Peruano"),
                new Moeda("BOB", "Boliviano"),
                new Moeda("SEK", "Coroa Sueca"),
                new Moeda("NOK", "Coroa Norueguesa"),
                new Moeda("DKK", "Coroa Dinamarquesa"),
                new Moeda("RUB", "Rublo Russo"),
                new Moeda("CNY", "Yuan Chinês"),
                new Moeda("NZD", "Dólar Neozelandês"),
                new Moeda("HKD", "Dólar de Hong Kong"),
                new Moeda("SGD", "Dólar de Cingapura"),
                new Moeda("ILS", "Novo Shekel Israelense"),
                new Moeda("TRY", "Lira Turca"),
                new Moeda("ZAR", "Rand Sul-Africano")
        };

        ArrayAdapter<Moeda> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, moedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoedaOrigem.setAdapter(adapter);
        spinnerMoedaDestino.setAdapter(adapter);

        btnConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Moeda moedaOrigem = (Moeda) spinnerMoedaOrigem.getSelectedItem();
                Moeda moedaDestino = (Moeda) spinnerMoedaDestino.getSelectedItem();

                String origem = moedaOrigem.getCodigo();
                String destino = moedaDestino.getCodigo();

                String valorStr = txtValor.getText().toString().trim();

                if (valorStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Digite um valor", Toast.LENGTH_SHORT).show();
                    return;
                }

                double valorInserido;
                try {
                    valorInserido = Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Número inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (origem.equals(destino)) {

                    boolean isCrypto = ehCripto(origem);
                    String formato = isCrypto ? "%.8f" : "%.2f";
                    String formatoFinal = formato + " %s = " + formato + " %s";
                    lblResultado.setText(String.format(formatoFinal, valorInserido, origem, valorInserido, destino));
                    return;
                }

                String paresParaApi = "";
                if (!origem.equals("BRL")) paresParaApi += origem + "-BRL";
                if (!destino.equals("BRL")) {
                    if (!paresParaApi.isEmpty()) paresParaApi += ",";
                    paresParaApi += destino + "-BRL";
                }

                String url = "https://economia.awesomeapi.com.br/json/last/" + paresParaApi;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Erro de conexão", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String jsonData = response.body().string();

                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        JSONObject json = new JSONObject(jsonData);

                                        double taxaOrigemParaBRL = 1.0;
                                        if (!origem.equals("BRL")) {
                                            taxaOrigemParaBRL = json.getJSONObject(origem + "BRL").getDouble("bid");
                                        }

                                        double taxaDestinoParaBRL = 1.0;
                                        if (!destino.equals("BRL")) {
                                            taxaDestinoParaBRL = json.getJSONObject(destino + "BRL").getDouble("bid");
                                        }

                                        double valorEmReais = valorInserido * taxaOrigemParaBRL;
                                        double resultadoFinal = valorEmReais / taxaDestinoParaBRL;


                                        boolean origemIsCripto = ehCripto(origem);
                                        boolean destinoIsCripto = ehCripto(destino);


                                        String formatoOrigem = origemIsCripto ? "%.8f" : "%.2f";
                                        String formatoDestino = destinoIsCripto ? "%.8f" : "%.2f";
                                     String formatoTexto = formatoOrigem + " %s = " + formatoDestino + " %s";

                                        lblResultado.setText(String.format(formatoTexto, valorInserido, origem, resultadoFinal, destino));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(requireContext(), "Erro ao calcular", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }


    private boolean ehCripto(String codigo) {
        return codigo.equals("BTC") || codigo.equals("ETH") || codigo.equals("XRP") || codigo.equals("LTC");
    }
}