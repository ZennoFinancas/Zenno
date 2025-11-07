
package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import com.example.zennofinancas.api.clsUsuarioController;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;


public class TelaEntrar extends ActivityBase
{

    EditText txtEmail, txtSenha;
    TextView lblEsqSenha;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrar);


        btnEntrar = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmailLogin);
        txtSenha = (EditText) findViewById(R.id.txtSenhaLogin);
        lblEsqSenha = (TextView) findViewById(R.id.lblEsqSenha);

        // Evento do botão entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailUsuario, senhaUsuario;
                emailUsuario = txtEmail.getText().toString().trim();
                senhaUsuario = txtSenha.getText().toString().trim();

                /* Controller responsável pelo login
                clsUsuarioController usuarioController = new clsUsuarioController();

                usuarioController.logar(TelaEntrar.this, emailUsuario, senhaUsuario);*/

                clsMetodos supabase = new clsMetodos();

                supabase.loginUsuario(TelaEntrar.this, emailUsuario, senhaUsuario);


            }
        });

        // Evento da label esqueceu a senha
        lblEsqSenha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(TelaEntrar.this,
                        TelaEsqueceuASenha.class);
                startActivity(it);
            }
        });


    }

}