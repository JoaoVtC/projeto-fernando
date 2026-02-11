package com.avanade.livraria.service;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.repository.Database;
import com.avanade.livraria.repository.JdbcRepositorioLivro;
import com.avanade.livraria.repository.JdbcRepositorioMulta;
import com.avanade.livraria.repository.JdbcRepositorioEmprestimo;
import com.avanade.livraria.repository.JdbcRepositorioUsuario;
import com.avanade.livraria.repository.RepositorioMulta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoEmprestimoTest {
    private Connection conn;
    private JdbcRepositorioLivro livroRepo;
    private JdbcRepositorioUsuario usuarioRepo;
    private JdbcRepositorioEmprestimo emprestimoRepo;
    private ServicoEmprestimo servico;
    private ServicoMulta servicoMulta;
    private RepositorioMulta repositorioMulta;

    @BeforeEach
    void setup() throws Exception {
        Database.init();
        conn = Database.getConnection();
        livroRepo = new JdbcRepositorioLivro(conn);
        usuarioRepo = new JdbcRepositorioUsuario(conn);
        emprestimoRepo = new JdbcRepositorioEmprestimo(conn, livroRepo, usuarioRepo);
        repositorioMulta = new JdbcRepositorioMulta(conn);
        servicoMulta = new ServicoMulta(repositorioMulta);
        servico = new ServicoEmprestimo(livroRepo, usuarioRepo, emprestimoRepo, servicoMulta);
    }

    @Test
    void criarEmprestimoReduzDisponiveis() {
        Livro l = new Livro("TDD", "Author", "111", 1);
        livroRepo.save(l);
        Usuario u = new Usuario("Tester", "000.000.000-00", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u);

        var emprestimo = servico.criarEmprestimo(u.getId(), l.getId());
        assertNotNull(emprestimo.getId());
        var updated = livroRepo.findById(l.getId());
        assertEquals(0, updated.getAvailableCopies());
    } 

    @Test
    void criarEmprestimoComDevolucaoDuasCopias() {
        Livro l = new Livro("TDD", "Author", "111", 2);
        livroRepo.save(l);
        Usuario u = new Usuario("Tester", "000.000.000-00", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u);

        var emprestimo = servico.criarEmprestimo(u.getId(), l.getId());
        assertNotNull(emprestimo.getId());
        var updated = livroRepo.findById(l.getId());
        assertEquals(1, updated.getAvailableCopies());
    }    

    @Test
    void listarEmprestimosAtivos(){

        Livro l1 = new Livro("TDD", "Author", "111", 3);
        livroRepo.save(l1);
        Livro l2 = new Livro("A Metamorofose", "Franz Kafka", "111", 2);
        livroRepo.save(l2);
        Livro l3 = new Livro("Meditações", "Marco Aurélio", "111", 2);
        livroRepo.save(l3);

        Usuario u1 = new Usuario("Tester", "000.000.000-00", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);

        servico.criarEmprestimo(u1.getId(), l1.getId());
        servico.criarEmprestimo(u1.getId(), l2.getId());
        servico.criarEmprestimo(u1.getId(), l3.getId());

        Emprestimo emprestimo = emprestimoRepo.findById(1L).get();

        LocalDateTime dataDevolvida = LocalDateTime.now().plusDays(20);
        emprestimo = servico.devolverLivro(emprestimo.getId(), dataDevolvida);
        
        List<EmprestimoDTO> listed = servico.listarEmprestimosAtivos();
        assertEquals(2, listed.size());
    }
    
}
