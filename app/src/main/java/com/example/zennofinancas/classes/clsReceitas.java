package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class clsReceitas {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";

    private String idReceita;
    private String idUsuario;
    private String idCategoria;
    private double valorReceita;
    private String descricaoReceita;
    private String dataReceita;

    public clsReceitas() {}
    public clsReceitas(String idReceita, String idUsuario, String idCategoria, double valorReceita, String descricaoReceita, String dataReceita) {
        this.idReceita = idReceita;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.valorReceita = valorReceita;
        this.descricaoReceita = descricaoReceita;
        this.dataReceita = dataReceita;
    }

    public String getIdReceita() { return idReceita; }
    public void setIdReceita(String idReceita) { this.idReceita = idReceita; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getIdCategoria() { return idCategoria; }
    public void setIdCategoria(String idCategoria) { this.idCategoria = idCategoria; }
    public double getValorReceita() { return valorReceita; }
    public void setValorReceita(double valorReceita) { this.valorReceita = valorReceita; }
    public String getDescricaoReceita() { return descricaoReceita; }
    public void setDescricaoReceita(String descricaoReceita) { this.descricaoReceita = descricaoReceita; }
    public String getDataReceita() { return dataReceita; }
    public void setDataReceita(String dataReceita) { this.dataReceita = dataReceita; }

    public static void inserirReceita(Context contexto, String idUsuario, String idCategoria, String valorReceita, String descricaoReceita, String dataReceita) {
        float valor = Float.parseFloat(valorReceita.replace(",", "."));
        String novaData = converterDataParaISO(dataReceita);

        JsonObject json = new JsonObject();
        json.addProperty("id_usuario", idUsuario);
        json.addProperty("id_categoria", idCategoria);
        json.addProperty("valor_receita", valor);
        json.addProperty("descricao_receita", descricaoReceita);
        json.addProperty("data_receita", novaData);

        Ion.with(contexto)
                .load("POST", BASE_URL + "receitas")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Receita cadastrada!", Toast.LENGTH_LONG).show();
                });
    }

    private static String converterDataParaISO(String dataBrasileira) {
        if (dataBrasileira == null || dataBrasileira.isEmpty()) return null;
        try {
            String[] partes = dataBrasileira.split("/");
            return String.format("%s-%s-%s", partes[2], partes[1], partes[0]);
        } catch (Exception e) {
            return null;
        }
    }
}