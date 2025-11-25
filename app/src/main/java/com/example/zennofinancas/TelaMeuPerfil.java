package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zennofinancas.classes.clsDadosUsuario;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TelaMeuPerfil extends AppCompatActivity {
    TextView lblNomeMeuPerfil,
            lblEmailMeuPerfil,
            lblEditarPerfil,
            lblNotificacoes,
            lblSuporte,
            lblSairConta, lblTermosPoliticas, lblSobreApp;

    ImageView imgFotoMeuPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_meu_perfil);
        lblNomeMeuPerfil = findViewById(R.id.lblNomeMeuPerfil);
        lblEmailMeuPerfil = findViewById(R.id.lblEmailMeuPerfil);
        lblNotificacoes = findViewById(R.id.lblNotificacoes);
        lblEditarPerfil = findViewById(R.id.lblEditarPerfil);
        lblSuporte  = findViewById(R.id.lblSuporte);
        lblSairConta = findViewById(R.id.lblSairConta);
        lblSobreApp = findViewById(R.id.lblSobreApp);
        lblTermosPoliticas = findViewById(R.id.lblTermosPoliticas);

        imgFotoMeuPerfil = findViewById(R.id.imgFotoMeuPerfil);

        // Exibe as informações do usuario na tela
        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaMeuPerfil.this);
        if (usuario != null) {

            // Valida se o user colocou uma foto ou não
            if (usuario.getFotoUsuario() != null) {
                Bitmap fotoBitmap = getBitmapFromBase64(usuario.getFotoUsuario());
                imgFotoMeuPerfil.setImageBitmap(fotoBitmap);
            }
            // Caso nn tenha foto, define foto padrão
            else {
                imgFotoMeuPerfil.setImageResource(R.drawable.chat_bot);
            }

            lblNomeMeuPerfil.setText(usuario.getNomeUsuario().toUpperCase());
            lblEmailMeuPerfil.setText(usuario.getEmailUsuario());
        } else {
            Toast.makeText(TelaMeuPerfil.this, "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
            return;
        }


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
        });*/

        lblSairConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clsDadosUsuario.logoutUsuario(TelaMeuPerfil.this);

                Intent it = new Intent(TelaMeuPerfil.this,
                        MainActivity.class);
                startActivity(it);


            }
        });



    }

    private Bitmap getBitmapFromBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}