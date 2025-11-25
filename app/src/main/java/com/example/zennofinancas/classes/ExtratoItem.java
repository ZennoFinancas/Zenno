package com.example.zennofinancas.classes;


public class ExtratoItem {

    private String nome;
    private String valor;
    private String tipo; // receita ou despesa

    public ExtratoItem(String nome, String valor, String tipo) {
        this.nome = nome;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getNome() { return nome; }
    public String getValor() { return valor; }
    public String getTipo() { return tipo; }
}
