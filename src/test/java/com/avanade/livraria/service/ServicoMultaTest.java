package com.avanade.livraria.service;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.DTO.UsuarioMultaDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.repository.Database;
import com.avanade.livraria.repository.JdbcRepositorioLivro;
import com.avanade.livraria.repository.JdbcRepositorioMulta;
import com.avanade.livraria.repository.JdbcRepositorioEmprestimo;
import com.avanade.livraria.repository.JdbcRepositorioUsuario;
import com.avanade.livraria.repository.RepositorioLivro;
import com.avanade.livraria.repository.RepositorioMulta;
import com.avanade.livraria.exceptions.InvalidarEmprestimoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;

public class ServicoMultaTest {
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
        usuarioRepo = new JdbcRepositorioUsuario(conn);
        livroRepo = new JdbcRepositorioLivro(conn);
        emprestimoRepo = new JdbcRepositorioEmprestimo(conn, livroRepo, usuarioRepo);
        repositorioMulta = new JdbcRepositorioMulta(conn);
        servicoMulta = new ServicoMulta(repositorioMulta,usuarioRepo, emprestimoRepo);
        servico = new ServicoEmprestimo(livroRepo, usuarioRepo, emprestimoRepo, servicoMulta);
       
    }

    @Test
    void devolverLivroNoPrazo() throws InvalidarEmprestimoException {
        Livro l1 = new Livro("TDD", "Author", "111", 2);
        livroRepo.save(l1);
        Usuario u1 = new Usuario("Tester", "000.000.000-00", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);

        servico.criarEmprestimo(u1.getId(), l1.getId());

        servico.devolverLivro(1L, LocalDateTime.now());
        Multa multa = repositorioMulta.findByEmprestimoId(1L);
        assertNull(multa);
    }

    @Test
    void devolverLivroAlemDoPrazo() throws InvalidarEmprestimoException {
        Livro l1 = new Livro("TDD", "Author", "111", 2);
        livroRepo.save(l1);
        Usuario u1 = new Usuario("Tester", "000.000.000-00", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);

        servico.criarEmprestimo(u1.getId(), l1.getId());

        servico.devolverLivro(1L, LocalDateTime.now().plusDays(20));
        Multa multa = repositorioMulta.findByEmprestimoId(1L);
        assertNotNull(multa);
    }

    @Test
    void verificarMultasExistentes() throws InvalidarEmprestimoException {
        Livro l1 = new Livro("TDD", "Author", "111", 3);
        livroRepo.save(l1);
        Usuario u1 = new Usuario("Tester", "tester@gmail.com", TipoUsuario.ESTUDANTE);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);
        usuarioRepo.save(u2);

        servico.criarEmprestimo(u1.getId(), l1.getId());
        servico.criarEmprestimo(u2.getId(), l1.getId());
        

        Emprestimo emprestimoU1 = emprestimoRepo.findById(1L).get();
        Emprestimo emprestimoU2 = emprestimoRepo.findById(2L).get();

        LocalDateTime dataDevolvida = LocalDateTime.now().plusDays(20);
        servico.devolverLivro(emprestimoU1.getId(), dataDevolvida);
        servico.devolverLivro(emprestimoU2.getId(), dataDevolvida);

        List<UsuarioMultaDTO> inadimplentes = servicoMulta.listarUsuariosComMultas();
        

        Boolean foundU1 = inadimplentes.stream()
            .anyMatch(u -> u.getEmail() == "tester@gmail.com");

        Boolean foundU2 = inadimplentes.stream()
            .anyMatch(u -> u.getEmail() == "tester2@gmail.com");

        assertTrue(foundU1 && foundU2);
        
    }

    @Test 
    void verificarMultasNaoPagas() throws InvalidarEmprestimoException {

        Livro l1 = new Livro("TDD", "Author", "111", 3);
        livroRepo.save(l1);
        Usuario u1 = new Usuario("Tester", "tester@gmail.com", TipoUsuario.ESTUDANTE);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);
        usuarioRepo.save(u2);

        servico.criarEmprestimo(u1.getId(), l1.getId());
        servico.criarEmprestimo(u2.getId(), l1.getId());
        

        Emprestimo emprestimoU1 = emprestimoRepo.findById(1L).get();
        Emprestimo emprestimoU2 = emprestimoRepo.findById(2L).get();

        LocalDateTime dataDevolvida = LocalDateTime.now().plusDays(20);
        servico.devolverLivro(emprestimoU1.getId(), dataDevolvida);
        servico.devolverLivro(emprestimoU2.getId(), dataDevolvida);

        List<UsuarioMultaDTO> inadiplestemtes = servicoMulta.listarUsuariosComMultas();
        

        Boolean foundU1 = inadiplestemtes.stream()
            .anyMatch(u -> u.getEmail() == "tester@gmail.com");

        Boolean foundU2 = inadiplestemtes.stream()
            .anyMatch(u -> u.getEmail() == "tester2@gmail.com");

        assertEquals(2, repositorioMulta.findMultasNaoPagas().size());

    }
}
