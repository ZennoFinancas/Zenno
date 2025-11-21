package com.example.zennofinancas.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class clsDadosUsuario {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String fotoUsuario;

    // Getters e Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    // Salvar no SharedPreferences
    public static void salvarUsuario(Context context, String nomeUsuario, String idUsuario, String emailUsuario, String fotoUsuario) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nomeUsuario", nomeUsuario);
        editor.putString("idUsuario", idUsuario);
        editor.putString("emailUsuario", emailUsuario);
        editor.putString("fotoUsuario", fotoUsuario);

        editor.apply();
    }

    // Recuperar o usuário atual
    public static clsDadosUsuario getUsuarioAtual(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String nome = prefs.getString("nomeUsuario", null);
        String id = prefs.getString("idUsuario", null);
        String email = prefs.getString("emailUsuario", null);
        String foto = prefs.getString("fotoUsuario", null);

        if (nome == null || id == null)
            return null;

        clsDadosUsuario usuario = new clsDadosUsuario();

        usuario.setNomeUsuario(nome);
        usuario.setIdUsuario(id);
        usuario.setEmailUsuario(email);
        usuario.setFotoUsuario(foto); // ➕ SETAR FOTO

        return usuario;
    }

    // Logout
    public static void logoutUsuario(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
