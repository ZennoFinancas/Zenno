package com.example.zennofinancas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Intent;


public class TelaConversao extends AppCompatActivity {
    EditText txtValor;
    Spinner spinnerMoedaOrigem;
    Spinner spinnerMoedaDestino;
    Button btnConverter, BtnEsqueceu;
    TextView lblResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_conversao);

        txtValor = findViewById(R.id.txtValor);
        spinnerMoedaOrigem = findViewById(R.id.spinnerMoedaOrigem);
        spinnerMoedaDestino = findViewById(R.id.spinnerMoedaDestino);
        btnConverter = findViewById(R.id.btnConverter);
        lblResultado = findViewById(R.id.lblResultado);

        Moeda[] moedas = {
                new Moeda("USD", "Dólar Americano \uD83C\uDDE7\uD83C\uDDF7"),
                new Moeda("EUR", "Euro"),
                new Moeda("BRL", "Real Brasileiro"),
                new Moeda("JPY", "Iene Japonês"),
                new Moeda("GBP", "Libra Esterlina"),
                new Moeda("AUD", "Dólar Australiano"),
                new Moeda("CAD", "Dólar Canadense"),
                new Moeda("CHF", "Franco Suíço"),
                new Moeda("CNY", "Yuan Chinês"),
                new Moeda("NZD", "Dólar Neozelandês")
        };

        ArrayAdapter<Moeda> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoedaOrigem.setAdapter(adapter);
        spinnerMoedaDestino.setAdapter(adapter);

        btnConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Moeda moedaOrigem = (Moeda) spinnerMoedaOrigem.getSelectedItem();
                Moeda moedaDestino = (Moeda) spinnerMoedaDestino.getSelectedItem();

                String origem = moedaOrigem.getCodigo();
                String destino = moedaDestino.getCodigo();

                String valorStr = txtValor.getText().toString().trim();
                if (valorStr.isEmpty()) {
                    Toast.makeText(TelaConversao.this, "Digite um valor", Toast.LENGTH_SHORT).show();
                    return;
                }

                double valor;
                try {
                    valor = Double.parseDouble(valorStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(TelaConversao.this, "Digite um número válido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Monta a url da api usando os códigos das moedas
                String url = "https://economia.awesomeapi.com.br/json/last/" + origem + "-" + destino;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(TelaConversao.this, "Erro ao acessar API", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String jsonData = response.body().string();
                            runOnUiThread(() -> {
                                try {
                                    JSONObject json = new JSONObject(jsonData);
                                    String key = origem + destino;
                                    double taxa = json.getJSONObject(key).getDouble("bid");
                                    double resultado = valor * taxa;
                                    lblResultado.setText(String.format("%.2f %s = %.2f %s", valor, origem, resultado, destino));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(TelaConversao.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
