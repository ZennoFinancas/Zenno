package com.example.zennofinancas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TelaCadastrarEmail extends ActivityBase {

    Button btnCadastrarUser;
    EditText txtNome, txtEmail, txtSenha, txtConfSenha; // Mudei TextView para EditText (correto)

    // Variáveis para controlar se a senha está visível ou não
    boolean senhaVisivel = false;
    boolean confSenhaVisivel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastrar_email);

        txtNome = findViewById(R.id.txtNomeCadastro);
        txtEmail = findViewById(R.id.txtEmailCadastro);
        txtSenha = findViewById(R.id.txtSenhaCadastro);
        txtConfSenha = findViewById(R.id.txtConfSenhaCadastro);
        btnCadastrarUser = findViewById(R.id.btnCadastrar);

        // Configura o clique do olhinho para as duas senhas
        configurarVisualizacaoSenha(txtSenha, true);
        configurarVisualizacaoSenha(txtConfSenha, false);

        btnCadastrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = txtNome.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String senha = txtSenha.getText().toString();
                String confSenha = txtConfSenha.getText().toString();

                clsMetodos supabase = new clsMetodos();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()) {
                    Toast.makeText(TelaCadastrarEmail.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else if (!senha.equals(confSenha)) {
                    Toast.makeText(TelaCadastrarEmail.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                } else if (senha.length() < 7) {
                    Toast.makeText(TelaCadastrarEmail.this, "Senha deve conter ao menos 7 caracteres!", Toast.LENGTH_SHORT).show();
                } else {
                    // Método inserir
                    supabase.inserirUsuario(TelaCadastrarEmail.this, nome, email, "(11)9999-2222", senha);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configurarVisualizacaoSenha(EditText editText, boolean isCampoSenhaPrincipal) {
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Índice do ícone da direita

            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Verifica se o clique foi na área do ícone da direita
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    // Lógica para alternar visibilidade
                    if (isCampoSenhaPrincipal) {
                        senhaVisivel = !senhaVisivel;
                        alternarIconeETransformacao(editText, senhaVisivel);
                    } else {
                        confSenhaVisivel = !confSenhaVisivel;
                        alternarIconeETransformacao(editText, confSenhaVisivel);
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void alternarIconeETransformacao(EditText editText, boolean isVisivel) {
        if (isVisivel) {
            // Mostra a senha
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vizualizar_senha, 0);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // Oculta a senha
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nao_vizualizar_senha, 0);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // Mantém o cursor no final do texto
        editText.setSelection(editText.getText().length());
    }
}