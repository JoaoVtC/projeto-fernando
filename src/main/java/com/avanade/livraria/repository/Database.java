package com.avanade.livraria.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:h2:mem:library;DB_CLOSE_DELAY=-1";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public static void init() throws SQLException {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS books (id IDENTITY PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), isbn VARCHAR(50), total_copies INT, available_copies INT)");
            s.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY PRIMARY KEY, name VARCHAR(255), document VARCHAR(100), user_type VARCHAR(20))");
            s.execute("CREATE TABLE IF NOT EXISTS loans (id IDENTITY PRIMARY KEY, book_id BIGINT, user_id BIGINT, loan_date TIMESTAMP, due_date TIMESTAMP, return_date TIMESTAMP, renovacoes INT)");
            s.execute("CREATE TABLE IF NOT EXISTS multas (id IDENTITY PRIMARY KEY, emprestimoId BIGINT, valor DECIMAL(10,2), dataPagamento TIMESTAMP)");

        }
    }
}
