package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class clsExtrato {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";
    private static final String RPC_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/rpc/";

    public interface SaldoCallback {
        void onResultado(double saldo);
    }

    public interface DespesaCallback {
        void onResultado(double totalDespesas);
    }

    public interface ExtratoCallback {
        void onResultado(Exception e, ArrayList<String[]> dados);
    }

    // Buscar saldo total das receitas
    public static void buscarSaldo(Context contexto, String idUsuario, SaldoCallback callback) {
        String url = BASE_URL + "receitas?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    double soma = 0.0;
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject linha = result.get(i).getAsJsonObject();
                            if (linha.has("valor_receita") && !linha.get("valor_receita").isJsonNull()) {
                                soma += linha.get("valor_receita").getAsDouble();
                            }
                        }
                    }
                    callback.onResultado(soma);
                });
    }

    // Buscar total das despesas
    public static void buscarDespesasTotal(Context contexto, String idUsuario, DespesaCallback callback) {
        String url = BASE_URL + "despesas?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    double soma = 0.0;
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            if (item.has("valor_despesa") && !item.get("valor_despesa").isJsonNull()) {
                                soma += item.get("valor_despesa").getAsDouble();
                            }
                        }
                    }
                    callback.onResultado(soma);
                });
    }

    // Buscar receitas filtradas por mês/ano
    public static void buscarReceitas(Context contexto, String idUsuario, int mes, int ano, ExtratoCallback callback) {
        String url = RPC_URL + "get_receitas_filtrado";

        JsonObject json = new JsonObject();
        json.addProperty("p_id_usuario", Integer.parseInt(idUsuario));
        json.addProperty("p_mes", mes);
        json.addProperty("p_ano", ano);

        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    ArrayList<String[]> receitas = new ArrayList<>();
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            String descricao = item.has("descricao_receita") ? item.get("descricao_receita").getAsString() : "";
                            String valor = item.has("valor_receita") ? item.get("valor_receita").getAsString() : "0";
                            String categoria = item.has("nome_categoria") ? item.get("nome_categoria").getAsString() : "";
                            String data = item.has("data_receita") ? item.get("data_receita").getAsString() : "";

                            receitas.add(new String[]{descricao, valor, categoria, data});
                        }
                    }
                    callback.onResultado(null, receitas);
                });
    }

    // Buscar despesas filtradas por mês/ano
    public static void buscarDespesas(Context contexto, String idUsuario, int mes, int ano, ExtratoCallback callback) {
        String url = RPC_URL + "get_despesas_filtrado";

        JsonObject json = new JsonObject();
        json.addProperty("p_id_usuario", Integer.parseInt(idUsuario));
        json.addProperty("p_mes", mes);
        json.addProperty("p_ano", ano);

        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    ArrayList<String[]> despesas = new ArrayList<>();
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            String descricao = item.has("descricao_despesa") ? item.get("descricao_despesa").getAsString() : "";
                            String valor = item.has("valor_despesa") ? item.get("valor_despesa").getAsString() : "0";
                            String categoria = item.has("nome_categoria") ? item.get("nome_categoria").getAsString() : "";
                            String data = item.has("data_despesa") ? item.get("data_despesa").getAsString() : "";

                            despesas.add(new String[]{descricao, valor, categoria, data});
                        }
                    }
                    callback.onResultado(null, despesas);
                });
    }

    // Buscar extrato completo (receitas + despesas)
    public static void buscarExtratoCompleto(Context contexto, String idUsuario, int mes, int ano, ExtratoCallback callback) {
        String url = RPC_URL + "get_extrato_completo";

        JsonObject json = new JsonObject();
        json.addProperty("p_id_usuario", Integer.parseInt(idUsuario));
        json.addProperty("p_mes", mes);
        json.addProperty("p_ano", ano);

        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    ArrayList<String[]> extrato = new ArrayList<>();
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            String descricao = item.has("descricao") ? item.get("descricao").getAsString() : "";
                            String valor = item.has("valor") ? item.get("valor").getAsString() : "0";
                            String categoria = item.has("nome_categoria") ? item.get("nome_categoria").getAsString() : "";
                            String data = item.has("data_transacao") ? item.get("data_transacao").getAsString() : "";
                            String origem = item.has("tabela_origem") ? item.get("tabela_origem").getAsString() : "";
                            String tipo = item.has("tipo") ? item.get("tipo").getAsString() : "";

                            extrato.add(new String[]{descricao, valor, categoria, data, origem, tipo});
                        }
                    }
                    callback.onResultado(null, extrato);
                });
    }
}