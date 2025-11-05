package com.example.zennofinancas;
import android.content.Intent;
import android.content.SharedPreferences;
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

        // Verificando se o usuário está logado
        SharedPreferences prefs = MainActivity.this.getSharedPreferences("user_prefs", MainActivity.this.MODE_PRIVATE);
        String nomeUsuario = prefs.getString("nomeUsuario", null);
        String idUsuario = prefs.getString("idUsuario", null);

        if (idUsuario != null) {

            Intent it = new Intent(MainActivity.this,
                    TelaInicial.class);
            startActivity(it);

        }

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

                Intent it = new Intent(MainActivity.this,
                        TelaCadastrar.class);
                startActivity(it);

            }
        });


    }
}