package com.example.zennofinancas.classes;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class clsDespesas {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";
    private static final String RPC_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/rpc/";

    private String idDespesa;
    private String idUsuario;
    private String idCategoria;
    private double valorDespesa;
    private String descricaoDespesa;
    private String dataDespesa;

    public clsDespesas() {}
    public clsDespesas(String idDespesa, String idUsuario, String idCategoria, double valorDespesa, String descricaoDespesa, String dataDespesa) {
        this.idDespesa = idDespesa;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.valorDespesa = valorDespesa;
        this.descricaoDespesa = descricaoDespesa;
        this.dataDespesa = dataDespesa;
    }

    public String getIdDespesa() { return idDespesa; }
    public void setIdDespesa(String idDespesa) { this.idDespesa = idDespesa; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getIdCategoria() { return idCategoria; }
    public void setIdCategoria(String idCategoria) { this.idCategoria = idCategoria; }
    public double getValorDespesa() { return valorDespesa; }
    public void setValorDespesa(double valorDespesa) { this.valorDespesa = valorDespesa; }
    public String getDescricaoDespesa() { return descricaoDespesa; }
    public void setDescricaoDespesa(String descricaoDespesa) { this.descricaoDespesa = descricaoDespesa; }
    public String getDataDespesa() { return dataDespesa; }
    public void setDataDespesa(String dataDespesa) { this.dataDespesa = dataDespesa; }

    public static void inserirDespesa(Context contexto,int repeticoes, String idUsuario, String idCategoria, String valorDespesa, String descricaoDespesa, String dataDespesa) {
        float valor = Float.parseFloat(valorDespesa.replace(",", "."));
        String novaData = converterDataParaISO(dataDespesa);

        JsonObject json = new JsonObject();
        json.addProperty("p_id_usuario", Integer.parseInt(idUsuario));
        json.addProperty("p_id_categoria", Integer.parseInt(idCategoria));
        json.addProperty("p_valor_despesa", valor);
        json.addProperty("p_descricao_despesa", descricaoDespesa);
        json.addProperty("p_data_inicial", novaData);
        json.addProperty("p_repeticoes", repeticoes);

        Ion.with(contexto)
                .load("POST", RPC_URL + "inserir_despesa_recorrente")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null || result == null || result.size() == 0) {
                        Toast.makeText(contexto, "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    JsonObject resp = result.get(0).getAsJsonObject();
                    Toast.makeText(contexto, resp.get("mensagem").getAsString(), Toast.LENGTH_LONG).show();
                });
    }

    public static void excluirDespesa(Context contexto, int idDespesa) {
        Ion.with(contexto)
                .load("DELETE", BASE_URL + "despesas?id_gasto=eq." + idDespesa)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asString()
                .setCallback((e, r) -> Toast.makeText(contexto, e != null ? "Erro" : "Deletada!", Toast.LENGTH_SHORT).show());
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