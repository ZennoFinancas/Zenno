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

    public interface ReceitaCallback {
        void onSucesso(String mensagem, int quantidadeInserida);
        void onErro(String erro);
    }

    public static void inserirReceita(
            Context contexto,
            int repeticoes,
            String idUsuario,
            String idCategoria,
            String valorReceita,
            String descricaoReceita,
            String dataReceita,
            ReceitaCallback callback
    ) {

        float valor = Float.parseFloat(valorReceita.replace(",", "."));
        String novaData = converterDataParaISO(dataReceita);

        JsonObject json = new JsonObject();
        json.addProperty("p_id_usuario", idUsuario);
        json.addProperty("p_id_categoria", idCategoria);
        json.addProperty("p_valor_receita", valor);
        json.addProperty("p_descricao_receita", descricaoReceita);
        json.addProperty("p_data_inicial", novaData);
        json.addProperty("p_repeticoes", repeticoes);

        Ion.with(contexto)
                .load("POST", BASE_URL + "/rpc/inserir_receita_recorrente")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback((e, result) -> {

                    if (e != null) {
                        Toast.makeText(contexto, "Erro de conexão", Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onErro(e.getMessage());
                        return;
                    }

                    if (result != null && result.size() > 0) {

                        JsonObject resposta = result.get(0).getAsJsonObject();

                        boolean sucesso = resposta.has("sucesso") && !resposta.get("sucesso").isJsonNull()
                                && resposta.get("sucesso").getAsBoolean();

                        String mensagem = resposta.has("mensagem") && !resposta.get("mensagem").isJsonNull()
                                ? resposta.get("mensagem").getAsString()
                                : "Resposta inesperada do servidor";

                        int quantidade = resposta.has("quantidade_inserida") && !resposta.get("quantidade_inserida").isJsonNull()
                                ? resposta.get("quantidade_inserida").getAsInt()
                                : 0;

                        if (sucesso) {
                            Toast.makeText(contexto, mensagem, Toast.LENGTH_LONG).show();
                            if (callback != null) callback.onSucesso(mensagem, quantidade);
                        } else {
                            Toast.makeText(contexto, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                            if (callback != null) callback.onErro(mensagem);
                        }
                    }
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