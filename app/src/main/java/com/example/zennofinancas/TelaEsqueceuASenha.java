package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zennofinancas.classes.clsUsuario;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class TelaEsqueceuASenha extends ActivityBase
{
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

                String email = txtEmailEsqSenha.getText().toString().trim();

                if (email.isEmpty())
                {
                    Toast.makeText(TelaEsqueceuASenha.this, "Digite Seu Email!", Toast.LENGTH_SHORT).show();
                }
                else{

                    // Verifica se o email digitado tem cadastro no BD
                    clsUsuario.verificarEmail(TelaEsqueceuASenha.this, email);
                }

            }
        });
    }
}