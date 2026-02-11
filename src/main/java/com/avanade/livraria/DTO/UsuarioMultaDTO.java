package com.avanade.livraria.DTO;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;

public class UsuarioMultaDTO {
    public String nome;
    public String email;
    public BigDecimal multa;
    public LocalDateTime dataMulta;

    public UsuarioMultaDTO(String nome, String email, BigDecimal multa,LocalDateTime dataMulta){
        this.nome = nome;
        this.email = email;
        this.nome = nome;
        this.multa = multa;
        this.dataMulta = dataMulta;
    }

    public String getEmail(){
        return this.email;
    }
    
}
