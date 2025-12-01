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

public class TelaMeuPerfil extends ActivityBase {

    // TextViews e ImageViews
    TextView lblEditarPerfil, lblNotificacoes, lblSuporte, lblSairConta, lblTermosPoliticas, lblSobreApp;
    ImageView imgFotoMeuPerfil, imgEditarPerfil, imgNotificacoes, imgSuporte, imgTermosPoliticas, imgSobreApp, imgSairConta;
    ImageView btnVoltar; // Botão de voltar (imgVoltar)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_meu_perfil);

        // Textos clicáveis (já declarados)
        lblNotificacoes = findViewById(R.id.lblNotificacoes);
        lblEditarPerfil = findViewById(R.id.lblEditarPerfil);
        lblSuporte  = findViewById(R.id.lblSuporte);
        lblSairConta = findViewById(R.id.lblSairConta);
        lblSobreApp = findViewById(R.id.lblSobreApp);
        lblTermosPoliticas = findViewById(R.id.lblTermosPoliticas);

        // Ícones
        imgFotoMeuPerfil = findViewById(R.id.imgFotoMeuPerfil);
        imgEditarPerfil = findViewById(R.id.imgEditarPerfil);
        imgNotificacoes = findViewById(R.id.imgNotificacoes);
        imgSuporte = findViewById(R.id.imgSuporte);
        imgSobreApp = findViewById(R.id.imgSobreApp);
        imgTermosPoliticas = findViewById(R.id.imgTermosPoliticas);
        imgSairConta = findViewById(R.id.imgSairConta);
        btnVoltar = findViewById(R.id.btnVoltarAnalise2);


        // 2. EXIBIÇÃO DE DADOS DO USUÁRIO (mantido)
        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaMeuPerfil.this);
        if (usuario != null) {
            if (usuario.getFotoUsuario() != null) {
                Bitmap fotoBitmap = getBitmapFromBase64(usuario.getFotoUsuario());
                imgFotoMeuPerfil.setImageBitmap(fotoBitmap);
            } else {
                imgFotoMeuPerfil.setImageResource(R.drawable.chat_bot);
            }
        } else {
            Toast.makeText(TelaMeuPerfil.this, "Erro ao obter usuário atual.", Toast.LENGTH_SHORT).show();
            return;
        }


        // 3. CONFIGURAÇÃO DOS EVENTOS DE CLIQUE (Intents)

        // Botão Voltar
        btnVoltar.setOnClickListener(v -> finish());

        // 3.1 OPÇÕES DO CARD ROXO (Editar Perfil e Notificações)

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


        // 3.2 OPÇÕES DO CARD VERDE (Informações e Suporte)

        // MUDANÇA AQUI: Suporte abre o navegador
        View.OnClickListener suporteClickListener = v -> {
            String url = "http://tcc3edsmodetecgr5.hospedagemdesites.ws/SitezenoFinancas/fale-conosco.php";
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(android.net.Uri.parse(url));
            startActivity(it);
        };
        lblSuporte.setOnClickListener(suporteClickListener);
        imgSuporte.setOnClickListener(suporteClickListener);

        View.OnClickListener sobreAppClickListener = v -> {
            // Supondo que você tem uma TelaSobreOApp
            Intent it = new Intent(TelaMeuPerfil.this, TelaSobreOApp.class);
            startActivity(it);
        };
        lblSobreApp.setOnClickListener(sobreAppClickListener);
        imgSobreApp.setOnClickListener(sobreAppClickListener);

        View.OnClickListener termosClickListener = v -> {
            // Supondo que você tem uma TelaPoliticaPrivacidade ou TelaTermos
            Intent it = new Intent(TelaMeuPerfil.this, TelaPoliticaPrivacidade.class);
            startActivity(it);
        };
        lblTermosPoliticas.setOnClickListener(termosClickListener);
        imgTermosPoliticas.setOnClickListener(termosClickListener);


        // 3.3 SAIR DA CONTA (Já estava pronto)
        View.OnClickListener sairClickListener = v -> {
            clsDadosUsuario.logoutUsuario(TelaMeuPerfil.this);
            Intent it = new Intent(TelaMeuPerfil.this, MainActivity.class);
            startActivity(it);
            finishAffinity(); // Fecha todas as telas anteriores
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