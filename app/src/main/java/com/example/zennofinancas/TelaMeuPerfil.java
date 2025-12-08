package com.example.zennofinancas;

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

public class TelaMeuPerfil extends ActivityBase {

    TextView lblEditarPerfil, lblNotificacoes, lblSuporte, lblSairConta, lblTermosPoliticas, lblSobreApp, lblNomeMeuPerfil, lblEmailMeuPerfil;
    ImageView imgFotoMeuPerfil, imgEditarPerfil, imgNotificacoes, imgSuporte, imgTermosPoliticas, imgSobreApp, imgSairConta;
    ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_meu_perfil);

        lblNotificacoes = findViewById(R.id.lblNotificacoes);
        lblEditarPerfil = findViewById(R.id.lblEditarPerfil);
        lblSuporte  = findViewById(R.id.lblSuporte);
        lblSairConta = findViewById(R.id.lblSairConta);
        lblSobreApp = findViewById(R.id.lblSobreApp);
        lblNomeMeuPerfil = findViewById(R.id.lblNomeMeuPerfil);
        lblEmailMeuPerfil = findViewById(R.id.lblEmailMeuPerfil);
        lblTermosPoliticas = findViewById(R.id.lblTermosPoliticas);

        imgFotoMeuPerfil = findViewById(R.id.imgFotoMeuPerfil);
        imgEditarPerfil = findViewById(R.id.imgEditarPerfil);
        imgNotificacoes = findViewById(R.id.imgNotificacoes);
        imgSuporte = findViewById(R.id.imgSuporte);
        imgSobreApp = findViewById(R.id.imgSobreApp);
        imgTermosPoliticas = findViewById(R.id.imgTermosPoliticas);
        imgSairConta = findViewById(R.id.imgSairConta);
        btnVoltar = findViewById(R.id.btnVoltarMeuPerfil);

        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaMeuPerfil.this);

        if (usuario != null) {
            String fotoBase64 = usuario.getFotoUsuario();
            boolean fotoCarregada = false;

            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                Bitmap fotoBitmap = getBitmapFromBase64(fotoBase64);
                if (fotoBitmap != null) {
                    imgFotoMeuPerfil.setImageBitmap(fotoBitmap);
                    fotoCarregada = true;
                }
            }

            if (!fotoCarregada) {
                imgFotoMeuPerfil.setImageResource(R.drawable.foto_usuario);
            }

            lblNomeMeuPerfil.setText(usuario.getNomeUsuario().toUpperCase());
            lblEmailMeuPerfil.setText(usuario.getEmailUsuario());
        } else {
            Toast.makeText(TelaMeuPerfil.this, "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
        }

        btnVoltar.setOnClickListener(v -> finish());

        View.OnClickListener editarPerfilClickListener = v -> {
            Intent it = new Intent(TelaMeuPerfil.this, TelaEditarPerfil.class);
            startActivity(it);
        };
        lblEditarPerfil.setOnClickListener(editarPerfilClickListener);
        imgEditarPerfil.setOnClickListener(editarPerfilClickListener);

        View.OnClickListener notificacoesClickListener = v -> {
            Intent it = new Intent(TelaMeuPerfil.this, TelaNotificacoes.class);
            startActivity(it);
        };
        lblNotificacoes.setOnClickListener(notificacoesClickListener);
        imgNotificacoes.setOnClickListener(notificacoesClickListener);

        View.OnClickListener suporteClickListener = v -> {
            String url = "http://tcc3edsmodetecgr5.hospedagemdesites.ws/SitezenoFinancas/fale-conosco.php";
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(android.net.Uri.parse(url));
            startActivity(it);
        };
        lblSuporte.setOnClickListener(suporteClickListener);
        imgSuporte.setOnClickListener(suporteClickListener);

        View.OnClickListener sobreAppClickListener = v -> {
            Intent it = new Intent(TelaMeuPerfil.this, TelaSobreOApp.class);
            startActivity(it);
        };
        lblSobreApp.setOnClickListener(sobreAppClickListener);
        imgSobreApp.setOnClickListener(sobreAppClickListener);

        View.OnClickListener termosClickListener = v -> {
            Intent it = new Intent(TelaMeuPerfil.this, TelaPoliticaPrivacidade.class);
            startActivity(it);
        };
        lblTermosPoliticas.setOnClickListener(termosClickListener);
        imgTermosPoliticas.setOnClickListener(termosClickListener);

        View.OnClickListener sairClickListener = v -> {
            clsDadosUsuario.logoutUsuario(TelaMeuPerfil.this);
            Intent it = new Intent(TelaMeuPerfil.this, MainActivity.class);
            startActivity(it);
            finishAffinity();
        };
        lblSairConta.setOnClickListener(sairClickListener);
        imgSairConta.setOnClickListener(sairClickListener);
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