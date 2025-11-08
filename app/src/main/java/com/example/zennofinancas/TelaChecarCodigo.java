package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zennofinancas.model.clsEnviarEmail;

public class TelaChecarCodigo extends ActivityBase
{
    Button btnChecarCod;
    TextView subtituloChecarCod;
    EditText txtChecarCod;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_checar_codigo);


        clsEnviarEmail email = new clsEnviarEmail();


        btnChecarCod = (Button) findViewById(R.id.btnChecarCod);
        subtituloChecarCod = (TextView) findViewById(R.id.subtituloChecarCod);
        txtChecarCod = (EditText) findViewById(R.id.txtChecarCod);

        // Recebe os valores enviados via Intent
        Bundle intent = getIntent().getExtras();
        if (intent == null) {
            Toast.makeText(this, "Erro: Dados não recebidos!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String emailUsuario = intent.getString("emailUsuario", "");
        String controleChecarCod = intent.getString("controleChecarCod", "");
        int codigoVerificacao = 0;

        codigoVerificacao = intent.getInt("codigoVerificacao");



        // Define mensagem e envia email
        if ("cadastrar".equals(controleChecarCod)) {
            subtituloChecarCod.setText("Nós enviamos um código de verificação para " + emailUsuario);
            email.enviar(
                    TelaChecarCodigo.this,
                    emailUsuario,
                    "Validação de Conta - Zenno Finanças",
                    "Validação de Conta",
                    "Digite o código abaixo no aplicativo para validar o seu acesso.",
                    codigoVerificacao
            );
        } else {
            subtituloChecarCod.setText("Nós enviamos um código de recuperação para " + emailUsuario);
            email.enviar(
                    TelaChecarCodigo.this,
                    emailUsuario,
                    "Recuperação de Senha - Zenno Finanças",
                    "Recuperação de Senha",
                    "Digite o código abaixo no aplicativo para recuperar sua senha.",
                    codigoVerificacao
            );
        }

        // Botão de checar código
        int finalCodigoVerificacao = codigoVerificacao;
        String finalControleChecarCod = controleChecarCod;
        String finalEmailUsuario = emailUsuario;

        btnChecarCod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clsMetodos supabase = new clsMetodos();

                int codigoDigitado;

                // Tenta converter o código digitado
                try {
                    codigoDigitado = Integer.parseInt(txtChecarCod.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(TelaChecarCodigo.this, "Digite um código válido!", Toast.LENGTH_SHORT).show();
                    return;
                }

                
                // Se o código estiver correto
                if ("cadastrar".equals(finalControleChecarCod) && codigoDigitado == finalCodigoVerificacao) {
                    supabase.validarEmailUsuario(TelaChecarCodigo.this, finalEmailUsuario);
                } else if (codigoDigitado == finalCodigoVerificacao) {
                    // Redireciona para redefinir senha
                    Intent i = new Intent(TelaChecarCodigo.this, TelaRedefinirSenha.class);
                    i.putExtra("emailUsuario", finalEmailUsuario);
                    startActivity(i);
                } else {
                    Toast.makeText(TelaChecarCodigo.this, "Código incorreto!", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }
}