package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TelaMeuPerfil extends AppCompatActivity {
    TextView lblEditarPerfil, lblNotificacoes, lblSuporte, lblSairConta, lblTermosPoliticas, lblSobreApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_meu_perfil);
        lblNotificacoes = findViewById(R.id.lblNotificacoes);
        lblEditarPerfil = findViewById(R.id.lblEditarPerfil);
        lblSuporte  = findViewById(R.id.lblSuporte);
        lblSairConta = findViewById(R.id.lblSairConta);
        lblSobreApp = findViewById(R.id.lblSobreApp);
        lblTermosPoliticas = findViewById(R.id.lblTermosPoliticas);

        lblEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this,
                        TelaEditarPerfil.class);
                startActivity(it);
            }
        });
        lblNotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this, TelaNotificacoes.class);
                startActivity(it);
            }
        });
/*
        lblSuporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this,
                        TelaSuporte.class);
                startActivity(it);
            }
        });
        lblSobreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this,
                        TelaSobreApp.class);
                startActivity(it);
            }
        });
        lblTermosPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this,
                        TelaTermosPoliticas.class);
                startActivity(it);
            }
        });

        lblSairConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaMeuPerfil.this,
                        TelaEntrar.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
                finish();

            }
        });*/



    }
}