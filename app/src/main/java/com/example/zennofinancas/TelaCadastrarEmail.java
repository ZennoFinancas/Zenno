package com.example.zennofinancas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TelaCadastrarEmail extends ActivityBase {

    Button btnCadastrarUser;
    TextView txtNome, txtEmail, txtSenha, txtConfSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastrar_email);

        txtNome = (TextView) findViewById(R.id.txtNomeCadastro);
        txtEmail = (TextView) findViewById(R.id.txtEmailCadastro);
        txtSenha = (TextView) findViewById(R.id.txtSenhaCadastro);
        txtConfSenha = (TextView) findViewById(R.id.txtConfSenhaCadastro);
        btnCadastrarUser = (Button) findViewById(R.id.btnCadastrar);

        btnCadastrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = txtNome.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String senha = txtSenha.getText().toString();
                String confSenha = txtConfSenha.getText().toString();


                // Controller do usuario
                clsMetodos supabase = new clsMetodos();



                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()) {
                    Toast.makeText(TelaCadastrarEmail.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else if (!senha.equals(confSenha)) {
                    Toast.makeText(TelaCadastrarEmail.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                } else if (senha.length() < 7) {
                    Toast.makeText(TelaCadastrarEmail.this, "Senha deve conter ao menos 7 caracteres!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(TelaCadastrarEmail.this, "AQUIIII", Toast.LENGTH_SHORT).show();
                    // Método inserir
                    supabase.inserirUsuario(TelaCadastrarEmail.this, nome, email, "(11)9999-2222", senha );
                }
            }
        });
    }

}