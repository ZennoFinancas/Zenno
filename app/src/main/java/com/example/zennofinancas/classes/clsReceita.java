package com.example.zennofinancas.classes;

public class clsReceita {
    private String idReceita;
    private String idUsuario;
    private String idCategoria;
    private double valorReceita;
    private String descricaoReceita;
    private String dataReceita;

    public clsReceita(String idReceita, String idUsuario, String idCategoria, double valorReceita, String descricaoReceita, String dataReceita) {
        this.idReceita = idReceita;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.valorReceita = valorReceita;
        this.descricaoReceita = descricaoReceita;
        this.dataReceita = dataReceita;
    }

    public clsReceita() {}

    public String getIdReceita() {
        return idReceita;
    }

    public void setIdReceita(String idReceita) {
        this.idReceita = idReceita;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public double getValorReceita() {
        return valorReceita;
    }

    public void setValorReceita(double valorReceita) {
        this.valorReceita = valorReceita;
    }

    public String getDescricaoReceita() {
        return descricaoReceita;
    }

    public void setDescricaoReceita(String descricaoReceita) {
        this.descricaoReceita = descricaoReceita;
    }

    public String getDataReceita() {
        return dataReceita;
    }

    public void setDataReceita(String dataReceita) {
        this.dataReceita = dataReceita;
    }
}
