package com.example.zennofinancas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zennofinancas.classes.clsDadosUsuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends ActivityBase {

    Button btnEntrar, btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btnEntrarInicial);
        btnCadastrar = findViewById(R.id.btnCadastrarInicial);

        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(MainActivity.this);

        if (usuario != null && usuario.getIdUsuario() != null) {
            Intent it = new Intent(MainActivity.this, TelaInicial.class);
            startActivity(it);
        }

        btnEntrar.setOnClickListener(view -> {
            Intent it = new Intent(MainActivity.this, TelaEntrar.class);
            startActivity(it);
        });

        btnCadastrar.setOnClickListener(view -> {
            Intent it = new Intent(MainActivity.this, TelaCadastrar.class);
            startActivity(it);
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;

                    String token = task.getResult();
                    System.out.println("Token FCM: " + token);
                });
    }
}
