package com.example.zennofinancas.classes;

public class clsAporteObjetivo {

    private String idAporte;
    private String idUsuario;
    private String idObjetivo;
    private double valorAporte;
    private String dataAporte;
    private String descricaoAporte;

    public clsAporteObjetivo() {
    }

    public clsAporteObjetivo(String idAporte, String idUsuario, String idObjetivo,
                             double valorAporte, String dataAporte, String descricaoAporte) {
        this.idAporte = idAporte;
        this.idUsuario = idUsuario;
        this.idObjetivo = idObjetivo;
        this.valorAporte = valorAporte;
        this.dataAporte = dataAporte;
        this.descricaoAporte = descricaoAporte;
    }

    public String getIdAporte() {
        return idAporte;
    }

    public void setIdAporte(String idAporte) {
        this.idAporte = idAporte;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdObjetivo() {
        return idObjetivo;
    }

    public void setIdObjetivo(String idObjetivo) {
        this.idObjetivo = idObjetivo;
    }

    public double getValorAporte() {
        return valorAporte;
    }

    public void setValorAporte(double valorAporte) {
        this.valorAporte = valorAporte;
    }

    public String getDataAporte() {
        return dataAporte;
    }

    public void setDataAporte(String dataAporte) {
        this.dataAporte = dataAporte;
    }

    public String getDescricaoAporte() {
        return descricaoAporte;
    }

    public void setDescricaoAporte(String descricaoAporte) {
        this.descricaoAporte = descricaoAporte;
    }
}
