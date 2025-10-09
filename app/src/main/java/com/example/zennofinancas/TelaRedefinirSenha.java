package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TelaRedefinirSenha extends ActivityBase
{
    Button btnRedSenha;
    EditText txtRedSenha, txtConfRedSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_redefinir_senha);

        btnRedSenha = (Button) findViewById(R.id.btnRedSenha);
        txtRedSenha = (EditText) findViewById(R.id.txtRedSenha);
        txtConfRedSenha = (EditText) findViewById(R.id.txtConfRedSenha);


        btnRedSenha.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                // Instanciando classe do Banco de Dados
                clsMetodos supabase = new clsMetodos();

                String emailUsuario = "";
                Bundle email = getIntent().getExtras();
                if(email != null) {
                    emailUsuario = email.getString("emailUsuario");
                }

                String senha = txtRedSenha.getText().toString();
                String confSenha = txtConfRedSenha.getText().toString();

                if (senha.isEmpty() || confSenha.isEmpty()) {
                    Toast.makeText(TelaRedefinirSenha.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else if (!senha.equals(confSenha)) {
                    Toast.makeText(TelaRedefinirSenha.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                } else if (senha.length() < 7) {
                    Toast.makeText(TelaRedefinirSenha.this, "Senha deve conter ao menos 7 caracteres!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(TelaRedefinirSenha.this, "AQUIIII", Toast.LENGTH_SHORT).show();
                    // Método alterar a senha

                    supabase.AlterarSenha(TelaRedefinirSenha.this, emailUsuario, senha);

                }
            }
        }); // btn redefinir senha

    }
}