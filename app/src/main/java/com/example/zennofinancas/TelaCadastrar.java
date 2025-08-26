package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaCadastrar extends ActivityBase
{
    // Atributos
    Button btnCadastrarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastrar);

        // Inicializando Elementos
        btnCadastrarEmail = (Button) findViewById(R.id.btnCadastrarEmail);

        // Evento Botão cadastrar com e-mail
        btnCadastrarEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaCadastrar.this,
                        TelaCadastrarEmail.class);
                startActivity(it);
            }
        });
    }
}