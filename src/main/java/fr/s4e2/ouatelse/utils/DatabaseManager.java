package fr.s4e2.ouatelse.utils;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.s4e2.ouatelse.objects.*;
import lombok.Getter;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Manages the Database connection and the creation of tables
 */
public class DatabaseManager {

    @Getter
    private ConnectionSource connectionSource;

    /**
     * Constructs the DatabaseManager
     */
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

    /**
     * Closes the connection source
     *
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
     */
    public void close() throws IOException {
        connectionSource.close();
    }

    /**
     * Sets up all the necessary tables
     *
     * @throws SQLException occurs when there is a connection that can't be made
     */
    public void setupTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Address.class);
        TableUtils.createTableIfNotExists(connectionSource, Availability.class);
        TableUtils.createTableIfNotExists(connectionSource, Cart.class);
        TableUtils.createTableIfNotExists(connectionSource, Client.class);
        TableUtils.createTableIfNotExists(connectionSource, Product.class);
        TableUtils.createTableIfNotExists(connectionSource, Role.class);
        TableUtils.createTableIfNotExists(connectionSource, Salary.class);
        TableUtils.createTableIfNotExists(connectionSource, Store.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Vendor.class);
    }

    /**
     * Displays all of the tables in the sqlite database
     *
     * @throws SQLException occurs when there is a connection that can't be made
     */
    public void displayTables() throws SQLException {
        GenericRawResults<String[]> results =
                DaoManager.createDao(connectionSource, User.class).queryRaw("SELECT name FROM sqlite_master WHERE type = 'table'");
        results.forEach(r -> System.out.println(r[0]));
    }
}
