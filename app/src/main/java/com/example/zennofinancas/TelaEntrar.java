package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

                String Email = "";
                apiSupabase APISUPABASE = new apiSupabase();

                APISUPABASE.postData("");

                new Thread(() -> {
                    // Chama o método postData na thread separada
                    String response = APISUPABASE.postData("");

                    // Atualiza a UI com o resultado da requisição
                    runOnUiThread(() -> {
                        if (response != null) {
                            // Se a resposta for válida, mostrar um Toast de sucesso
                            Toast.makeText(TelaEntrar.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Se a resposta for null, mostrar um Toast de erro
                            Toast.makeText(TelaEntrar.this, "Erro de login!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start(); // Inicia a thread de fundo


            }
        });


    }
}