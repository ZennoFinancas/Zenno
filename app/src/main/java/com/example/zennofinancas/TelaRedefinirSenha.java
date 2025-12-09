package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zennofinancas.classes.clsUsuario;

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

                String emailUsuario = "";

                // Validação se o email foi enviado via intent da tela anterior
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    emailUsuario = extras.getString("emailUsuario");
                } else {
                    Toast.makeText(TelaRedefinirSenha.this, "Erro: email não recebido", Toast.LENGTH_SHORT).show();
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
                    // Método alterar a senha
                    clsMetodos.alterarSenha(TelaRedefinirSenha.this, emailUsuario, senha);

                }
            }
        }); // btn redefinir senha

    }
}