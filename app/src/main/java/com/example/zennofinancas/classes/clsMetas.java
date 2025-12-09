package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;

import com.example.zennofinancas.TelaMetas;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class clsMetas {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";

    private String idObjetivo;
    private String idUsuario;
    private String nomeObjetivo;
    private double valorDesejado;
    private String dataObjetivo;

    public clsMetas() {}
    public clsMetas(String idObjetivo, String idUsuario, String nomeObjetivo, double valorDesejado, String dataObjetivo) {
        this.idObjetivo = idObjetivo;
        this.idUsuario = idUsuario;
        this.nomeObjetivo = nomeObjetivo;
        this.valorDesejado = valorDesejado;
        this.dataObjetivo = dataObjetivo;
    }

    public String getIdObjetivo() { return idObjetivo; }
    public void setIdObjetivo(String idObjetivo) { this.idObjetivo = idObjetivo; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getNomeObjetivo() { return nomeObjetivo; }
    public void setNomeObjetivo(String nomeObjetivo) { this.nomeObjetivo = nomeObjetivo; }
    public double getValorDesejado() { return valorDesejado; }
    public void setValorDesejado(double valorDesejado) { this.valorDesejado = valorDesejado; }
    public String getDataObjetivo() { return dataObjetivo; }
    public void setDataObjetivo(String dataObjetivo) { this.dataObjetivo = dataObjetivo; }

    public interface MetasCallback {
        void onResultado(Exception e, ArrayList<String[]> metas);
    }

    public interface AportesCallback {
        void onResultado(Exception e, double totalAportes);
    }

    // Buscar objetivos
    public static void buscarObjetivos(Context contexto, String idUsuario, MetasCallback callback) {
        String url = BASE_URL + "objetivos?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    if (result == null || result.size() == 0) {
                        callback.onResultado(null, new ArrayList<>());
                        return;
                    }

                    ArrayList<String[]> listaMetas = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject linha = result.get(i).getAsJsonObject();
                        String idMeta = linha.has("id_objetivo") ? linha.get("id_objetivo").getAsString() : "";
                        String nome = linha.has("nome_objetivo") ? linha.get("nome_objetivo").getAsString() : "";
                        String valor = linha.has("valor_desejado") ? linha.get("valor_desejado").getAsString() : "0";
                        listaMetas.add(new String[]{idMeta, nome, valor});
                    }
                    callback.onResultado(null, listaMetas);
                });
    }

    // Inserir objetivo
    public interface InserirMetaCallback {
        void onResultado(Exception e, String idMetaCriada);
    }

    public static void inserirObjetivo(Context contexto, String idUsuario, String nomeObjetivo, String valorDesejado, String dataObjetivo, InserirMetaCallback callback) {
        float valor = Float.parseFloat(valorDesejado.replace(",", "."));

        JsonObject json = new JsonObject();
        json.addProperty("id_usuario", idUsuario);
        json.addProperty("nome_objetivo", nomeObjetivo);
        json.addProperty("valor_desejado", valor);

        Ion.with(contexto)
                .load("POST", BASE_URL + "objetivos")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Prefer", "return=representation")  // 👈 OBRIGA O SUPABASE A RETORNAR O OBJETO
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    if (result == null || result.size() == 0) {
                        callback.onResultado(new Exception("Nenhum retorno do servidor"), null);
                        return;
                    }

                    JsonObject linha = result.get(0).getAsJsonObject();
                    String idMeta = linha.has("id_objetivo") ? linha.get("id_objetivo").getAsString() : null;

                    callback.onResultado(null, idMeta);
                });
    }

    // Excluir objetivo
    public static void excluirObjetivo(Context contexto, String idObjetivo) {
        String url = BASE_URL + "objetivos?id_objetivo=eq." + idObjetivo;

        Ion.with(contexto)
                .load("DELETE", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao deletar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Meta deletada!", Toast.LENGTH_LONG).show();
                });
    }

    // Buscar aportes
    public static void buscarAportesObjetivo(Context contexto, String idObjetivo, AportesCallback callback) {
        String url = BASE_URL + "aportes_objetivo?id_objetivo=eq." + idObjetivo;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        callback.onResultado(e, 0.0);
                        return;
                    }

                    double total = 0.0;
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();
                            if (item.has("valor")) {
                                total += item.get("valor").getAsDouble();
                            }
                        }
                    }
                    callback.onResultado(null, total);
                });
    }

    // Inserir aporte
    public static void inserirAporteObjetivo(Context contexto, String idObjetivo, String valorDesejado, String idUsuario) {
        float valor = Float.parseFloat(valorDesejado.replace(",", "."));

        JsonObject json = new JsonObject();
        json.addProperty("id_objetivo", idObjetivo);
        json.addProperty("valor", valor);
        json.addProperty("id_usuario", idUsuario);

        Ion.with(contexto)
                .load("POST", BASE_URL + "aportes_objetivo")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Dinheiro guardado!", Toast.LENGTH_LONG).show();
                });

    }
}