package com.avanade.livraria.domain;

public class Usuario {
    private Long id;
    private String name;
    private String document;
    private TipoUsuario userType;

    public Usuario(Long id, String name, String document, TipoUsuario userType) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.userType = userType;
    }

    public Usuario(String name, String document, TipoUsuario userType) {
        this(null, name, document, userType);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public TipoUsuario getUserType() { return userType; }
}
