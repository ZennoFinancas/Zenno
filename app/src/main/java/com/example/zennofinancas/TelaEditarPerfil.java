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
import android.widget.TextView;

import com.example.zennofinancas.classes.clsDadosUsuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class TelaEditarPerfil extends AppCompatActivity {

    EditText txtNomeEditPerfil, txtEmailEditPerfil, txtCelularEditPerfil;
    ImageView imgEditPerfil;
    Button btnTrocarFoto, btnSalvarAlteracoes;

    private Bitmap bitmap;
    Uri imagemUri;
    String bx;

    public static String fotoy, fotox;

    private int PICK_IMAGE_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_perfil);

        txtNomeEditPerfil = findViewById(R.id.txtNomeEditPerfil);
        txtEmailEditPerfil = findViewById(R.id.txtEmailEditPerfil);
        txtCelularEditPerfil = findViewById(R.id.txtCelularEditPerfil);
        imgEditPerfil = findViewById(R.id.imgFotoEditPerfil);

        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarAlteracoes);

        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(TelaEditarPerfil.this);

        txtNomeEditPerfil.setText(usuario.getNomeUsuario());
        txtEmailEditPerfil.setText(usuario.getEmailUsuario());

        if (usuario.getFotoUsuario() != null) {
            Bitmap fotoBitmap = getBitmapFromBase64(usuario.getFotoUsuario());
            imgEditPerfil.setImageBitmap(fotoBitmap);
        }
        // Caso nn tenha foto, define foto padrão
        else {
            imgEditPerfil.setImageResource(R.drawable.chat_bot);
        }

        btnTrocarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrir();
            }
        });


        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, nome, numero;
                email = txtEmailEditPerfil.getText().toString();
                nome = txtNomeEditPerfil.getText().toString();
                numero = txtCelularEditPerfil.getText().toString();

                clsMetodos.alterarDados(TelaEditarPerfil.this, email, nome, numero, fotox);

                SharedPreferences prefs = TelaEditarPerfil.this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("fotoUsuario", fotox);
                editor.apply();
            }
        });



    }

    private static final int PICK_IMAGE = 1;

    private void abrir() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imagemUri = data.getData();
            try {
                // Carregar a imagem a partir do URI
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream( imagemUri));
                imgEditPerfil.setImageBitmap(bitmap); // Definir no ImageView
                bx=imagem_string(bitmap);
                fotox=bx;
                Bitmap b = getfoto(bx);
                imgEditPerfil.setImageBitmap(b);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public String imagem_string(Bitmap fotox)
    {
        ByteArrayOutputStream data=new ByteArrayOutputStream();
        fotox.compress( Bitmap.CompressFormat.JPEG,100,data );
        byte[] b1=data.toByteArray();
        return Base64.encodeToString( b1, Base64.DEFAULT );
    }

    public  Bitmap getfoto(String s) {
        byte[] decodes= Base64.decode(s,Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodes, 0, decodes.length);
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