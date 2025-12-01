package com.example.zennofinancas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zennofinancas.classes.clsDadosUsuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Tela inicial do app: responsável por exibir os botões de entrada e cadastro, além de verificar se o usuário já está logado.
 */

public class MainActivity extends ActivityBase
{
    // Declaração de Componentes
    Button btnEntrar, btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inicializa o Firebase para uso de autenticação, banco e notificações
        FirebaseApp.initializeApp(this);

        // Carrega o layout da tela inicial
        setContentView(R.layout.activity_main);

        // Associação de Elementos
        btnEntrar = findViewById(R.id.btnEntrarInicial);
        btnCadastrar = findViewById(R.id.btnCadastrarInicial);

        // Recupera dados do usuário já salvo no app
        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(MainActivity.this);

        // Se existir um usuário logado, redireciona para a tela principal
        if (usuario != null && usuario.getIdUsuario() != null)
        {
            Intent it = new Intent(MainActivity.this, TelaInicial.class);
            startActivity(it);
        }

        // Botão "Entrar" → direciona para tela de login
        btnEntrar.setOnClickListener(view -> {
            Intent it = new Intent(MainActivity.this, TelaEntrar.class);
            startActivity(it);
        });

        // Botão "Cadastrar" → direciona para tela de cadastro
        btnCadastrar.setOnClickListener(view -> {
            Intent it = new Intent(MainActivity.this, TelaCadastrar.class);
            startActivity(it);
        });

        // Obtém o token do Firebase Cloud Messaging para notificações push
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;

                    String token = task.getResult();
                    System.out.println("Token FCM: " + token);
                });
    }
}