package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TelaChecarCodigo extends ActivityBase
{
    Button btnChecarCod;
    TextView subtituloChecarCod;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_checar_codigo);

        btnChecarCod = (Button) findViewById(R.id.btnChecarCod);
        subtituloChecarCod = (TextView) findViewById(R.id.subtituloChecarCod);

        String emailUsuario = "";
        Bundle email = getIntent().getExtras();

        if(email != null) {
            emailUsuario = email.getString("emailUsuario");
        }

        subtituloChecarCod.setText("Nós enviamos um código de recuperação para " + emailUsuario);


        btnChecarCod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaChecarCodigo.this,
                        TelaRedefinirSenha.class);
                startActivity(it);
            }
        });
    }
}