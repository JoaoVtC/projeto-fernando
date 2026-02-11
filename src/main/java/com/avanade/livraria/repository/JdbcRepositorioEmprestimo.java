package com.avanade.livraria.repository;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRepositorioEmprestimo implements RepositorioEmprestimo {
    private final Connection conn;
    private JdbcRepositorioLivro livroRepo;
    private JdbcRepositorioUsuario usuarioRepo;

     public JdbcRepositorioEmprestimo(Connection conn) {
        this.conn = conn;
    }
    public JdbcRepositorioEmprestimo(Connection conn, JdbcRepositorioLivro livroRepo,
            JdbcRepositorioUsuario usuarioRepo) {
        this.conn = conn;
        this.livroRepo = livroRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public Emprestimo save(Emprestimo emprestimo) {
        String sql = "INSERT INTO loans(book_id,user_id,loan_date,due_date,return_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, emprestimo.getBookId());
            ps.setLong(2, emprestimo.getUserId());
            ps.setTimestamp(3, Timestamp.valueOf(emprestimo.getLoanDate()));
            ps.setTimestamp(4, Timestamp.valueOf(emprestimo.getDueDate()));
            if (emprestimo.getReturnDate() != null)
                ps.setTimestamp(5, Timestamp.valueOf(emprestimo.getReturnDate()));
            else
                ps.setTimestamp(5, null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    emprestimo.setId(rs.getLong(1));
            }
            return emprestimo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Emprestimo> findById(Long id) {
        String sql = "SELECT id,book_id,user_id,loan_date,due_date,return_date FROM loans WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Emprestimo l = new Emprestimo(rs.getLong("id"), rs.getLong("book_id"), rs.getLong("user_id"),
                            rs.getTimestamp("loan_date").toLocalDateTime(),
                            rs.getTimestamp("due_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") != null ? rs.getTimestamp("return_date").toLocalDateTime()
                                    : null);
                    return Optional.of(l);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveDataDevolucao(Long EmprestimoId, LocalDateTime dataDevolvida) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(dataDevolvida));
            ps.setLong(2, EmprestimoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Emprestimo> findEmprestimosAtivos(){
        String sql = "SELECT * FROM loans WHERE return_date IS NULL ";
        List<Emprestimo> emprestimosAtivos = new ArrayList<>();

         try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                
                Emprestimo emprestimo = new Emprestimo(rs.getLong("book_id"), rs.getLong("user_id"), rs.getTimestamp("loan_date").toLocalDateTime(), rs.getTimestamp("due_date").toLocalDateTime());
                emprestimosAtivos.add(emprestimo);
            }

            return emprestimosAtivos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
}
