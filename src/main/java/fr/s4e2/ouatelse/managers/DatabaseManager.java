package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.*;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class DatabaseManager {
    private static final String DATABASE_NOT_INITIALIZED_EXCEPTION = "Could not setup the database";
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final ConnectionSource connectionSource;
    private EntityManagerAddress entityManagerAddress;
    private EntityManagerRole entityManagerRole;
    private EntityManagerStore entityManagerStore;
    private EntityManagerUser entityManagerUser;
    private EntityManagerProduct entityManagerProduct;
    private EntityManagerVendor entityManagerVendor;

    public DatabaseManager(String databaseName) {
        try {
            this.connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
            this.setupTables();
            this.setupDao();
            this.fillDatabase();
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, DATABASE_NOT_INITIALIZED_EXCEPTION, exception);
            throw new DatabaseInitialisationException(DATABASE_NOT_INITIALIZED_EXCEPTION);
        }
    }

    public static void deleteDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) return;
        try {
            Files.delete(Paths.get(databaseName));
        } catch (IOException exception) {
            exception.printStackTrace();
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
        this.entityManagerVendor = new EntityManagerVendor(connectionSource);
    }

    public void fillDatabase() throws SQLException {
        this.setupRoles();
        this.setupTestUser();
        this.setupTestStore();
    }

    private void setupTestUser() throws SQLException {
        if (this.connectionSource == null
                || this.entityManagerUser == null
                || this.entityManagerRole == null
        ) return;

        if (this.entityManagerUser.getUserIfExists("test", "test") == null) {
            User user = new User();
            user.setSurname("test");
            user.setName("test");
            user.setMobilePhoneNumber("00 00 00 00 00");
            user.setEmail("test@test.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("test");
            user.setPassword("test");
            user.setRole(this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", "Admin").prepare()).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            this.entityManagerUser.create(user);
        }
    }

    private void setupTestStore() {
        if (this.connectionSource == null || this.entityManagerStore == null) return;

        if (this.entityManagerStore.authGetStoreIfExists("test", "test") == null) {
            Store store = new Store();
            store.setId("test");
            store.setPassword("test");

            this.entityManagerStore.create(store);
        }
    }

    private void setupRoles() throws SQLException {
        final String DIRECTOR_ROLE_NAME = "Director";
        final String ADMIN_ROLE_NAME = "Admin";
        final String BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME = "Responsables des achats et stocks";
        final String SALES_MANAGER_ROLE_NAME = "Responsable des ventes";
        final String HUMAN_RESOURCES_MANAGER_ROLE_NAME = "Responsable des Ressources Humaines";

        if (this.connectionSource == null || this.entityManagerRole == null) return;

        List<Role> temporaryResultsList = this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", DIRECTOR_ROLE_NAME).prepare());
        if (temporaryResultsList.isEmpty()) {
            Role director = this.entityManagerRole.create(DIRECTOR_ROLE_NAME);
            ArrayList<Permission> directorPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.EMPLOYEES_MANAGEMENT,
                    Permission.ROLE_MANAGEMENT,
                    Permission.STORE_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.MONITORING,
                    Permission.EMPLOYEES_MANAGEMENT,
                    Permission.SALARY_MANAGEMENT,
                    Permission.STATISTICS,
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT,
                    Permission.SALES_MANAGEMENT,
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT,
                    Permission.VENDORS_MANAGEMENT
            ));
            director.setPermissions(directorPermission);
            this.entityManagerRole.update(director);
        }

        temporaryResultsList = this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", ADMIN_ROLE_NAME).prepare());
        if (temporaryResultsList.isEmpty()) {
            Role admin = this.entityManagerRole.create(ADMIN_ROLE_NAME);
            ArrayList<Permission> adminPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.EMPLOYEES_MANAGEMENT,
                    Permission.ROLE_MANAGEMENT,
                    Permission.STORE_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.EMPLOYEES_MANAGEMENT,
                    Permission.SALARY_MANAGEMENT,
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT,
                    Permission.SALES_MANAGEMENT,
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT,
                    Permission.VENDORS_MANAGEMENT
            ));
            admin.setPermissions(adminPermission);
            this.entityManagerRole.update(admin);
        }

        temporaryResultsList = this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME).prepare());
        if (temporaryResultsList.isEmpty()) {
            Role buyingsAndStocksManager = this.entityManagerRole.create(BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME);
            ArrayList<Permission> buyingsAndStocksManagerPermission = new ArrayList<>(Arrays.asList(
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT
            ));
            buyingsAndStocksManager.setPermissions(buyingsAndStocksManagerPermission);
            this.entityManagerRole.update(buyingsAndStocksManager);
        }

        temporaryResultsList = this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", SALES_MANAGER_ROLE_NAME).prepare());
        if (temporaryResultsList.isEmpty()) {
            Role salesManager = this.entityManagerRole.create(SALES_MANAGER_ROLE_NAME);
            ArrayList<Permission> salesManagerPermission = new ArrayList<>(Collections.singletonList(
                    Permission.SALES_MANAGEMENT
            ));
            salesManager.setPermissions(salesManagerPermission);
            this.entityManagerRole.update(salesManager);
        }

        temporaryResultsList = this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().where().eq("name", HUMAN_RESOURCES_MANAGER_ROLE_NAME).prepare());
        if (temporaryResultsList.isEmpty()) {
            Role humanResourcesManager = this.entityManagerRole.create(HUMAN_RESOURCES_MANAGER_ROLE_NAME);
            ArrayList<Permission> humanResourcesManagerPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.EMPLOYEES_MANAGEMENT,
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT
            ));
            humanResourcesManager.setPermissions(humanResourcesManagerPermission);
            this.entityManagerRole.update(humanResourcesManager);
        }
    }
}
