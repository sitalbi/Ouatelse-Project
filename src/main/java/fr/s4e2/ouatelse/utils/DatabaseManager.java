package fr.s4e2.ouatelse.utils;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.Provider;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import lombok.Getter;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseManager {
    @Getter
    private ConnectionSource connectionSource;

    public DatabaseManager() {
        try {
            this.connectionSource = new JdbcConnectionSource("jdbc:sqlite:sqlite.db");
            this.setupTables();
            this.displayTables();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void close() throws IOException {
        connectionSource.close();
    }

    public void setupTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Provider.class);
        TableUtils.createTableIfNotExists(connectionSource, Product.class);
        TableUtils.createTableIfNotExists(connectionSource, Store.class);
    }

    public void displayTables() throws SQLException {
        GenericRawResults<String[]> results =
                DaoManager.createDao(connectionSource, User.class).queryRaw("SELECT name FROM sqlite_master WHERE type = 'table'");
        results.forEach(r -> System.out.println(r[0]));
    }
}
