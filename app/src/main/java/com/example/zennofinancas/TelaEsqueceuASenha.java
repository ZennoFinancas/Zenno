package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class TelaEsqueceuASenha extends ActivityBase
{

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    Button btnEsqSenha;
    TextView txtEmailEsqSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_esqueceu_asenha);

        btnEsqSenha = (Button) findViewById(R.id.btnEsqSenha);
        txtEmailEsqSenha = (TextView) findViewById(R.id.txtEmailEsqSenha);

        btnEsqSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                VerificarEmail();
                Intent it = new Intent(TelaEsqueceuASenha.this,
                        TelaChecarCodigo.class);
                startActivity(it);
            }
        });
    }

        private void VerificarEmail(){
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + txtEmailEsqSenha.getText().toString().trim();

        Ion.with(TelaEsqueceuASenha.this)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(TelaEsqueceuASenha.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, o login foi um sucesso
                        if (result != null && result.size() > 0) {

                            //Pegar os dados do usuario encontrado
                            JsonObject usuario = result.get(0).getAsJsonObject();

                            Toast.makeText(TelaEsqueceuASenha.this, "Email encontrado com sucesso! ", Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(TelaEsqueceuASenha.this, TelaChecarCodigo.class);
                            startActivity(trocar);

                        } else {

                            Toast.makeText(TelaEsqueceuASenha.this, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}