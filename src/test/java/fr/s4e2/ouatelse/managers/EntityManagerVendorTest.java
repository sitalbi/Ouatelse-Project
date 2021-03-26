package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.objects.Vendor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains tests for EntityManagerVendor
 */
class EntityManagerVendorTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerVendor entityManagerVendor;

    private Vendor createCompliantVendor() {
        Vendor compliantVendor = new Vendor();

        compliantVendor.setName(UUID.randomUUID().toString());
        compliantVendor.setPhoneNumber("+33 6 00 00 00 00");
        compliantVendor.setEmail(Double.toString(Math.random()));
        compliantVendor.setContractState(true);

        return compliantVendor;
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerVendor = databaseManager.getEntityManagerVendor();

        CloseableIterator<Vendor> iterator = this.entityManagerVendor.getAll();
        iterator.forEachRemaining(vendor -> this.entityManagerVendor.delete(vendor));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Vendor> iterator = this.entityManagerVendor.getAll();
        iterator.forEachRemaining(vendor -> this.entityManagerVendor.delete(vendor));

        if (this.databaseManager != null) {
            try {
                this.databaseManager.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                fail();
            }
        }
    }

    /*
        Use cases :
            - Vendor is not compliant with database scheme
            - Vendor is compliant with database scheme
     */
    @Test
    void create() {
        // Vendor is not compliant
        Vendor notCompliantVendor = new Vendor();
        assertDoesNotThrow(() -> this.entityManagerVendor.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerVendor.exists(notCompliantVendor));


        // Vendor is compliant
        Vendor compliantVendor = createCompliantVendor();
        assertDoesNotThrow(() -> this.entityManagerVendor.create(compliantVendor));
        assertTrue(this.entityManagerVendor.exists(compliantVendor));
    }

    /*
        Use cases :
            - Vendor does not exist in the database, so its ID shouldn't change
            - Vendor exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be caught by the code
     */
    @Test
    void delete() {
        // Vendor does not exist in the database
        Vendor notExistingVendor = createCompliantVendor();
        assertFalse(this.entityManagerVendor.exists(notExistingVendor));
        assertDoesNotThrow(() -> this.entityManagerVendor.delete(notExistingVendor));
        assertFalse(this.entityManagerVendor.exists(notExistingVendor));


        // Vendor exists in the database and is afterwards deleted
        Vendor existingVendor = createCompliantVendor();
        this.entityManagerVendor.create(existingVendor);
        assertTrue(this.entityManagerVendor.exists(existingVendor));
        assertDoesNotThrow(() -> this.entityManagerVendor.delete(existingVendor));
        assertFalse(this.entityManagerVendor.exists(existingVendor));
    }

    /*
        Use cases :
            - Vendor does not exist in the database, so it shouldn't change
            - Vendor exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be catched by the code
    */
    @Test
    void update() {
        final String USER_NAME_BEFORE_MODIFICATION = "Some surname";
        final String USER_NAME_AFTER_MODIFICATION = "Some other surname";


        // Vendor does not exist in the database
        Vendor notExistingVendor = createCompliantVendor();
        notExistingVendor.setName(USER_NAME_AFTER_MODIFICATION);

        long notExistingVendorIDBeforeModification = notExistingVendor.getId();

        assertDoesNotThrow(() -> this.entityManagerVendor.update(notExistingVendor));

        assertEquals(notExistingVendorIDBeforeModification, notExistingVendor.getId());
        assertNotEquals(notExistingVendor.getName(), USER_NAME_BEFORE_MODIFICATION);


        // Vendor exists in the database and is afterwards deleted
        Vendor existingVendor = createCompliantVendor();
        this.entityManagerVendor.create(existingVendor);
        existingVendor.setName(USER_NAME_AFTER_MODIFICATION);

        this.entityManagerVendor.update(existingVendor);
        long existingVendorIDBeforeModification = existingVendor.getId();

        assertEquals(existingVendorIDBeforeModification, existingVendor.getId());
        assertNotEquals(existingVendor.getName(), USER_NAME_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no vendors in the database, so the iterator shouldn't iterate
            - There are vendors in the database, so the iterator should iterate through all the vendors
     */
    @Test
    void getAll() {
        // There are no vendors in the database
        CloseableIterator<Vendor> vendorsIterator = this.entityManagerVendor.getAll();
        boolean areThereResults = vendorsIterator.hasNext();
        assertFalse(areThereResults);


        // There are vendors in the database
        List<Vendor> iteratedVendors = new ArrayList<>();

        this.entityManagerVendor.create(createCompliantVendor());
        this.entityManagerVendor.create(createCompliantVendor());

        vendorsIterator = this.entityManagerVendor.getAll();
        assertTrue(vendorsIterator.hasNext());
        for (CloseableIterator<Vendor> it = vendorsIterator; it.hasNext(); ) {
            iteratedVendors.add(it.next());
        }

        assertEquals(2, iteratedVendors.size());
        assertFalse(vendorsIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() throws SQLException {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerVendor.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerVendor.executeQuery(this.entityManagerVendor.getQueryBuilder().prepare()));
        this.entityManagerVendor.create(createCompliantVendor());
        List<Vendor> results = this.entityManagerVendor.executeQuery(this.entityManagerVendor.getQueryBuilder().prepare());

        assertEquals(1, results.size());
    }

    /*
        Use cases :
            - There are no vendors in the database, so the list should be empty
            - There are vendors in the database, so inserted addresses should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no vendors in the database
        List<Vendor> vendorList = this.entityManagerVendor.getQueryForAll();
        assertTrue(vendorList.isEmpty());


        // There are vendors in the database
        this.entityManagerVendor.create(createCompliantVendor());
        this.entityManagerVendor.create(createCompliantVendor());

        vendorList = this.entityManagerVendor.getQueryForAll();
        assertFalse(vendorList.isEmpty());
        assertEquals(2, vendorList.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerVendor.getQueryBuilder());
    }

    /*
        Use cases :
            - Vendor exists in the database
            - Vendor does not exists in the database
            - Vendor is null
     */
    @Test
    void exists() {
        // Vendor exists
        Vendor existingVendor = createCompliantVendor();
        this.entityManagerVendor.create(existingVendor);
        assertTrue(this.entityManagerVendor.exists(existingVendor));

        // Vendor doesn't exist
        Vendor nonExistingVendor = createCompliantVendor();
        assertFalse(this.entityManagerVendor.exists(nonExistingVendor));

        // Vendor is null
        assertFalse(this.entityManagerVendor.exists(null));
    }
}