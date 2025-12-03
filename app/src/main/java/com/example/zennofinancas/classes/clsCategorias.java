package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class clsCategorias {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";

    private String idCategoria;
    private String idUsuario;
    private String nome;
    private String tipo;

    public clsCategorias() {}
    public clsCategorias(String idCategoria, String idUsuario, String nome, String tipo) {
        this.idCategoria = idCategoria;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getIdCategoria() { return idCategoria; }
    public void setIdCategoria(String idCategoria) { this.idCategoria = idCategoria; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public static void buscarCategorias(Context contexto, String idUsuario, String tipo, FutureCallback<ArrayList<String[]>> callback) {
        String url = BASE_URL + "categorias?select=id_categoria,nome&id_usuario=eq." + idUsuario + "&tipo=eq." + tipo;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onCompleted(e, null);
                        return;
                    }

                    ArrayList<String[]> categorias = new ArrayList<>();
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            String id = item.get("id_categoria").getAsString();
                            String nome = item.get("nome").getAsString();
                            categorias.add(new String[]{id, nome});
                        }
                    }
                    callback.onCompleted(null, categorias);
                });
    }
}