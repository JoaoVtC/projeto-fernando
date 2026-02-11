package com.avanade.livraria.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;
import java.sql.Statement;

import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;

public class JdbcRepositorioMulta implements RepositorioMulta {

    private final Connection conn;

    public JdbcRepositorioMulta(Connection conn) { this.conn = conn; }


    @Override
    public Multa save(Multa multa) {
        String sql = "INSERT INTO multas(emprestimoId, valor, dataPagamento) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, multa.getEmprestimoId());
            ps.setBigDecimal(2, multa.getValor());
           ps.setTimestamp(3, multa.getDataPagamento() != null ? Timestamp.valueOf(multa.getDataPagamento()) : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) multa.setId(rs.getLong(1));
            }
            return multa;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Multa findById(Long id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Multa multa = new Multa(rs.getLong("id"), rs.getLong("emprestimoId"), rs.getBigDecimal("valor"), 
                    rs.getTimestamp("dataPagamento").toLocalDateTime());
                    return multa;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       
    }

    @Override
     public Multa findByEmprestimoId(Long id) {
        String sql = "SELECT * FROM multas WHERE emprestimoId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Multa multa = new Multa(rs.getLong("id"), rs.getLong("emprestimoId"), rs.getBigDecimal("valor"), 
                    rs.getTimestamp("dataPagamento").toLocalDateTime());
                    return multa;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       
    }

    @Override
    public List<Multa> findMultasNaoPagas(){
            String sql = "SELECT * FROM multas WHERE dataPagamento IS NULL";
            List<Multa> multas = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                Multa multa = new Multa(rs.getLong("id"), rs.getLong("emprestimoID"), rs.getBigDecimal("valor"), null);
                multas.add(multa);
                }
            }
            return multas;
        }
            
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


