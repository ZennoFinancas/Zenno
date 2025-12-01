
package com.example.zennofinancas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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

        // Evento que mostra a senha
        txtSenha.setOnTouchListener((v, event) ->
        {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                if (event.getRawX() >= (txtSenha.getRight()
                        - txtSenha.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                {

                    // Verifica se está ocultando ou mostrando
                    boolean senhaOculta =
                            txtSenha.getInputType() ==
                                    (android.text.InputType.TYPE_CLASS_TEXT |
                                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    if (senhaOculta)
                    {
                        // Mostrar senha
                        txtSenha.setInputType(
                                android.text.InputType.TYPE_CLASS_TEXT |
                                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        );
                        txtSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nao_vizualizar_senha, 0);

                    }

                    else
                    {
                        // Ocultar senha
                        txtSenha.setInputType(
                                android.text.InputType.TYPE_CLASS_TEXT |
                                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        );
                        txtSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vizualizar_senha, 0);
                    }

                    // Mantém cursor no final
                    txtSenha.setSelection(txtSenha.getText().length());
                    return true;
                }
            }

            return false;
        });



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