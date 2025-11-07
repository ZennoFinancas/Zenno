package com.example.zennofinancas.model;

import android.content.Context;
import com.example.zennofinancas.api.clsSupabaseClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

public class clsUsuarioModel {

    public static void buscarUsuario(Context contexto, String email, String senha, FutureCallback<JsonArray> callback) {
        String endpoint = "usuarios?email_usuario=eq." + email + "&senha_usuario=eq." + senha;
        clsSupabaseClient.get(contexto, endpoint, callback);
    }

    public static void inserirUsuario(Context contexto, JsonObject dados, FutureCallback<String> callback) {
        clsSupabaseClient.post(contexto, "usuarios", dados, callback);
    }

    public static void alterarSenha(Context contexto, String email, JsonObject json, FutureCallback<JsonArray> callback) {
        clsSupabaseClient.patch(contexto, "usuarios?email_usuario=eq." + email, json, callback);
    }
}
