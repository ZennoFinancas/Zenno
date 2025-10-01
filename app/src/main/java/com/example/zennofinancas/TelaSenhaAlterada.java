package com.example.zennofinancas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaSenhaAlterada extends ActivityBase
{
    Button btnVoltarAoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_senha_alterada);

        btnVoltarAoLogin = (Button) findViewById(R.id.btnTudoPronto);

        btnVoltarAoLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaSenhaAlterada.this,
                        TelaEntrar.class);
                startActivity(it);
            }
        });
    }
}