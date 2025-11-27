package com.example.zennofinancas.ui.conversor;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

// IMPORTANTE: Importe a classe que acabamos de criar
import com.example.zennofinancas.classes.AnimacaoUtils;
import com.example.zennofinancas.Moeda;
import com.example.zennofinancas.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConversorFragmento extends Fragment {

    private EditText txtQuantiaConversor;
    private EditText txtResultadoConversor;
    private Spinner spMoedaOrigem;
    private Spinner spMoedaDestino;
    private ImageView imgConversor; // Botão de inverter
    private ImageView imgSetaOrigem; // Seta do Spinner de cima
    private ImageView imgSetaDestino; // Seta do Spinner de baixo
    private Button btnConverter;
    private TextView txtResultadoFinal;

    public ConversorFragmento() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_conversor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Vincular componentes
        txtQuantiaConversor = view.findViewById(R.id.txtQuantiaConversor);
        txtResultadoConversor = view.findViewById(R.id.txtResultadoConversor);
        spMoedaOrigem = view.findViewById(R.id.spMoeda);
        spMoedaDestino = view.findViewById(R.id.spMoedaDestino);
        imgConversor = view.findViewById(R.id.imgConversor);

        // Vinculando as setinhas para passar para a classe de animação
        imgSetaOrigem = view.findViewById(R.id.imgSetaMoeda);
        imgSetaDestino = view.findViewById(R.id.imgSetaMoedaDestino);

        btnConverter = view.findViewById(R.id.btnConverter);
        txtResultadoFinal = view.findViewById(R.id.txtResultado);

        // 2. Criar a lista de moedas
        List<Moeda> listaMoedas = criarListaMoedas();

        // 3. Configurar o Adapter Customizado
        MoedaAdapter adapter = new MoedaAdapter(requireContext(), listaMoedas);
        spMoedaOrigem.setAdapter(adapter);
        spMoedaDestino.setAdapter(adapter);

        // Definir seleção padrão: BRL (0) -> USD (1)
        if (listaMoedas.size() > 1) {
            spMoedaOrigem.setSelection(0);
            spMoedaDestino.setSelection(1);
        }

        // 4. Ação do Botão Converter
        btnConverter.setOnClickListener(v -> realizarConversao());

        // 5. Ação do Botão de Inverter (Setas centrais)
        imgConversor.setOnClickListener(v -> {
            // Lógica de troca de posição
            int posicaoOrigem = spMoedaOrigem.getSelectedItemPosition();
            int posicaoDestino = spMoedaDestino.getSelectedItemPosition();

            spMoedaOrigem.setSelection(posicaoDestino);
            spMoedaDestino.setSelection(posicaoOrigem);

            // Lógica de limpeza visual
            txtResultadoFinal.setVisibility(View.INVISIBLE);
            txtResultadoConversor.setText("");

            // Animação do botão central
            imgConversor.animate().cancel(); // Cancela anterior para não bugar

            // Lógica de "vai e volta" para o botão central também
            float rotacaoAtual = imgConversor.getRotation() % 360;
            float novaRotacao = (Math.abs(rotacaoAtual) < 90f) ? 180f : 0f;

            imgConversor.animate()
                    .rotation(novaRotacao)
                    .setDuration(300)
                    .withLayer()
                    .start();
        });

        // 6. Animação das Setas dos Spinners usando a Classe Utilitária
        // Isso aplica a lógica inteligente criada no AnimacaoUtils
        AnimacaoUtils.configurarSetaSpinner(spMoedaOrigem, imgSetaOrigem);
        AnimacaoUtils.configurarSetaSpinner(spMoedaDestino, imgSetaDestino);
    }

    private List<Moeda> criarListaMoedas() {
        List<Moeda> lista = new ArrayList<>();

        lista.add(new Moeda("BRL", "Real Brasileiro", R.drawable.flag_brl));
        lista.add(new Moeda("USD", "Dólar Americano", R.drawable.flag_usd));
        lista.add(new Moeda("EUR", "Euro", R.drawable.flag_eur));
        lista.add(new Moeda("BTC", "Bitcoin", R.drawable.flag_btc));
        lista.add(new Moeda("ETH", "Ethereum", R.drawable.flag_eth));
        lista.add(new Moeda("XRP", "XRP (Ripple)", R.drawable.flag_xrp));
        lista.add(new Moeda("LTC", "Litecoin", R.drawable.flag_ltc));
        lista.add(new Moeda("JPY", "Iene Japonês", R.drawable.flag_jpy));
        lista.add(new Moeda("GBP", "Libra Esterlina", R.drawable.flag_gbp));
        lista.add(new Moeda("CHF", "Franco Suíço", R.drawable.flag_chf));
        lista.add(new Moeda("CAD", "Dólar Canadense", R.drawable.flag_cad));
        lista.add(new Moeda("AUD", "Dólar Australiano", R.drawable.flag_aud));
        lista.add(new Moeda("ARS", "Peso Argentino", R.drawable.flag_ars));
        lista.add(new Moeda("CLP", "Peso Chileno", R.drawable.flag_clp));
        lista.add(new Moeda("UYU", "Peso Uruguaio", R.drawable.flag_uyu));
        lista.add(new Moeda("COP", "Peso Colombiano", R.drawable.flag_cop));
        lista.add(new Moeda("PYG", "Guarani Paraguaio", R.drawable.flag_pyg));
        lista.add(new Moeda("MXN", "Peso Mexicano", R.drawable.flag_mxn));
        lista.add(new Moeda("PEN", "Sol Peruano", R.drawable.flag_pen));
        lista.add(new Moeda("BOB", "Boliviano", R.drawable.flag_bob));
        lista.add(new Moeda("SEK", "Coroa Sueca", R.drawable.flag_sek));
        lista.add(new Moeda("NOK", "Coroa Norueguesa", R.drawable.flag_nok));
        lista.add(new Moeda("DKK", "Coroa Dinamarquesa", R.drawable.flag_dkk));
        lista.add(new Moeda("RUB", "Rublo Russo", R.drawable.flag_rub));
        lista.add(new Moeda("CNY", "Yuan Chinês", R.drawable.flag_cny));
        lista.add(new Moeda("NZD", "Dólar Neozelandês", R.drawable.flag_nzd));
        lista.add(new Moeda("HKD", "Dólar de Hong Kong", R.drawable.flag_hkd));
        lista.add(new Moeda("SGD", "Dólar de Cingapura", R.drawable.flag_sgd));
        lista.add(new Moeda("ILS", "Novo Shekel Israelense", R.drawable.flag_ils));
        lista.add(new Moeda("TRY", "Lira Turca", R.drawable.flag_try));
        lista.add(new Moeda("ZAR", "Rand Sul-Africano", R.drawable.flag_zar));

        return lista;
    }

    private void realizarConversao() {
        Moeda moedaOrigem = (Moeda) spMoedaOrigem.getSelectedItem();
        Moeda moedaDestino = (Moeda) spMoedaDestino.getSelectedItem();

        String origem = moedaOrigem.getCodigo();
        String destino = moedaDestino.getCodigo();
        String valorStr = txtQuantiaConversor.getText().toString().trim();

        if (valorStr.isEmpty()) {
            Toast.makeText(requireContext(), "Digite um valor", Toast.LENGTH_SHORT).show();
            return;
        }

        double valorInserido;
        try {
            valorInserido = Double.parseDouble(valorStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Número inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (origem.equals(destino)) {
            mostrarResultado(valorInserido, valorInserido, origem, destino);
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

                                double taxaOrigem = 1.0;
                                if (!origem.equals("BRL")) {
                                    taxaOrigem = json.getJSONObject(origem + "BRL").getDouble("bid");
                                }

                                double taxaDestino = 1.0;
                                if (!destino.equals("BRL")) {
                                    taxaDestino = json.getJSONObject(destino + "BRL").getDouble("bid");
                                }

                                double valorEmReais = valorInserido * taxaOrigem;
                                double resultadoFinal = valorEmReais / taxaDestino;

                                mostrarResultado(valorInserido, resultadoFinal, origem, destino);

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

    private void mostrarResultado(double valorEntrada, double valorSaida, String origem, String destino) {
        boolean origemIsCripto = ehCripto(origem);
        boolean destinoIsCripto = ehCripto(destino);

        String formatoOrigem = origemIsCripto ? "%.8f" : "%.2f";
        String formatoDestino = destinoIsCripto ? "%.8f" : "%.2f";

        txtResultadoConversor.setText(String.format(formatoDestino, valorSaida));

        String textoFinal = String.format(formatoOrigem + " %s = " + formatoDestino + " %s",
                valorEntrada, origem, valorSaida, destino);
        txtResultadoFinal.setText(textoFinal);

        // Faz o resultado aparecer
        txtResultadoFinal.setVisibility(View.VISIBLE);
    }

    private boolean ehCripto(String codigo) {
        return codigo.equals("BTC") || codigo.equals("ETH") || codigo.equals("XRP") || codigo.equals("LTC");
    }

    // --- ADAPTER CUSTOMIZADO ---
    private class MoedaAdapter extends ArrayAdapter<Moeda> {
        public MoedaAdapter(Context context, List<Moeda> moedas) {
            super(context, 0, moedas);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent, Color.WHITE);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent, Color.BLACK);
        }

        private View initView(int position, View convertView, ViewGroup parent, int corTexto) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_moeda, parent, false);
            }

            ImageView imgBandeira = convertView.findViewById(R.id.imgBandeiraItem);
            TextView txtNome = convertView.findViewById(R.id.txtCodigoItem);

            Moeda item = getItem(position);

            if (item != null) {
                if (item.getBandeiraResId() != 0) {
                    imgBandeira.setImageResource(item.getBandeiraResId());
                    imgBandeira.setVisibility(View.VISIBLE);
                } else {
                    imgBandeira.setVisibility(View.GONE);
                }

                txtNome.setText(item.getNome());
                txtNome.setTextColor(corTexto);
            }

            return convertView;
        }
    }
}