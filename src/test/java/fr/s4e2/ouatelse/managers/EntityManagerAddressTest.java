package fr.s4e2.ouatelse.managers;

import fr.s4e2.ouatelse.objects.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerAddressTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerAddress entityManagerAddress;

    @BeforeEach
    void setUp() {
        if (this.databaseManager != null) {
            try {
                this.databaseManager.close();
                DatabaseManager.deleteDatabase(DATABASE_NAME);
                this.databaseManager = new DatabaseManager(DATABASE_NAME);
                this.entityManagerAddress = databaseManager.getEntityManagerAddress();
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
        In both cases, no exception should be thrown as they should be catched by the code
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
            - Address does not exist in the database, so its ID
     */
    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void getAll() {
    }

    @Test
    void executeQuery() {
    }

    @Test
    void getQueryForAll() {
    }

    @Test
    void getQueryBuilder() {
    }
}