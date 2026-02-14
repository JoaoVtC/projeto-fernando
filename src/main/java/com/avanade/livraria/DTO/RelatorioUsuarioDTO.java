package com.avanade.livraria.DTO;

import java.util.List;

import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;

public class RelatorioUsuarioDTO {
    public String nome;
    public List<Emprestimo> emprestimos;
    public List<Livro> livros;
    public List<Multa> multas;

    public RelatorioUsuarioDTO(String nome, List<Emprestimo> emprestimos, List<Livro> livros,List<Multa> multas){
        this.nome = nome;
        this.emprestimos = emprestimos;
        this.livros = livros;
        this.multas = multas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public List<Multa> getMultas() {
        return multas;
    }

    public void setMultas(List<Multa> multas) {
        this.multas = multas;
    }
}
