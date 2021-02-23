package fr.s4e2.ouatelse.utils;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.s4e2.ouatelse.databaseInterface.DatabaseAddressInterface;
import fr.s4e2.ouatelse.databaseInterface.DatabaseRoleInterface;
import fr.s4e2.ouatelse.databaseInterface.DatabaseStoreInterface;
import fr.s4e2.ouatelse.databaseInterface.DatabaseUserInterface;
import fr.s4e2.ouatelse.objects.*;
import lombok.Getter;

import java.io.IOException;
import java.sql.SQLException;

@Getter
public class DatabaseManager {

    private ConnectionSource connectionSource;
    private DatabaseRoleInterface databaseRoleInterface;
    private DatabaseStoreInterface databaseStoreInterface;
    private DatabaseUserInterface databaseUserInterface;
    private DatabaseAddressInterface databaseAddressInterface;

    public DatabaseManager() {
        try {
            this.connectionSource = new JdbcConnectionSource("jdbc:sqlite:sqlite.db");
            this.setupTables();
            this.displayTables();
            this.setupDao();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void close() throws IOException {
        connectionSource.close();
    }

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

    public void setupDao() {
        this.databaseRoleInterface = new DatabaseRoleInterface(this.getConnectionSource());
        this.databaseStoreInterface = new DatabaseStoreInterface(this.getConnectionSource());
        this.databaseUserInterface = new DatabaseUserInterface(this.getConnectionSource());
        this.databaseAddressInterface = new DatabaseAddressInterface(this.getConnectionSource());
    }

    public void displayTables() throws SQLException {
        GenericRawResults<String[]> results =
                DaoManager.createDao(connectionSource, User.class).queryRaw("SELECT name FROM sqlite_master WHERE type = 'table'");
        results.forEach(r -> System.out.println(r[0]));
    }
}
