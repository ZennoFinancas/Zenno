package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaEsqueceuASenha extends ActivityBase
{

    Button btnEsqSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_esqueceu_asenha);

        btnEsqSenha = (Button) findViewById(R.id.btnEsqSenha);

        btnEsqSenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaEsqueceuASenha.this,
                        TelaChecarCodigo.class);
                startActivity(it);
            }
        });
    }
}