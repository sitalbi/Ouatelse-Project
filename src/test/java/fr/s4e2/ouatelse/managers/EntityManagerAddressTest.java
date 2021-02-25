package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerAddressTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerAddress entityManagerAddress;

    private void clearTable() {
        this.entityManagerAddress.getAll().forEachRemaining(address -> this.entityManagerAddress.delete(address));
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerAddress = databaseManager.getEntityManagerAddress();

        clearTable();
    }

    @AfterEach
    void tearDown() {
        clearTable();

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
            - Address is not compliant with database scheme
            - Address is compliant with database scheme
     */
    @Test
    void create() {
        // Address is not compliant
        Address notCompliantAddress = new Address(0, null, null);
        assertDoesNotThrow(() -> this.entityManagerAddress.create(notCompliantAddress));
        // Inserted objects have an ID > 0
        assertFalse(notCompliantAddress.getId() > 0);


        // Address is compliant
        Address compliantAddress = new Address(33000, "Bordeaux", "Some address");
        assertDoesNotThrow(() -> this.entityManagerAddress.create(compliantAddress));
        assertTrue(compliantAddress.getId() > 0);
    }

    /*
        Use cases :
            - Address does not exist in the database, so its ID shouldn't change
            - Address exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Address does not exist in the database
        Address notExistingAddress = new Address(33000, "Bordeaux", "Some address");
        assertFalse(this.entityManagerAddress.exists(notExistingAddress));
        assertDoesNotThrow(() -> this.entityManagerAddress.delete(notExistingAddress));
        assertFalse(this.entityManagerAddress.exists(notExistingAddress));


        // Address exists in the database and is aftewards deleted
        Address existingAddress = new Address(33000, "Bordeaux", "Some address");
        this.entityManagerAddress.create(existingAddress);
        assertTrue(this.entityManagerAddress.exists(existingAddress));

        assertDoesNotThrow(() -> this.entityManagerAddress.delete(existingAddress));

        assertFalse(this.entityManagerAddress.exists(existingAddress));
    }

    /*
        Use cases :
            - Address does not exist in the database, so it shouldn't change
            - Address exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be catched by the code
    */
    @Test
    void update() {
        final String ADDRESS_BEFORE_MODIFICATION = "Some address";
        final String ADDRESS_AFTER_MODIFICATION = "Some other address";


        // Address does not exist in the database
        Address notExistingAddress = new Address(33000, "Bordeaux", ADDRESS_BEFORE_MODIFICATION);
        notExistingAddress.setAddress(ADDRESS_AFTER_MODIFICATION);

        long notExistingAddressIDBeforeModification = notExistingAddress.getId();

        assertDoesNotThrow(() -> this.entityManagerAddress.update(notExistingAddress));

        assertEquals(notExistingAddressIDBeforeModification, notExistingAddress.getId());
        assertNotEquals(notExistingAddress.getAddress(), ADDRESS_BEFORE_MODIFICATION);


        // Address exists in the database and is aftewards deleted
        Address existingAddress = new Address(33000, "Bordeaux", ADDRESS_BEFORE_MODIFICATION);
        this.entityManagerAddress.create(existingAddress);
        existingAddress.setAddress(ADDRESS_AFTER_MODIFICATION);

        this.entityManagerAddress.update(existingAddress);
        long existingAddressIDBeforeModification = existingAddress.getId();

        assertDoesNotThrow(() -> this.entityManagerAddress.delete(existingAddress));
        assertEquals(existingAddressIDBeforeModification, existingAddress.getId());
        assertNotEquals(existingAddress.getAddress(), ADDRESS_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no addresses in the database, so the iterator shouldn't iterate
            - There are addresses in the database, so the iterator should iterate through all the addresses
     */
    @Test
    void getAll() {
        // There are no addresses in the database
        CloseableIterator<Address> addressesIterator = this.entityManagerAddress.getAll();
        boolean areThereResults = addressesIterator.hasNext();
        assertFalse(areThereResults);


        // There are addresses in the database
        List<Address> iteratedAddresses
                = new ArrayList<>();

        Address firstAddress = new Address(33000, "Bordeaux", "Some address");
        Address secondAddress = new Address(24200, "Sarlat", "Some other address");

        this.entityManagerAddress.create(firstAddress);
        this.entityManagerAddress.create(secondAddress);

        addressesIterator = this.entityManagerAddress.getAll();
        assertTrue(addressesIterator.hasNext());
        for (CloseableIterator<Address> it = addressesIterator; it.hasNext(); ) {
            iteratedAddresses.add(it.next());
        }

        assertEquals(iteratedAddresses.size(), 2);
        assertFalse(addressesIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerAddress.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerAddress.executeQuery(this.entityManagerAddress.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no addresses in the database, so the list should be empty
            - There are addresses in the database, so inserted addresses should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no addresses in the database
        List<Address> addressList = this.entityManagerAddress.getQueryForAll();
        assertTrue(addressList.isEmpty());


        // There are addresses in the database
        Address firstAddress = new Address(33000, "Bordeaux", "Some address");
        Address secondAddress = new Address(24200, "Sarlat", "Some other address");

        this.entityManagerAddress.create(firstAddress);
        this.entityManagerAddress.create(secondAddress);

        addressList = this.entityManagerAddress.getQueryForAll();
        assertFalse(addressList.isEmpty());
        assertEquals(addressList.size(), 2);
    }

    /*
        Simple wrapper, nothing should have to be tested
     */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerAddress.getQueryBuilder());
    }

    /*
        Use cases :
            - Address exists in the database
            - Address does not exists in the database
            - Address is null
     */
    @Test
    void exists() {
        // Address exists
        Address existingAddress = new Address(33000, "Bordeaux", "Some address");
        this.entityManagerAddress.create(existingAddress);
        assertTrue(this.entityManagerAddress.exists(existingAddress));

        // Address doesn't exist
        Address nonExistingAddress = new Address(24200, "Sarlat", "Some other address");
        assertFalse(this.entityManagerAddress.exists(nonExistingAddress));

        // Address is null
        assertFalse(this.entityManagerAddress.exists(null));
    }
}