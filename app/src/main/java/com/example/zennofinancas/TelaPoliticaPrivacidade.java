package com.example.zennofinancas;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TelaPoliticaPrivacidade extends ActivityBase {

    // 1. Declara a variável do botão
    ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_politica_privacidade);

        // 2. Vincula com o ID que está no XML (btnVoltarPolitica)
        btnVoltar = findViewById(R.id.btnVoltarPolitica);

        // 3. Configura o clique para fechar a tela
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela atual e volta para a anterior (TelaMeuPerfil)
            }
        });
    }
}