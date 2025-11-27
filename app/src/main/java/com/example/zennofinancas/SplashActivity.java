package com.example.zennofinancas; // Confirme seu pacote

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Esconder a barra de ação (ActionBar) para a tela ficar cheia
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configura o tempo de espera (3000 milisegundos = 3 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 1. Cria a intenção de abrir a Tela Inicial (MainActivity)
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // 2. Mata a SplashActivity para que, se o usuário apertar "Voltar", não volte para a splash
            finish();
        }, 3000);
    }
}