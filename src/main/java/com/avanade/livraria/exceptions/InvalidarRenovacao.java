package com.avanade.livraria.exceptions;

public class InvalidarRenovacao extends Exception {
    public InvalidarRenovacao(){
        super("O limite de renovações ja foi atingido");
    }
}
