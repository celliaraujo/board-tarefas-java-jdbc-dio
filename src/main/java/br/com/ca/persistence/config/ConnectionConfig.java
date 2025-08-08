package br.com.ca.persistence.config;


import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionConfig {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/board";
        String user = "board";
        String password = "board";
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);

        return connection;
    }
}
