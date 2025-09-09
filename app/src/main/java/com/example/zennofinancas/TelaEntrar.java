
package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;


public class TelaEntrar extends ActivityBase
{

    String Host = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private String url,ret;
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
               /* new Thread(() -> {
                    apiSupabase api = new apiSupabase();
                    String resposta = api.postData("");

                    runOnUiThread(() -> {
                        if (resposta != null) {
                            Toast.makeText(TelaEntrar.this, "ConexÃ£o bem-sucedida!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TelaEntrar.this, "Erro na conexÃ£o com a API", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();*/


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
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios";

        JsonObject json = new JsonObject();
        json.addProperty("nome_user", "Pedro Costa");
        json.addProperty("email_user", "pcp161107@gmail.com");
        json.addProperty("senha_user", "pcp123");

        Ion.with(TelaEntrar.this)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)  // corpo em JSON
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<com.google.gson.JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, com.google.gson.JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(TelaEntrar.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null && result.size() > 0) {
                            // Inserção realizada com sucesso

                            Intent trocar = new Intent(TelaEntrar.this, MainActivity.class);
                            startActivity(trocar);
                        } else {
                            Toast.makeText(TelaEntrar.this, "Erro ao inserir: resultado vazio", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    /*private void inserir()
    {
        url=Host+"inserirt.php";
        Ion.with (TelaEntrar.this)
                .load ( url )
                .setBodyParameter ( "usuario" ,txtlogin.getText ().toString ())
                .setBodyParameter ( "senha",txtsenha.getText () .toString ())
                .asJsonObject ()
                .setCallback ( new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        ret=result.get("status").getAsString ();
                        if(ret.equals ( "ok" ))
                        {
                            Toast.makeText(getApplicationContext(),
                                    " incluido com sucesso", Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),  " erro",
                                    Toast.LENGTH_LONG).show();

                        }


                    }
                } );

    }*/
}