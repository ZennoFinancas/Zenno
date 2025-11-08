package com.example.zennofinancas.classes;

public class clsObjetivo {

    private String idObjetivo;
    private String idUsuario;
    private String nomeObjetivo;
    private String descricaoObjetivo;
    private double valorMeta;
    private double valorAtual;
    private String dataCriacao;
    private String dataLimite;

    public clsObjetivo() {
    }

    public clsObjetivo(String idObjetivo, String idUsuario, String nomeObjetivo, String descricaoObjetivo,
                       double valorMeta, double valorAtual, String dataCriacao, String dataLimite) {
        this.idObjetivo = idObjetivo;
        this.idUsuario = idUsuario;
        this.nomeObjetivo = nomeObjetivo;
        this.descricaoObjetivo = descricaoObjetivo;
        this.valorMeta = valorMeta;
        this.valorAtual = valorAtual;
        this.dataCriacao = dataCriacao;
        this.dataLimite = dataLimite;
    }

    public String getIdObjetivo() {
        return idObjetivo;
    }

    public void setIdObjetivo(String idObjetivo) {
        this.idObjetivo = idObjetivo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeObjetivo() {
        return nomeObjetivo;
    }

    public void setNomeObjetivo(String nomeObjetivo) {
        this.nomeObjetivo = nomeObjetivo;
    }

    public String getDescricaoObjetivo() {
        return descricaoObjetivo;
    }

    public void setDescricaoObjetivo(String descricaoObjetivo) {
        this.descricaoObjetivo = descricaoObjetivo;
    }

    public double getValorMeta() {
        return valorMeta;
    }

    public void setValorMeta(double valorMeta) {
        this.valorMeta = valorMeta;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(String dataLimite) {
        this.dataLimite = dataLimite;
    }
}
