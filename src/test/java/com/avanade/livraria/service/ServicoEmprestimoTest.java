package com.avanade.livraria.service;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.DTO.RelatorioUsuarioDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.repository.Database;
import com.avanade.livraria.repository.JdbcRepositorioLivro;
import com.avanade.livraria.repository.JdbcRepositorioMulta;
import com.avanade.livraria.repository.JdbcRepositorioEmprestimo;
import com.avanade.livraria.repository.JdbcRepositorioUsuario;
import com.avanade.livraria.repository.RepositorioMulta;
import com.avanade.livraria.exceptions.InvalidarEmprestimoException;
import com.avanade.livraria.exceptions.InvalidarRenovacao;

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
        servicoMulta = new ServicoMulta(repositorioMulta, usuarioRepo, emprestimoRepo);
        servico = new ServicoEmprestimo(livroRepo, usuarioRepo, emprestimoRepo, servicoMulta, repositorioMulta);
    }

    @Test
    void criarEmprestimoReduzDisponiveis() throws InvalidarEmprestimoException {
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
    void criarEmprestimoComDevolucaoDuasCopias() throws InvalidarEmprestimoException {
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
    void listarEmprestimosAtivos() throws InvalidarEmprestimoException {

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

    @Test
    void relatorioUsuario() throws InvalidarEmprestimoException {
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

        // Devolver o primeiro empréstimo
        Emprestimo emprestimo1 = emprestimoRepo.findById(1L).get();
        LocalDateTime dataDevolvida1 = LocalDateTime.now().plusDays(20);
        emprestimo1 = servico.devolverLivro(emprestimo1.getId(), dataDevolvida1);
        
        // Devolver o segundo empréstimo
        Emprestimo emprestimo2 = emprestimoRepo.findById(2L).get();
        LocalDateTime dataDevolvida2 = LocalDateTime.now().plusDays(15);
        emprestimo2 = servico.devolverLivro(emprestimo2.getId(), dataDevolvida2);
        
        RelatorioUsuarioDTO relatorio = servico.gerarRelatorioUsuario(1L);
        
        // Verificar total de empréstimos
        assertEquals(3, relatorio.emprestimos.size());
        
        // Contar empréstimos devolvidos (com returnDate)
        long devolvidos = relatorio.emprestimos.stream()
            .filter(e -> e.getReturnDate() != null)
            .count();
        assertEquals(2, devolvidos);
        
        // Contar empréstimos ativos (sem returnDate)
        long ativos = relatorio.emprestimos.stream()
            .filter(e -> e.getReturnDate() == null)
            .count();
        assertEquals(1, ativos);
    }
    
    @Test 
    void estudanteUltrapassarLimiteEmprestimo() throws InvalidarEmprestimoException{
        Livro l1 = new Livro("TDD", "Author", "111", 4);
        livroRepo.save(l1);
        Usuario u1 = new Usuario("Tester", "tester@gmail.com", TipoUsuario.ESTUDANTE);
        usuarioRepo.save(u1);

        servico.criarEmprestimo(u1.getId(), l1.getId());
        servico.criarEmprestimo(u1.getId(), l1.getId());
        servico.criarEmprestimo(u1.getId(), l1.getId());
        
        assertThrows(InvalidarEmprestimoException.class, () -> {
            servico.criarEmprestimo(u1.getId(), l1.getId());
        }, "Deveria lançar InvalidarEmprestimoException - estudante com 3 empréstimos ativos");
    }

    @Test
    void professorUltrapassarLimiteEmprestimo() throws InvalidarEmprestimoException{
        Livro l2 = new Livro("Vidas Secas", "Graciliano Ramos", "111", 6);
        livroRepo.save(l2);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.PROFESSOR);
        usuarioRepo.save(u2);

        servico.criarEmprestimo(u2.getId(), l2.getId());
        servico.criarEmprestimo(u2.getId(), l2.getId());
        servico.criarEmprestimo(u2.getId(), l2.getId());
        servico.criarEmprestimo(u2.getId(), l2.getId());
        servico.criarEmprestimo(u2.getId(), l2.getId());
        
        assertThrows(InvalidarEmprestimoException.class, () -> {
            servico.criarEmprestimo(u2.getId(), l2.getId());
        }, "Deveria lançar InvalidarEmprestimoException - professor com 5 empréstimos ativos");
    }

    
    @Test
    void livroSemCopias() throws InvalidarEmprestimoException{
        Livro l2 = new Livro("Vidas Secas", "Graciliano Ramos", "111", 1);
        livroRepo.save(l2);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.PROFESSOR);
        usuarioRepo.save(u2);

        servico.criarEmprestimo(u2.getId(), l2.getId());
        
        assertThrows(InvalidarEmprestimoException.class, () -> {
            servico.criarEmprestimo(u2.getId(), l2.getId());
        }, "Deveria lançar InvalidarEmprestimoException - livro sem cópias disponíveis");
    }

    @Test
    void multaExistente() throws InvalidarEmprestimoException{
        Livro l2 = new Livro("Vidas Secas", "Graciliano Ramos", "111", 6);
        livroRepo.save(l2);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.PROFESSOR);
        usuarioRepo.save(u2);

       Long emprestimoId = servico.criarEmprestimo(u2.getId(), l2.getId()).getId();
       servico.devolverLivro(emprestimoId, LocalDateTime.now().plusDays(35));
       
       assertThrows(InvalidarEmprestimoException.class, () -> {
           servico.criarEmprestimo(u2.getId(), l2.getId());
       }, "Deveria lançar InvalidarEmprestimoException - usuário com multa pendente");
    }

    @Test
    void limiteRenovacoes() throws InvalidarRenovacao, InvalidarEmprestimoException {
        Livro l2 = new Livro("Vidas Secas", "Graciliano Ramos", "111", 6);
        livroRepo.save(l2);
        Usuario u2 = new Usuario("Tester2", "tester2@gmail.com", TipoUsuario.PROFESSOR);
        usuarioRepo.save(u2);

       Emprestimo emprestimo = servico.criarEmprestimo(u2.getId(), l2.getId());
       servico.renovarEmprestimo(emprestimo.getId());
       servico.renovarEmprestimo(emprestimo.getId());

        Emprestimo emprestimoNovo = emprestimoRepo.findById(emprestimo.getId()).get();
        assertEquals(2, emprestimoNovo.getRenovacoes());
        assertThrows(InvalidarRenovacao.class, () -> {
                  servico.renovarEmprestimo(emprestimoNovo.getId());
       }, "Deveria lançar InvalidarRenovacao - usuário com multa pendente");

    }

}
