package com.example.zennofinancas.classes;

import java.text.NumberFormat;
import java.util.Locale;

public class ExtratoItem {

    private int idTransacao;
    private int idUsuario;
    private Integer idCategoria; // Pode ser null para despesas sem categoria
    private String nomeCategoria;
    private String tipoTransacao; // "receita" ou "despesa"
    private double valor;
    private String descricao;
    private String dataTransacao; // formato: yyyy-MM-dd
    private int ano;
    private int mes;

    // Construtor completo
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

    // Construtor simplificado (compatÃ­vel com o cÃ³digo existente)
    public ExtratoItem(String nome, String valorFormatado, String tipo) {
        this.nomeCategoria = nome;
        this.tipoTransacao = tipo;
        // Remove "R$" e espaÃ§os, substitui vÃ­rgula por ponto
        String valorLimpo = valorFormatado.replace("R$", "").replace(" ", "")
                .replace(".", "").replace(",", ".");
        try {
            this.valor = Double.parseDouble(valorLimpo);
        } catch (NumberFormatException e) {
            this.valor = 0.0;
        }
    }

    // Getters bÃ¡sicos
    public int getIdTransacao() {
        return idTransacao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDataTransacao() {
        return dataTransacao;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    // MÃ©todos de compatibilidade com cÃ³digo existente
    public String getNome() {
        return nomeCategoria != null ? nomeCategoria : "Sem categoria";
    }

    public String getTipo() {
        return tipoTransacao;
    }

    /**
     * Retorna o valor formatado como moeda (R$ 1.234,56)
     * Usado pelo adapter para exibiÃ§Ã£o
     */
    public String getValor() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(valor);
    }

    /**
     * Retorna o valor numÃ©rico puro (double)
     * Usado para cÃ¡lculos e operaÃ§Ãµes matemÃ¡ticas
     */
    public double getValorNumerico() {
        return valor;
    }

    /**
     * Alias para getValor() - mantido por compatibilidade
     */
    public String getValorFormatado() {
        return getValor();
    }

    /**
     * Verifica se Ã© receita
     */
    public boolean isReceita() {
        return "receita".equalsIgnoreCase(tipoTransacao);
    }

    /**
     * Verifica se Ã© despesa
     */
    public boolean isDespesa() {
        return "despesa".equalsIgnoreCase(tipoTransacao);
    }

    /**
     * Retorna o valor com sinal (+ para receita, - para despesa)
     */
    public String getValorComSinal() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatter.format(valor);

        if (isReceita()) {
            return "+ " + valorFormatado;
        } else {
            return "- " + valorFormatado;
        }
    }


    @Override
    public String toString() {
        return "ExtratoItem{" +
                "tipo=" + tipoTransacao +
                ", categoria='" + nomeCategoria + '\'' +
                ", valor=" + getValor() +
                ", data=" + dataTransacao +
                '}';
    }
}