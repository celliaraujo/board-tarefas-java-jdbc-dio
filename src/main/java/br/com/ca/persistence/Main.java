package br.com.ca.persistence;

import br.com.ca.persistence.migration.MigrationStrategy;
import br.com.ca.ui.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.ca.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(Connection connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();

    }
}
