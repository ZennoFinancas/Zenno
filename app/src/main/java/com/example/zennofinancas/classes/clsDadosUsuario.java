package com.example.zennofinancas.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class clsDadosUsuario {
    private String idUsuario;
    private String nomeUsuario;

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    // Salvar no SharedPreferences
    public static void salvarUsuario(Context context, String nomeUsuario, String idUsuario) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nomeUsuario", nomeUsuario);
        editor.putString("idUsuario", idUsuario);
        editor.apply();
    }

    // Recuperar o usuário atual
    public static clsDadosUsuario getUsuarioAtual(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String nome = prefs.getString("nomeUsuario", null);
        String id = prefs.getString("idUsuario", null);

        if (nome == null || id == null) return null;

        clsDadosUsuario usuario = new clsDadosUsuario();
        usuario.setNomeUsuario(nome);
        usuario.setIdUsuario(id);
        return usuario;
    }

    // (logout)
    public static void logoutUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
