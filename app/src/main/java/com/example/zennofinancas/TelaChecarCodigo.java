package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaChecarCodigo extends ActivityBase
{
    Button btnChecarCod;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_checar_codigo);

        btnChecarCod = (Button) findViewById(R.id.btnChecarCod);

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