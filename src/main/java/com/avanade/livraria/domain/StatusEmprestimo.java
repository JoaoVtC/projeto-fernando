package com.avanade.livraria.domain;

public enum StatusEmprestimo {
    APROVADO("Empréstimo aprovado com sucesso"),
    LIMITE_EMPRESTIMOS_ATINGIDO("Usuário atingiu o limite de empréstimos ativos"),
    MULTA_PENDENTE("Usuário possui multas pendentes"),
    LIVRO_INDISPONIVEL("Livro não possui cópias disponíveis");

    private final String mensagem;

    StatusEmprestimo(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
