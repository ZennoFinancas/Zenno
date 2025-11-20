package com.example.zennofinancas.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class clsDadosUsuario {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario; // ➕ NOVO CAMPO

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

    public String getEmailUsuario() {        // ➕ GET
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) { // ➕ SET
        this.emailUsuario = emailUsuario;
    }

    // Salvar no SharedPreferences
    public static void salvarUsuario(Context context, String nomeUsuario, String idUsuario, String emailUsuario) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nomeUsuario", nomeUsuario);
        editor.putString("idUsuario", idUsuario);
        editor.putString("emailUsuario", emailUsuario); // ➕ SALVAR NOVO CAMPO

        editor.apply();
    }

    // Recuperar o usuário atual
    public static clsDadosUsuario getUsuarioAtual(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String nome = prefs.getString("nomeUsuario", null);
        String id = prefs.getString("idUsuario", null);
        String email = prefs.getString("emailUsuario", null);

        if (nome == null || id == null) return null;

        clsDadosUsuario usuario = new clsDadosUsuario();

        usuario.setNomeUsuario(nome);
        usuario.setIdUsuario(id);
        usuario.setEmailUsuario(email); // ➕ SETAR

        return usuario;
    }

    // Logout (limpa tudo)
    public static void logoutUsuario(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
