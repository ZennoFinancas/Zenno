package com.example.zennofinancas.classes;

import com.google.gson.annotations.SerializedName;
import java.text.NumberFormat;
import java.util.Locale;

public class ExtratoItem {

    // === CORREÇÃO 1: Mapeamento do ID ===
    // Isso garante que o ID venha corretamente do banco
    @SerializedName(value = "idTransacao", alternate = {"id", "id_transacao", "id_gasto", "id_receita"})
    private int idTransacao;

    private int idUsuario;
    private Integer idCategoria;
    private String nomeCategoria;

    @SerializedName(value = "tipoTransacao", alternate = {"tipo_categoria", "tipo"})
    private String tipoTransacao;

    @SerializedName(value = "valor", alternate = {"valor_despesa", "valor_receita", "valor_aporte"})
    private double valor;

    @SerializedName(value = "descricao", alternate = {"descricao_despesa", "descricao_receita"})
    private String descricao;

    @SerializedName(value = "dataTransacao", alternate = {"data_despesa", "data_receita", "data_transacao"})
    private String dataTransacao;

    private int ano;
    private int mes;

    public ExtratoItem() {
    }

    public ExtratoItem(int idTransacao, int idUsuario, Integer idCategoria,
                       String nomeCategoria, String tipoTransacao, double valor,
                       String descricao, String dataTransacao, int ano, int mes) {
        this.idTransacao = idTransacao;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.descricao = descricao;
        this.dataTransacao = dataTransacao;
        this.ano = ano;
        this.mes = mes;
    }

    // --- GETTERS E SETTERS ---

    // === CORREÇÃO 2: O Método que estava faltando ===
    public int getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(int idTransacao) {
        this.idTransacao = idTransacao;
    }
    // ================================================

    public double getValorNumerico() {
        return valor;
    }

    public String getValor() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(valor);
    }

    public String getValorFormatado() {
        return getValor();
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getNomeCategoria() {
        if (nomeCategoria != null && !nomeCategoria.isEmpty()) {
            return nomeCategoria;
        }
        if (descricao != null && !descricao.isEmpty()) {
            return descricao;
        }
        return "Geral";
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public String getDescricao() { return descricao; }
    public String getDataTransacao() { return dataTransacao; }
    public String getTipoTransacao() { return tipoTransacao; }

    public boolean isReceita() {
        return "receita".equalsIgnoreCase(tipoTransacao);
    }

    public boolean isDespesa() {
        return "despesa".equalsIgnoreCase(tipoTransacao) || "gasto".equalsIgnoreCase(tipoTransacao);
    }

    public String getValorComSinal() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatter.format(valor);

        if (isReceita()) {
            return "+ " + valorFormatado;
        } else {
            return "- " + valorFormatado;
        }
    }
}