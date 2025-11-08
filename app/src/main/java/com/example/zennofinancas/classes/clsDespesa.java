package com.example.zennofinancas.classes;

public class clsDespesa {

    private String idGasto;
    private String idUsuario;
    private String idCategoria;
    private String descricaoGasto;
    private String dataGasto;
    private double valorGasto;

    public clsDespesa() {
    }

    public clsDespesa(String idGasto, String idUsuario, String idCategoria, String descricaoGasto, String dataGasto, double valorGasto) {
        this.idGasto = idGasto;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.descricaoGasto = descricaoGasto;
        this.dataGasto = dataGasto;
        this.valorGasto = valorGasto;
    }

    public String getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(String idGasto) {
        this.idGasto = idGasto;
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

    public String getDescricaoGasto() {
        return descricaoGasto;
    }

    public void setDescricaoGasto(String descricaoGasto) {
        this.descricaoGasto = descricaoGasto;
    }

    public String getDataGasto() {
        return dataGasto;
    }

    public void setDataGasto(String dataGasto) {
        this.dataGasto = dataGasto;
    }

    public double getValorGasto() {
        return valorGasto;
    }

    public void setValorGasto(double valorGasto) {
        this.valorGasto = valorGasto;
    }
}
