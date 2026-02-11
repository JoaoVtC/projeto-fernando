package com.avanade.livraria.repository;

import com.avanade.livraria.domain.Usuario;

import java.sql.*;

public class JdbcRepositorioUsuario implements RepositorioUsuario {
    private final Connection conn;

    public JdbcRepositorioUsuario(Connection conn) { this.conn = conn; }

    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO users(name,document,user_type) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getName());
            ps.setString(2, usuario.getDocument());
            ps.setString(3, usuario.getUserType().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getLong(1));
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Usuario findById(Long id) {
        String sql = "SELECT id,name,document,user_type FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario(rs.getLong("id"), rs.getString("name"), rs.getString("document"), com.avanade.livraria.domain.TipoUsuario.valueOf(rs.getString("user_type")));
                    return u;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
