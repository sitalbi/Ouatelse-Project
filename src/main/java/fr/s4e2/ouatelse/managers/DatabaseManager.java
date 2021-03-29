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

    private static final String DEFAULT_CLIENT_PHONE_NUMBER = "123456789";
    private static final String DEFAULT_USER_PHONE_NUMBER = "00 00 00 00 00";
    private static final String DEFAULT_USER_ADDRESS = "15 Rue de Naudet";
    private static final String DEFAULT_USER_CITY = "Gradignan";
    private static final String DEFAULT_PASSWORD = "password";

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
        final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

        if (databaseName == null || databaseName.trim().isEmpty()) return;
        try {
            Files.delete(Paths.get(databaseName));
        } catch (IOException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
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
        this.setupTestStores();
        this.setupTestUsers();
        this.setupTestClient();
        this.setupTestVendors();
        this.setupTestProducts();
    }

    /**
     * Fills the database with standard role data
     *
     * @throws SQLException occurs when there is a connection that can't be established
     */
    private void setupRoles() throws SQLException {
        final String DIRECTOR_ROLE_NAME = "Directeur";
        final String ADMIN_ROLE_NAME = "Admin";
        final String BUYINGS_AND_STOCKS_MANAGER_ROLE_NAME = "Responsables des achats et stocks";
        final String SALES_MANAGER_ROLE_NAME = "Responsable des ventes";
        final String HUMAN_RESOURCES_MANAGER_ROLE_NAME = "Responsable des Ressources Humaines";
        final String VENDOR_ROLE_NAME = "Vendeur";

        if (connectionSource == null || entityManagerRole == null) return;

        List<Role> temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", DIRECTOR_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role director = entityManagerRole.create(DIRECTOR_ROLE_NAME);
            ArrayList<Permission> directorPermission = new ArrayList<>(Arrays.asList(Permission.values()));
            director.setPermissions(directorPermission);
            this.entityManagerRole.update(director);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", ADMIN_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role admin = entityManagerRole.create(ADMIN_ROLE_NAME);
            ArrayList<Permission> adminPermission = new ArrayList<>(Arrays.asList(
                    Permission.USER_MANAGEMENT,
                    Permission.ROLE_MANAGEMENT
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
                    Permission.STOCKS_MANAGEMENT,
                    Permission.STATISTICS,
                    Permission.VENDORS_MANAGEMENT
            ));
            buyingsAndStocksManager.setPermissions(buyingsAndStocksManagerPermission);
            this.entityManagerRole.update(buyingsAndStocksManager);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", SALES_MANAGER_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role salesManager = entityManagerRole.create(SALES_MANAGER_ROLE_NAME);
            ArrayList<Permission> salesManagerPermission = new ArrayList<>(Arrays.asList(
                    Permission.SALES_MANAGEMENT,
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.CLIENTS_MANAGEMENT,
                    Permission.MONITORING,
                    Permission.STOCKS_MANAGEMENT
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
                    Permission.USER_MANAGEMENT,
                    Permission.SALARY_MANAGEMENT,
                    Permission.PLANNING_MANAGEMENT
            ));
            humanResourcesManager.setPermissions(humanResourcesManagerPermission);
            this.entityManagerRole.update(humanResourcesManager);
        }

        temporaryResultsList = entityManagerRole.executeQuery(
                entityManagerRole.getQueryBuilder().where().eq("name", VENDOR_ROLE_NAME).prepare()
        );
        if (temporaryResultsList.isEmpty()) {
            Role vendor = entityManagerRole.create(VENDOR_ROLE_NAME);
            ArrayList<Permission> vendorPermission = new ArrayList<>(Arrays.asList(
                    Permission.PRODUCTS_MANAGEMENT,
                    Permission.SALES_MANAGEMENT,
                    Permission.CLIENTS_MANAGEMENT
            ));
            vendor.setPermissions(vendorPermission);
            this.entityManagerRole.update(vendor);
        }
    }

    /**
     * Fills the database with test store data
     */
    private void setupTestStores() {
        if (connectionSource == null || entityManagerStore == null) return;

        if (entityManagerStore.authGetStoreIfExists("Ouatelse Le Haillan", DEFAULT_PASSWORD) == null) {
            Store store = new Store();
            store.setId("Ouatelse Le Haillan");
            store.setPassword(DEFAULT_PASSWORD);

            Address address = new Address();
            address.setStreetNameAndNumber("137 avenue Pasteur");
            address.setCity("Le Haillan");
            address.setZipCode(33185);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }

        if (entityManagerStore.authGetStoreIfExists("Agence Ouatelse Bordeaux", DEFAULT_PASSWORD) == null) {
            Store store = new Store();
            store.setId("Agence Ouatelse Bordeaux");
            store.setPassword(DEFAULT_PASSWORD);

            Address address = new Address();
            address.setStreetNameAndNumber("Place Pey-Berland");
            address.setCity("Bordeaux");
            address.setZipCode(33045);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }

        if (entityManagerStore.authGetStoreIfExists("Ouatelse Paris", DEFAULT_PASSWORD) == null) {
            Store store = new Store();
            store.setId("Ouatelse Paris");
            store.setPassword(DEFAULT_PASSWORD);

            Address address = new Address();
            address.setStreetNameAndNumber("Place de l'Hôtel-de-Ville");
            address.setCity("Paris");
            address.setZipCode(75196);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }

        if (entityManagerStore.authGetStoreIfExists("Ouatelse Arcachon", DEFAULT_PASSWORD) == null) {
            Store store = new Store();
            store.setId("Ouatelse Arcachon");
            store.setPassword(DEFAULT_PASSWORD);

            Address address = new Address();
            address.setStreetNameAndNumber("Place Lucien-de-Gracia");
            address.setCity("Arcachon");
            address.setZipCode(33311);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }

        if (entityManagerStore.authGetStoreIfExists("Ouatelse Nice", DEFAULT_PASSWORD) == null) {
            Store store = new Store();
            store.setId("Ouatelse Nice");
            store.setPassword(DEFAULT_PASSWORD);

            Address address = new Address();
            address.setStreetNameAndNumber("5 rue de l'Hôtel-de-Ville");
            address.setCity("Nice");
            address.setZipCode(6364);

            store.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerStore.create(store);
        }
    }

    /**
     * Fills the database with test user data
     *
     * @throws SQLException occurs when there is a connection that can't be established
     */
    private void setupTestUsers() throws SQLException {
        if (connectionSource == null || entityManagerUser == null || entityManagerRole == null) return;

        if (entityManagerUser.getUserIfExists("almardant", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Mardant");
            user.setName("Alex");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("mardantalex@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("almardant");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Vendeur").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }

        if (entityManagerUser.getUserIfExists("micassiope", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Cassiope");
            user.setName("Michel");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("cassiopemichel@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("micassiope");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Responsables des achats et stocks").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }

        if (entityManagerUser.getUserIfExists("paamidala", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Amidala");
            user.setName("Padmé");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("amidalapadme@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.MME);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("paamidala");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Responsable des ventes").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }

        if (entityManagerUser.getUserIfExists("jijinn", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Jinn");
            user.setName("Qui-Gon");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("jinnquigon@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("jijinn");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Responsable des Ressources Humaines").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }

        if (entityManagerUser.getUserIfExists("jpgeorge", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Georges");
            user.setName("Jean-Pierre");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("georgesouatelse@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("jpgeorge");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Directeur").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }

        if (entityManagerUser.getUserIfExists("kfeine", DEFAULT_PASSWORD) == null) {
            User user = new User();
            user.setSurname("Kfeiné");
            user.setName("M.");
            user.setMobilePhoneNumber(DEFAULT_USER_PHONE_NUMBER);
            user.setEmail("kfeine@gmail.com");
            user.setBirthDate(new Date());
            user.setCivility(Civility.M);
            user.setStatus(PersonState.EMPLOYED);
            user.setCredentials("kfeine");
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole(entityManagerRole.executeQuery(entityManagerRole.getQueryBuilder()
                    .where().eq("name", "Admin").prepare()
            ).stream().findFirst().orElse(null));
            user.setHiringDate(new Date());
            user.setHoursPerWeek(35);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            user.setAddress(address);
            user.setWorkingStore(entityManagerStore.getQueryForAll().stream().findFirst().orElse(null));

            this.entityManagerAddress.create(address);
            this.entityManagerUser.create(user);
        }
    }

    /**
     * Fills the database with test client data
     */
    private void setupTestClient() {
        if (connectionSource == null || entityManagerStore == null) return;

        if (entityManagerClient.getClientIfExists(1, "Valentin", "Caravati") == null) {
            Client client = new Client();
            client.setName("Valentin");
            client.setSurname("Caravati");
            client.setMobilePhoneNumber(DEFAULT_CLIENT_PHONE_NUMBER);
            client.setEmail("valentin.caravati@gmail.com");
            client.setBirthDate(new Date());
            client.setCivility(Civility.M);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            client.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerClient.create(client);
        }

        if (entityManagerClient.getClientIfExists(2, "Lukaz", "Talbi") == null) {
            Client client = new Client();
            client.setName("Lukaz");
            client.setSurname("Talbi");
            client.setMobilePhoneNumber(DEFAULT_CLIENT_PHONE_NUMBER);
            client.setEmail("lukaz.talbi@gmail.com");
            client.setBirthDate(new Date());
            client.setCivility(Civility.M);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            client.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerClient.create(client);
        }

        if (entityManagerClient.getClientIfExists(3, "Matteo", "Volant") == null) {
            Client client = new Client();
            client.setName("Matteo");
            client.setSurname("Volant");
            client.setMobilePhoneNumber(DEFAULT_CLIENT_PHONE_NUMBER);
            client.setEmail("matteo.volant@outlook.com");
            client.setBirthDate(new Date());
            client.setCivility(Civility.M);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            client.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerClient.create(client);
        }

        if (entityManagerClient.getClientIfExists(4, "Simon", "Leroy") == null) {
            Client client = new Client();
            client.setName("Simon");
            client.setSurname("Leroy");
            client.setMobilePhoneNumber(DEFAULT_CLIENT_PHONE_NUMBER);
            client.setEmail("simon.leroy@gmail.com");
            client.setBirthDate(new Date());
            client.setCivility(Civility.M);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            client.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerClient.create(client);
        }
    }

    /**
     * Fills the database with test vendors
     */
    private void setupTestVendors() {
        if (connectionSource == null || entityManagerVendor == null) return;

        if (entityManagerVendor.getVendorIfExists("Nestlé Nespresso S.A.") == null) {
            Vendor vendor = new Vendor();
            vendor.setName("Nestlé Nespresso S.A.");
            vendor.setEmail("nespresso@nestle.com");
            vendor.setContractState(true);
            vendor.setPhoneNumber(DEFAULT_USER_PHONE_NUMBER);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            vendor.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerVendor.create(vendor);
        }

        if (entityManagerVendor.getVendorIfExists("Senseo") == null) {
            Vendor vendor = new Vendor();
            vendor.setName("Senseo");
            vendor.setEmail("vendor@senseo.com");
            vendor.setContractState(false);
            vendor.setPhoneNumber(DEFAULT_USER_PHONE_NUMBER);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            vendor.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerVendor.create(vendor);
        }

        if (entityManagerVendor.getVendorIfExists("Koninklijke Philips N.V.") == null) {
            Vendor vendor = new Vendor();
            vendor.setName("Koninklijke Philips N.V.");
            vendor.setEmail("vendor@philips.com");
            vendor.setContractState(true);
            vendor.setPhoneNumber(DEFAULT_USER_PHONE_NUMBER);

            Address address = new Address();
            address.setStreetNameAndNumber(DEFAULT_USER_ADDRESS);
            address.setCity(DEFAULT_USER_CITY);
            address.setZipCode(33175);

            vendor.setAddress(address);

            this.entityManagerAddress.create(address);
            this.entityManagerVendor.create(vendor);
        }
    }

    /**
     * Fills the database with test products
     */
    private void setupTestProducts() {
        if (connectionSource == null || entityManagerProduct == null) return;

        if (entityManagerProduct.getProductIfExists(195622386) == null) {
            Product product = new Product();
            product.setReference(195622386);
            product.setBarCode("19562236");
            product.setName("SENSEO Select");
            product.setPurchasePrice(85.0);
            product.setBrand("Senseo");
            product.setMargin(0.2);
            product.setTaxes(0.2);
            product.setState(ProductState.IN_STOCK);
            product.setCategory("Machine à Café");
            product.setSoldBy(entityManagerVendor.getVendorIfExists("Senseo"));
            product.setStore(entityManagerStore.getStoreIfExist("Ouatelse Le Haillan"));

            this.entityManagerProduct.create(product);
        }

        if (entityManagerProduct.getProductIfExists(952685135) == null) {
            Product product = new Product();
            product.setReference(952685135);
            product.setBarCode("952685135");
            product.setName("Vertuo PLUS");
            product.setPurchasePrice(199.0);
            product.setBrand("Nespresso");
            product.setMargin(0.2);
            product.setTaxes(0.2);
            product.setState(ProductState.IN_STOCK);
            product.setCategory("Machine à Café");
            product.setSoldBy(entityManagerVendor.getVendorIfExists("Nestlé Nespresso S.A."));
            product.setStore(entityManagerStore.getStoreIfExist("Ouatelse Paris"));

            this.entityManagerProduct.create(product);
        }

        if (entityManagerProduct.getProductIfExists(129846356) == null) {
            Product product = new Product();
            product.setReference(129846356);
            product.setBarCode("129846356");
            product.setName("Series 3200");
            product.setPurchasePrice(569.99);
            product.setBrand("Philips");
            product.setMargin(0.2);
            product.setTaxes(0.2);
            product.setState(ProductState.IN_STOCK);
            product.setCategory("Machine à Café");
            product.setSoldBy(entityManagerVendor.getVendorIfExists("Koninklijke Philips N.V."));
            product.setStore(entityManagerStore.getStoreIfExist("Ouatelse Paris"));

            this.entityManagerProduct.create(product);
        }
    }
}
