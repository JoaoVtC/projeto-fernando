package com.avanade.livraria;

import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.repository.*;
import com.avanade.livraria.service.ServicoEmprestimo;

import java.sql.Connection;

public class Principal {
    public static void main(String[] args) throws Exception {
        Database.init();
        try (Connection conn = Database.getConnection()) {
            JdbcRepositorioLivro livroRepo = new JdbcRepositorioLivro(conn);
            JdbcRepositorioUsuario usuarioRepo = new JdbcRepositorioUsuario(conn);
            JdbcRepositorioEmprestimo emprestimoRepo = new JdbcRepositorioEmprestimo(conn);

            // Seed
            Livro l1 = new Livro("Clean Code", "Robert C. Martin", "978-0132350884", 3);
            Livro l2 = new Livro("Effective Java", "Joshua Bloch", "978-0134685991", 2);
            livroRepo.save(l1);
            livroRepo.save(l2);

            Usuario u1 = new Usuario("Alice", "111.111.111-11", TipoUsuario.ESTUDANTE);
            Usuario u2 = new Usuario("Professor Bob", "222.222.222-22", TipoUsuario.PROFESSOR);
            usuarioRepo.save(u1);
            usuarioRepo.save(u2);

            ServicoEmprestimo servico = new ServicoEmprestimo(livroRepo, usuarioRepo, emprestimoRepo);
            var emprestimo = servico.criarEmprestimo(u1.getId(), l1.getId());
            System.out.println("Emprestimo criado: id=" + emprestimo.getId() + " due=" + emprestimo.getDueDate());

            System.out.println("Livros atualmente no DB:");
            for (Livro b : livroRepo.findAll()) {
                System.out.println(b.getId() + " - " + b.getTitle() + " (available=" + b.getAvailableCopies() + ")");
            }
        }
    }
}
