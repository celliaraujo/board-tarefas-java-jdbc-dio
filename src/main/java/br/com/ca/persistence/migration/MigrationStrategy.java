package br.com.ca.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

@AllArgsConstructor
public class MigrationStrategy {
    private final Connection connection;

    public void executeMigration(){
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try(FileOutputStream fos = new FileOutputStream("Liquibase.log")){
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));
            try(JdbcConnection jdbcConnection = new JdbcConnection(this.connection)
)           {
                Liquibase liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection);
                liquibase.update();

            } catch (LiquibaseException e) {
                e.printStackTrace();
            }
            System.setErr(new PrintStream(originalErr));

        }
        catch (IOException ex){
            ex.printStackTrace();
        }finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

    }
}
