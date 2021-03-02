package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.s4e2.ouatelse.objects.*;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.SQLException;

@Getter
public class DatabaseManager {

    private ConnectionSource connectionSource;

    private EntityManagerAddress entityManagerAddress;
    private EntityManagerRole entityManagerRole;
    private EntityManagerStore entityManagerStore;
    private EntityManagerUser entityManagerUser;
    private EntityManagerProduct entityManagerProduct;

    public DatabaseManager(String databaseName) {
        try {
            this.connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
            this.setupTables();
            this.setupDao();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public static void deleteDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) return;
        try {
            Files.delete(Paths.get(databaseName));
            System.out.format("[P_INFO] %s deleted%n", databaseName);
        } catch (NoSuchFileException e) {
            System.out.format("[P_INFO] %s does not exist%n", databaseName);
        } catch (IOException e) {
            System.err.format("[P_ERROR] Couldn't delete %s%n", databaseName);
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
        TableUtils.createTableIfNotExists(connectionSource, Order.class);
        TableUtils.createTableIfNotExists(connectionSource, Product.class);
        TableUtils.createTableIfNotExists(connectionSource, ProductStock.class);
        TableUtils.createTableIfNotExists(connectionSource, Role.class);
        TableUtils.createTableIfNotExists(connectionSource, Salary.class);
        TableUtils.createTableIfNotExists(connectionSource, Store.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Vendor.class);
    }

    public void setupDao() {
        this.entityManagerAddress = new EntityManagerAddress(connectionSource);
        this.entityManagerRole = new EntityManagerRole(connectionSource);
        this.entityManagerStore = new EntityManagerStore(connectionSource);
        this.entityManagerUser = new EntityManagerUser(connectionSource);
        this.entityManagerProduct = new EntityManagerProduct(connectionSource);
    }
}
