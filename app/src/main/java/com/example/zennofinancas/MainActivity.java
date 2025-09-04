package com.example.zennofinancas;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActivityBase
{
    // Atributos
    Button btnEntrar, btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializando Elementos
        btnEntrar = (Button) findViewById(R.id.btnEntrarInicial);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarInicial);


        // Evento Botão Entrar
        btnEntrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(MainActivity.this,
                                        TelaEntrar.class);
                startActivity(it);

            }
        });

        // Evento Botão Cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(MainActivity.this,TelaConversao.class);
                startActivity(it);
                /*Intent it = new Intent(MainActivity.this,
                        TelaCadastrar.class);
                startActivity(it);*/

            }
        });


    }
}