package com.example.zennofinancas.api;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.zennofinancas.MainActivity;
import com.example.zennofinancas.api.clsSupabaseClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.ArrayList;

public class clsReceitaController {

    public static void inserirReceita(Context contexto, String idUsuario, String idCategoria, String valor, String descricao, String data) {
        JsonObject json = new JsonObject();
        json.addProperty("id_usuario", idUsuario);
        json.addProperty("id_categoria", idCategoria);
        json.addProperty("valor_receita", valor);
        json.addProperty("descricao_receita", descricao);
        json.addProperty("data_receita", data);

        clsSupabaseClient.post(contexto, "receitas", json, (e, result) -> {
            if (e != null) {
                Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(contexto, "Receita cadastrada com sucesso!", Toast.LENGTH_LONG).show();
            contexto.startActivity(new Intent(contexto, MainActivity.class));
        });
    }

    public static void buscarCategorias(Context contexto, String idUsuario, FutureCallback<ArrayList<String[]>> callback) {
        clsSupabaseClient.get(contexto, "categorias?id_usuario=eq." + idUsuario, (e, result) -> {
            if (e != null) {
                Toast.makeText(contexto, "Erro ao buscar categorias", Toast.LENGTH_SHORT).show();
                callback.onCompleted(e, null);
                return;
            }

            ArrayList<String[]> categorias = new ArrayList<>();
            if (result != null && result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    JsonObject item = result.get(i).getAsJsonObject();
                    categorias.add(new String[]{
                            item.get("id_categoria").getAsString(),
                            item.get("nome_categoria").getAsString()
                    });
                }
            }
            callback.onCompleted(null, categorias);
        });
    }
}
