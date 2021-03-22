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

/**
 * Manages the Database connection and the creation of tables
 */
@Getter
public class DatabaseManager {
    private static final String DATABASE_NOT_INITIALIZED_EXCEPTION = "Could not setup the database";

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final ConnectionSource connectionSource;

    /**
     * Constructs the DatabaseManager
     */
    private EntityManagerProductStock entityManagerProductStock;
    private EntityManagerAddress entityManagerAddress;
    private EntityManagerRole entityManagerRole;
    private EntityManagerStore entityManagerStore;
    private EntityManagerUser entityManagerUser;
    private EntityManagerProduct entityManagerProduct;
    private EntityManagerVendor entityManagerVendor;
    private EntityManagerScheduledOrder entityManagerScheduledOrder;
    private EntityManagerClient entityManagerClient;
    private EntityManagerCart entityManagerCart;
    private EntityManagerClientStock entityManagerClientStock;
    private EntityManagerSalary entityManagerSalary;

    /**
     * Constructs the DatabaseManager
     */
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

    /**
     * Deletes a database by its name
     *
     * @param databaseName database name to delete
     */
    public static void deleteDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) return;
        try {
            Files.delete(Paths.get(databaseName));
        } catch (IOException exception) {
            exception.printStackTrace();
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
        TableUtils.createTableIfNotExists(connectionSource, Order.class);
        TableUtils.createTableIfNotExists(connectionSource, Product.class);
        TableUtils.createTableIfNotExists(connectionSource, ProductStock.class);
        TableUtils.createTableIfNotExists(connectionSource, Role.class);
        TableUtils.createTableIfNotExists(connectionSource, Salary.class);
        TableUtils.createTableIfNotExists(connectionSource, Store.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Vendor.class);
        TableUtils.createTableIfNotExists(connectionSource, ScheduledOrder.class);
        TableUtils.createTableIfNotExists(connectionSource, ClientStock.class);
    }

    /**
     * Sets up all the Entity Managers
     */
    public void setupDao() {
        this.entityManagerAddress = new EntityManagerAddress(connectionSource);
        this.entityManagerRole = new EntityManagerRole(connectionSource);
        this.entityManagerStore = new EntityManagerStore(connectionSource);
        this.entityManagerUser = new EntityManagerUser(connectionSource);
        this.entityManagerProduct = new EntityManagerProduct(connectionSource);
        this.entityManagerVendor = new EntityManagerVendor(connectionSource);
        this.entityManagerProductStock = new EntityManagerProductStock(connectionSource);
        this.entityManagerScheduledOrder = new EntityManagerScheduledOrder(connectionSource);
        this.entityManagerClient = new EntityManagerClient(connectionSource);
        this.entityManagerCart = new EntityManagerCart(connectionSource);
        this.entityManagerClientStock = new EntityManagerClientStock(connectionSource);
        this.entityManagerSalary = new EntityManagerSalary(connectionSource);
    }

    /**
     * Fills the database with default data
     *
     * @throws SQLException occurs when there is a connection that can't be established
     */
    public void fillDatabase() throws SQLException {
        this.setupRoles();
        this.setupTestStore();
        this.setupTestUser();
    }

    /**
     * Fills the database with standard user data
     *
     * @throws SQLException occurs when there is a connection that can't be established
     */
    private void setupTestUser() throws SQLException {
        if (this.connectionSource == null || this.entityManagerUser == null || this.entityManagerRole == null) return;

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
            user.setRole(entityManagerRole.executeQuery(
                    entityManagerRole.getQueryBuilder().where().eq("name", "Admin").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setAddress("Test Address");
            address.setCity("Test City");
            address.setZipCode(0);

            user.setAddress(address);
            user.setWorkingStore(this.entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }
    }

    /**
     * Fills the database with standard store data
     */
    private void setupTestStore() {
        if (this.connectionSource == null || this.entityManagerStore == null) return;

        if (this.entityManagerStore.authGetStoreIfExists("test", "test") == null) {
            Store store = new Store();
            store.setId("test");
            store.setPassword("test");

            Address address = new Address();
            address.setAddress("Test Address");
            address.setCity("Test City");
            address.setZipCode(0);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }
    }

    /**
     * Fills the database with standard role data
     *
     * @throws SQLException occurs when there is a connection that can't be established
     */
    private void setupRoles() throws SQLException {
        final String DIRECTOR_ROLE_NAME = "Director";
        final String ADMIN_ROLE_NAME = "Admin";
        final String BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME = "Responsables des achats et stocks";
        final String SALES_MANAGER_ROLE_NAME = "Responsable des ventes";
        final String HUMAN_RESOURCES_MANAGER_ROLE_NAME = "Responsable des Ressources Humaines";

        if (this.connectionSource == null || this.entityManagerRole == null) return;

        List<Role> temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", DIRECTOR_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role director = entityManagerRole.create(DIRECTOR_ROLE_NAME);
            ArrayList<Permission> directorPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.ROLE_MANAGEMENT,
                    Permission.STORE_MANAGEMENT,
                    Permission.MONITORING,
                    Permission.SALARY_MANAGEMENT,
                    Permission.STATISTICS,
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT,
                    Permission.SALES_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT,
                    Permission.VENDORS_MANAGEMENT
            ));
            director.setPermissions(directorPermission);
            this.entityManagerRole.update(director);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", ADMIN_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role admin = entityManagerRole.create(ADMIN_ROLE_NAME);
            ArrayList<Permission> adminPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.ROLE_MANAGEMENT,
                    Permission.STORE_MANAGEMENT,
                    Permission.SALARY_MANAGEMENT,
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT,
                    Permission.SALES_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT,
                    Permission.VENDORS_MANAGEMENT
            ));
            admin.setPermissions(adminPermission);
            this.entityManagerRole.update(admin);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role buyingsAndStocksManager = entityManagerRole.create(BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME);
            ArrayList<Permission> buyingsAndStocksManagerPermission = new ArrayList<>(Arrays.asList(
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.STOCKS_MANAGEMENT
            ));
            buyingsAndStocksManager.setPermissions(buyingsAndStocksManagerPermission);
            this.entityManagerRole.update(buyingsAndStocksManager);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", SALES_MANAGER_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role salesManager = entityManagerRole.create(SALES_MANAGER_ROLE_NAME);
            ArrayList<Permission> salesManagerPermission = new ArrayList<>(Collections.singletonList(
                    Permission.SALES_MANAGEMENT
            ));
            salesManager.setPermissions(salesManagerPermission);
            this.entityManagerRole.update(salesManager);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", HUMAN_RESOURCES_MANAGER_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role humanResourcesManager = entityManagerRole.create(HUMAN_RESOURCES_MANAGER_ROLE_NAME);
            ArrayList<Permission> humanResourcesManagerPermission = new ArrayList<>(Arrays.asList(
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.USER_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT
            ));
            humanResourcesManager.setPermissions(humanResourcesManagerPermission);
            this.entityManagerRole.update(humanResourcesManager);
        }
    }
}
