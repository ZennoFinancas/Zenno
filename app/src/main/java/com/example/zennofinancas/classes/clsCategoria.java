package com.example.zennofinancas.classes;

public class clsCategoria {
    private String idCategoria;
    private String nomeCategoria;
    private String idUsuario;

    public clsCategoria(String idCategoria, String nomeCategoria, String idUsuario) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.idUsuario = idUsuario;
    }

    public clsCategoria() {}

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
