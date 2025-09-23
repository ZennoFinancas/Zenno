
package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;


public class TelaEntrar extends ActivityBase
{

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";

    EditText txtEmail, txtSenha;
    TextView lblEsqSenha;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrar);


        btnEntrar = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmailLogin);
        txtSenha = (EditText) findViewById(R.id.txtSenhaLogin);
        lblEsqSenha = (TextView) findViewById(R.id.lblEsqSenha);

        // Evento do botão entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logar();


            }
        });

        // Evento da label esqueceu a senha
        lblEsqSenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaEntrar.this,
                        TelaEsqueceuASenha.class);
                startActivity(it);
            }
        });
    }


    private void logar() {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + txtEmail.getText().toString().trim() + "&senha_usuario=eq." + txtSenha.getText().toString().trim();

        Ion.with(TelaEntrar.this)
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
                            Toast.makeText(TelaEntrar.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, o login foi um sucesso
                        if (result != null && result.size() > 0) {

                            //Pegar os dados do usuario encontrado
                            JsonObject usuario = result.get(0).getAsJsonObject();

                            // Salvar nome do usuario
                            String nomeDoUsuario = usuario.get("nome_usuario").getAsString();

                            Toast.makeText(TelaEntrar.this, "Login realizado com sucesso! Bem-vindo, " + nomeDoUsuario, Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(TelaEntrar.this, TelaInicial.class);
                            startActivity(trocar);

                        } else {

                            Toast.makeText(TelaEntrar.this, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}