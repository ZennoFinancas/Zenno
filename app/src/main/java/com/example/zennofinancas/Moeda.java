package com.example.zennofinancas;

public class Moeda {
    private String codigo;
    private String nome;
    private int bandeiraResId; // Campo para a imagem

    public Moeda(String codigo, String nome, int bandeiraResId) {
        this.codigo = codigo;
        this.nome = nome;
        this.bandeiraResId = bandeiraResId;
    }

    // Construtor antigo (opcional, para compatibilidade se precisar)
    public Moeda(String codigo, String nome) {
        this(codigo, nome, 0);
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getBandeiraResId() { return bandeiraResId; }

    @Override
    public String toString() { return nome; }
}