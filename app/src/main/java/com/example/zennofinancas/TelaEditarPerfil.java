package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zennofinancas.classes.clsDadosUsuario;
import com.example.zennofinancas.classes.clsUsuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class TelaEditarPerfil extends ActivityBase {

    EditText txtNomeEditPerfil, txtEmailEditPerfil, txtCelularEditPerfil;
    // Adicionei a variável btnVoltar aqui
    ImageView imgEditPerfil, btnVoltar;
    Button btnTrocarFoto, btnSalvarAlteracoes;

    Uri imagemUri;
    String fotox = "";

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_perfil);

        txtNomeEditPerfil = findViewById(R.id.txtNomeEditPerfil);
        txtEmailEditPerfil = findViewById(R.id.txtEmailEditPerfil);
        txtCelularEditPerfil = findViewById(R.id.txtCelularEditPerfil);
        imgEditPerfil = findViewById(R.id.imgFotoEditPerfil);

        // Vínculo do botão voltar com o XML
        btnVoltar = findViewById(R.id.btnVoltarEditarPerfil);

        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarAlteracoes);

        // Ação do Botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Fecha essa tela e retorna automaticamente para a anterior (TelaMeuPerfil)
            }
        });

        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaEditarPerfil.this);

        if (usuario != null) {
            SharedPreferences prefs = TelaEditarPerfil.this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            txtCelularEditPerfil.setText(prefs.getString("numeroUsuario", null));
            txtNomeEditPerfil.setText(usuario.getNomeUsuario());
            txtEmailEditPerfil.setText(usuario.getEmailUsuario());

            String fotoBase64 = usuario.getFotoUsuario();
            boolean fotoCarregada = false;

            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                fotox = fotoBase64;
                Bitmap fotoBitmap = getBitmapFromBase64(fotoBase64);
                if (fotoBitmap != null) {
                    imgEditPerfil.setImageBitmap(fotoBitmap);
                    fotoCarregada = true;
                }
            }

            if (!fotoCarregada) {
                imgEditPerfil.setImageResource(R.drawable.foto_usuario);
            }
        }

        btnTrocarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
            }
        });

        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmailEditPerfil.getText().toString();
                String nome = txtNomeEditPerfil.getText().toString();
                String numero = txtCelularEditPerfil.getText().toString();

                clsUsuario.alterarDados(TelaEditarPerfil.this, email, nome, numero, fotox);

                SharedPreferences prefs = TelaEditarPerfil.this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("nomeUsuario", nome);
                editor.putString("numeroUsuario", numero);
                editor.putString("fotoUsuario", fotox);
                editor.apply();

                // Opcional: Se quiser voltar automaticamente após salvar, descomente a linha abaixo
                // finish();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imagemUri = data.getData();
            try {
                Bitmap bitmapOriginal = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagemUri));
                fotox = imagem_string(bitmapOriginal);

                Bitmap bitmapParaMostrar = getBitmapFromBase64(fotox);
                imgEditPerfil.setImageBitmap(bitmapParaMostrar);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String imagem_string(Bitmap imagemOriginal) {
        Bitmap imagemReduzida = redimensionarImagem(imagemOriginal, 600);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        imagemReduzida.compress(Bitmap.CompressFormat.JPEG, 70, data);
        byte[] bytes = data.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public Bitmap redimensionarImagem(Bitmap imagemOriginal, int larguraMaxima) {
        int largura = imagemOriginal.getWidth();
        int altura = imagemOriginal.getHeight();

        if (largura <= larguraMaxima) return imagemOriginal;

        float razao = (float) largura / (float) altura;
        int novaAltura = Math.round(larguraMaxima / razao);

        return Bitmap.createScaledBitmap(imagemOriginal, larguraMaxima, novaAltura, true);
    }

    private Bitmap getBitmapFromBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}