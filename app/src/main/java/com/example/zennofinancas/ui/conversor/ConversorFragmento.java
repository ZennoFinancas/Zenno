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
// Removida a importação 'TelaConversao', pois não é necessária para o Contexto.

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConversorFragmento extends Fragment {

    // Argumentos boilerplate (pode manter ou remover se não usar)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // Declaração das Views
    private EditText txtValor;
    private Spinner spinnerMoedaOrigem;
    private Spinner spinnerMoedaDestino;
    private Button btnConverter;
    // 'BtnEsqueceu' foi declarado mas nunca usado/inicializado no seu código original.
    // Se não for usar, remova a declaração. Se for, inicialize em onViewCreated.
    private TextView lblResultado;

    public ConversorFragmento() {
        // Required empty public constructor
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
        // O 'onCreate' é usado para lógica que não envolve a View,
        // como ler argumentos.
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODA A LÓGICA DE VIEW FOI MOVIDA DAQUI
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 'onCreateView' apenas infla o layout e o retorna.
        return inflater.inflate(R.layout.fragmento_conversor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // AGORA SIM: A 'view' existe e podemos inicializar os componentes.
        // Use o 'view' fornecido para encontrar os IDs.
        txtValor = view.findViewById(R.id.txtValor);
        spinnerMoedaOrigem = view.findViewById(R.id.spinnerMoedaOrigem);
        spinnerMoedaDestino = view.findViewById(R.id.spinnerMoedaDestino);
        btnConverter = view.findViewById(R.id.btnSalvarAlteracoes);
        lblResultado = view.findViewById(R.id.lblResultado);

        // Configuração do Spinner
        Moeda[] moedas = {
                new Moeda("USD", "Dólar Americano \uD83C\uDDFA\uD83C\uDDF8 "),
                new Moeda("EUR", "Euro \uD83C\uDDEA\uD83C\uDDFA"),
                new Moeda("BRL", "Real Brasileiro \uD83C\uDDE7\uD83C\uDDF7"),
                new Moeda("JPY", "Iene Japonês \uD83C\uDDEF\uD83C\uDDF5"),
                new Moeda("GBP", "Libra Esterlina \uD83C\uDDEC\uD83C\uDDE7"),
                new Moeda("AUD", "Dólar Australiano \uD83C\uDFF4\uDB40\uDC61\uDB40\uDC75\uDB40\uDC73\uDB40\uDC61\uDB40\uDC7F"),
                new Moeda("CAD", "Dólar Canadense \uD83C\uDDE8\uD83C\uDDE6  "),
                new Moeda("CHF", "Franco Suíço \uD83C\uDDE8\uD83C\uDDED"),
                new Moeda("CNY", "Yuan Chinês \uD83C\uDDE8\uD83C\uDDF3"),
                new Moeda("NZD", "Dólar Neozelandês \uD83C\uDDF3\uD83C\uDDFF")
        };

        // Use 'requireContext()' para obter o Contexto do Fragmento
        ArrayAdapter<Moeda> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, moedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoedaOrigem.setAdapter(adapter);
        spinnerMoedaDestino.setAdapter(adapter);

        // Configuração do Listener
        btnConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Moeda moedaOrigem = (Moeda) spinnerMoedaOrigem.getSelectedItem();
                Moeda moedaDestino = (Moeda) spinnerMoedaDestino.getSelectedItem();

                String origem = moedaOrigem.getCodigo();
                String destino = moedaDestino.getCodigo();

                String valorStr = txtValor.getText().toString().trim();
                if (valorStr.isEmpty()) {
                    // Use 'requireContext()' para o Toast
                    Toast.makeText(requireContext(), "Digite um valor", Toast.LENGTH_SHORT).show();
                    return;
                }

                double valor;
                try {
                    valor = Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Digite um número válido!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://economia.awesomeapi.com.br/json/last/" + origem + "-" + destino;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        // Use 'requireActivity().runOnUiThread()' para atualizar a UI
                        if (isAdded()) { // Garante que o fragmento ainda está anexado
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Erro ao acessar API", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String jsonData = response.body().string();
                            // Use 'requireActivity().runOnUiThread()'
                            if (isAdded()) { // Garante que o fragmento ainda está anexado
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        JSONObject json = new JSONObject(jsonData);
                                        String key = origem + destino;
                                        double taxa = json.getJSONObject(key).getDouble("bid");
                                        double resultado = valor * taxa;
                                        lblResultado.setText(String.format("%.2f %s = %.2f %s", valor, origem, resultado, destino));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(requireContext(), "Erro ao processar dados", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
}