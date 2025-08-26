package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                authManager authManager = new authManager();
                authManager.registerUser(Email, "Vini123",TelaEntrar.this );


            }
        });


    }
}