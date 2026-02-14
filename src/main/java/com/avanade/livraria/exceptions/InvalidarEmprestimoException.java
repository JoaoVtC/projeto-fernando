package com.avanade.livraria.exceptions;

import com.avanade.livraria.domain.StatusEmprestimo;

public class InvalidarEmprestimoException extends Exception {
    private final String nomeUsuario;
    private final StatusEmprestimo causa;
    
    public InvalidarEmprestimoException(String nomeUsuario, StatusEmprestimo causa) {
        super("O usuário " + nomeUsuario + " não pode realizar emprestimos \n Causa: " + causa.getMensagem());
        this.nomeUsuario = nomeUsuario;
        this.causa = causa;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public StatusEmprestimo getCausa() {
        return causa;
    }
}
