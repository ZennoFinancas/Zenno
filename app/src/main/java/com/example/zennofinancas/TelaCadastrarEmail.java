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

import org.w3c.dom.Text;

public class TelaCadastrarEmail extends ActivityBase {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    Button btnCadastrarUser;
    TextView txtNome, txtEmail, txtSenha, txtConfSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastrar_email);

        txtNome = (TextView) findViewById(R.id.txtNomeCadastro);
        txtEmail = (TextView) findViewById(R.id.txtEmailCadastro);
        txtSenha = (TextView) findViewById(R.id.txtSenhaCadastro);
        txtConfSenha = (TextView) findViewById(R.id.txtConfSenhaCadastro);

        btnCadastrarUser = (Button) findViewById(R.id.btnCadastrar);



        btnCadastrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = txtNome.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String senha = txtSenha.getText().toString();
                String confSenha = txtConfSenha.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()) {
                    Toast.makeText(TelaCadastrarEmail.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else if (!senha.equals(confSenha)) {
                    Toast.makeText(TelaCadastrarEmail.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TelaCadastrarEmail.this, "AQUIIII", Toast.LENGTH_SHORT).show();
                    inserir();
                }
            }
        });
    }


    //Método inserir usuario
    //Erro

    private void inserir() {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios";

        JsonObject json = new JsonObject();
        json.addProperty("nome_user", txtNome.getText().toString().trim());
        json.addProperty("email_user", txtEmail.getText().toString().trim());
        json.addProperty("senha_user", txtSenha.getText().toString());

        Ion.with(TelaCadastrarEmail.this)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)  // corpo em JSON
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, com.google.gson.JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(TelaCadastrarEmail.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null && result.size() > 0) {
                            // Inserção realizada com sucesso

                            Intent trocar = new Intent(TelaCadastrarEmail.this, MainActivity.class);
                            startActivity(trocar);
                        } else {
                            Toast.makeText(TelaCadastrarEmail.this, "Erro ao inserir: resultado vazio", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}