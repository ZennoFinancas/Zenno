package com.example.zennofinancas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zennofinancas.classes.clsUsuario;

// Tela responsável pelo cadastro de usuário por e-mail
public class TelaCadastrarEmail extends ActivityBase {

    // Declaração dos elementos da interface
    Button btnCadastrarUser;
    ImageView btnVoltar;
    EditText txtNome, txtEmail, txtSenha, txtConfSenha;

    // Variáveis para controlar se a senha está visível ou não
    boolean senhaVisivel = false;
    boolean confSenhaVisivel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastrar_email);

        // Vínculo dos componentes da tela com o XML
        txtNome = findViewById(R.id.txtNomeCadastro);
        txtEmail = findViewById(R.id.txtEmailCadastro);
        txtSenha = findViewById(R.id.txtSenhaCadastro);
        txtConfSenha = findViewById(R.id.txtConfSenhaCadastro);
        btnCadastrarUser = findViewById(R.id.btnCadastrar);

        // Referência do botão voltar
        btnVoltar = findViewById(R.id.btnVoltarCadastrar);

        // Evento de clique para retornar à tela inicial
        btnVoltar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Volta para MainActivity
                Intent intent = new Intent(TelaCadastrarEmail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Configuração dos ícones de mostrar/ocultar senha
        configurarVisualizacaoSenha(txtSenha, true);
        configurarVisualizacaoSenha(txtConfSenha, false);

        // Ação do botão cadastrar usuário
        btnCadastrarUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                // Captura dos valores digitados
                String nome = txtNome.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String senha = txtSenha.getText().toString();
                String confSenha = txtConfSenha.getText().toString();

                // Validações básicas de preenchimento e senha
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty())
                {
                    Toast.makeText(TelaCadastrarEmail.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }

                else if (!senha.equals(confSenha))
                {
                    Toast.makeText(TelaCadastrarEmail.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                }

                else if (senha.length() < 7)
                {
                    Toast.makeText(TelaCadastrarEmail.this, "Senha deve conter ao menos 7 caracteres!", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    // Inserção do usuário no banco
                    clsUsuario.inserirUsuario(TelaCadastrarEmail.this, nome, email, "(11)9999-2222", senha);
                }
            }
        });
    }

    // Método responsável por ativar a visualização da senha ao clicar no ícone
    @SuppressLint("ClickableViewAccessibility")
    private void configurarVisualizacaoSenha(EditText editText, boolean isCampoSenhaPrincipal)
    {
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Índice do ícone da direita

            // Só executa quando o usuário solta o dedo no ícone
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                // Verifica se o toque foi exatamente no ícone
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                {
                    // Alterna entre mostrar e ocultar conforme o campo
                    if (isCampoSenhaPrincipal)
                    {
                        senhaVisivel = !senhaVisivel;
                        alternarIconeETransformacao(editText, senhaVisivel);
                    }

                    else
                    {
                        confSenhaVisivel = !confSenhaVisivel;
                        alternarIconeETransformacao(editText, confSenhaVisivel);
                    }
                    return true;
                }
            }
            return false;
        });
    }

    // Alterna ícone e comportamento do campo (visualizar/ocultar senha)
    private void alternarIconeETransformacao(EditText editText, boolean isVisivel)
    {
        if (isVisivel)
        {
            // Mostra a senha digitada
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vizualizar_senha, 0);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

        else
        {
            // Oculta a senha
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nao_vizualizar_senha, 0);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // Mantém o cursor sempre no final
        editText.setSelection(editText.getText().length());
    }
}