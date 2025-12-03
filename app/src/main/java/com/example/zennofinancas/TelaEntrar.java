package com.example.zennofinancas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zennofinancas.classes.clsUsuario;

/**
 * Tela de Login: responsável pela autenticação do usuário e pela navegação para recuperação de senha.
 */

public class TelaEntrar extends ActivityBase
{
    // Declaração de Componentes
    EditText txtEmail, txtSenha;
    TextView lblEsqSenha;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_entrar);

        // Associação de Elementos
        btnEntrar = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmailLogin);
        txtSenha = (EditText) findViewById(R.id.txtSenhaLogin);
        lblEsqSenha = (TextView) findViewById(R.id.lblEsqSenha);

        // Detecta toque no ícone de "ver senha" dentro do EditText
        txtSenha.setOnTouchListener((v, event) ->
        {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                // Verifica se o toque foi exatamente no ícone de visualizar senha
                if (event.getRawX() >= (txtSenha.getRight()
                        - txtSenha.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                {

                    // Verifica se a senha está oculta ou visível
                    boolean senhaOculta =
                            txtSenha.getInputType() ==
                                    (android.text.InputType.TYPE_CLASS_TEXT |
                                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    if (senhaOculta)
                    {
                        // Mostrar senha
                        txtSenha.setInputType(
                                android.text.InputType.TYPE_CLASS_TEXT |
                                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        txtSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nao_vizualizar_senha, 0);

                    }

                    else
                    {
                        // Ocultar senha novamente
                        txtSenha.setInputType(
                                android.text.InputType.TYPE_CLASS_TEXT |
                                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vizualizar_senha, 0);
                    }

                    // Mantém o cursor sempre no final do texto
                    txtSenha.setSelection(txtSenha.getText().length());
                    return true;
                }
            }

            return false;
        });

        // Evento do botão "Entrar"
        btnEntrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Captura e limpa valores inseridos
                String emailUsuario, senhaUsuario;
                emailUsuario = txtEmail.getText().toString().trim();
                senhaUsuario = txtSenha.getText().toString().trim();

                // Realiza tentativa de login
                clsUsuario.loginUsuario(TelaEntrar.this, emailUsuario, senhaUsuario);
            }
        });

        // Label "Esqueceu a senha" → direciona para tela de esqueceu a senha
        lblEsqSenha.setOnClickListener(view -> {
            Intent it = new Intent(TelaEntrar.this, TelaEsqueceuASenha.class);
            startActivity(it);
        });
    }
}