package com.avanade.livraria.repository;

import com.avanade.livraria.domain.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRepositorioLivro implements RepositorioLivro {
    private final Connection conn;

    public JdbcRepositorioLivro(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Livro save(Livro livro) {
        String sql = "INSERT INTO books(title,author,isbn,total_copies,available_copies) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, livro.getTitle());
            ps.setString(2, livro.getAuthor());
            ps.setString(3, livro.getIsbn());
            ps.setInt(4, livro.getTotalCopies());
            ps.setInt(5, livro.getAvailableCopies());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setId(rs.getLong(1));
                }
            }
            return livro;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Livro findById(Long id) {
        String sql = "SELECT id,title,author,isbn,total_copies,available_copies FROM books WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Livro b = new Livro(rs.getLong("id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("total_copies"), rs.getInt("available_copies"));
                    return b;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Livro> findAll() {
        String sql = "SELECT id,title,author,isbn,total_copies,available_copies FROM books";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Livro> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Livro(rs.getLong("id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("total_copies"), rs.getInt("available_copies")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Livro livro) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, total_copies=?, available_copies=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, livro.getTitle());
            ps.setString(2, livro.getAuthor());
            ps.setString(3, livro.getIsbn());
            ps.setInt(4, livro.getTotalCopies());
            ps.setInt(5, livro.getAvailableCopies());
            ps.setLong(6, livro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
