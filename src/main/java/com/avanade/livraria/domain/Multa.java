package com.avanade.livraria.domain;

import java.math.BigDecimal;

public class Multa {
    private Long id;
    private Long emprestimoId;
    private BigDecimal valor;
    private java.time.LocalDateTime dataPagamento;

    public Multa(Long id, Long emprestimoId, BigDecimal valor, java.time.LocalDateTime dataPagamento) {
        this.id = id;
        this.emprestimoId = emprestimoId;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
    }

    public Multa(Long emprestimoId, BigDecimal valor) {
        this(null, emprestimoId, valor, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmprestimoId() { return emprestimoId; }
    public BigDecimal getValor() { return valor; }
    public java.time.LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(java.time.LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
}
