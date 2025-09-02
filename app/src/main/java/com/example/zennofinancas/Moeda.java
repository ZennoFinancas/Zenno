package com.example.zennofinancas;

public class Moeda { // Classe para armazenar código e nome da moeda
    private String codigo;
    private String nome;

    public Moeda(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}