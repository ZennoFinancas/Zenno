
package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class TelaEntrar extends ActivityBase
{

    EditText txtEmail, txtSenha;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrar);


        btnEntrar = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmailLogin);
        txtSenha = (EditText) findViewById(R.id.txtSenhaLogin);


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    apiSupabase api = new apiSupabase();
                    String resposta = api.postData("");

                    runOnUiThread(() -> {
                        if (resposta != null) {
                            Toast.makeText(TelaEntrar.this, "ConexÃ£o bem-sucedida!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TelaEntrar.this, "Erro na conexÃ£o com a API", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();


            }
        });
    }
}