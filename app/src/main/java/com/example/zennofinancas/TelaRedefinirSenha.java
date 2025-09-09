package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaRedefinirSenha extends ActivityBase
{
    Button btnRedSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_redefinir_senha);

        btnRedSenha = (Button) findViewById(R.id.btnRedSenha);

        btnRedSenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaRedefinirSenha.this,
                        TelaSenhaAlterada.class);
                startActivity(it);
            }
        });
    }
}