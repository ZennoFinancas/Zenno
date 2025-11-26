package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;

import com.example.zennofinancas.classes.ExtratoItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class SupabaseHelper {

    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";


    public static void buscarExtrato(Context contexto, String idUsuario,
                                     Integer mes, Integer ano, String tipoFiltro,
                                     FutureCallback<ArrayList<ExtratoItem>> callback) {

        // Monta a URL com filtros
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "view_extrato_completo?");
        urlBuilder.append("id_usuario=eq.").append(idUsuario);

        if (mes != null) {
            urlBuilder.append("&mes=eq.").append(mes);
        }

        if (ano != null) {
            urlBuilder.append("&ano=eq.").append(ano);
        }

        if (tipoFiltro != null && !tipoFiltro.isEmpty()) {
            urlBuilder.append("&tipo_transacao=eq.").append(tipoFiltro);
        }

        // Ordena por data decrescente
        urlBuilder.append("&order=data_transacao.desc");

        String url = urlBuilder.toString();

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao buscar extrato: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        callback.onCompleted(e, null);
                        return;
                    }

                    ArrayList<ExtratoItem> extrato = new ArrayList<>();

                    if (result != null && result.size() > 0) {
                        for (int i = 0; i < result.size(); i++) {
                            try {
                                JsonObject item = result.get(i).getAsJsonObject();

                                int idTransacao = item.get("id_transacao").getAsInt();
                                int idUser = item.get("id_usuario").getAsInt();

                                Integer idCategoria = null;
                                if (!item.get("id_categoria").isJsonNull()) {
                                    idCategoria = item.get("id_categoria").getAsInt();
                                }

                                String nomeCategoria = item.get("nome_categoria").isJsonNull()
                                        ? "Sem categoria"
                                        : item.get("nome_categoria").getAsString();

                                String tipoTransacao = item.get("tipo_transacao").getAsString();
                                double valor = item.get("valor").getAsDouble();

                                String descricao = item.get("descricao").isJsonNull()
                                        ? ""
                                        : item.get("descricao").getAsString();

                                String dataTransacao = item.get("data_transacao").getAsString();
                                int anoItem = item.get("ano").getAsInt();
                                int mesItem = item.get("mes").getAsInt();

                                ExtratoItem extratoItem = new ExtratoItem(
                                        idTransacao, idUser, idCategoria, nomeCategoria,
                                        tipoTransacao, valor, descricao, dataTransacao,
                                        anoItem, mesItem
                                );

                                extrato.add(extratoItem);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    callback.onCompleted(null, extrato);
                });
    }

    /**
     * Busca categorias do usuário por tipo
     */
    public static void buscarCategorias(Context contexto, String idUsuario, String tipo,
                                        FutureCallback<ArrayList<String[]>> callback) {

        String url = BASE_URL + "categorias"
                + "?select=id_categoria,nome&id_usuario=eq." + idUsuario
                + "&tipo=eq." + tipo;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao buscar categorias", Toast.LENGTH_SHORT).show();
                        callback.onCompleted(e, null);
                        return;
                    }

                    ArrayList<String[]> categorias = new ArrayList<>();

                    if (result != null && result.size() > 0) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            String idCategoria = item.get("id_categoria").getAsString();
                            String nomeCategoria = item.get("nome").getAsString();
                            categorias.add(new String[]{idCategoria, nomeCategoria});
                        }
                    }

                    callback.onCompleted(null, categorias);
                });
    }
}