package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Civility;
import fr.s4e2.ouatelse.objects.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerClientTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerClient entityManagerClient;

    private Client createCompliantClient() {
        Client compliantClient = new Client();

        compliantClient.setName("Some name");
        compliantClient.setSurname("Some surname");
        compliantClient.setMobilePhoneNumber("+33 6 00 00 00 00");
        compliantClient.setEmail(Double.toString(Math.random()));
        compliantClient.setBirthDate(new Date());
        compliantClient.setCivility(Civility.M);
        compliantClient.setWorkPhoneNumber(compliantClient.getMobilePhoneNumber());
        compliantClient.setHomePhoneNumber(compliantClient.getMobilePhoneNumber());

        return compliantClient;
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerClient = databaseManager.getEntityManagerClient();

        CloseableIterator<Client> iterator = this.entityManagerClient.getAll();
        iterator.forEachRemaining(client -> this.entityManagerClient.delete(client));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Client> iterator = this.entityManagerClient.getAll();
        iterator.forEachRemaining(client -> this.entityManagerClient.delete(client));

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
            - Client is not compliant with database scheme
            - Client is compliant with database scheme
     */
    @Test
    void create() {
        // Client is not compliant
        Client notCompliantClient = new Client();
        assertDoesNotThrow(() -> this.entityManagerClient.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerClient.exists(notCompliantClient));


        // Client is compliant
        Client compliantClient = createCompliantClient();
        assertDoesNotThrow(() -> this.entityManagerClient.create(compliantClient));
        assertTrue(this.entityManagerClient.exists(compliantClient));
    }

    /*
        Use cases :
            - Client does not exist in the database, so its ID shouldn't change
            - Client exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Client does not exist in the database
        Client notExistingClient = createCompliantClient();
        assertFalse(this.entityManagerClient.exists(notExistingClient));
        assertDoesNotThrow(() -> this.entityManagerClient.delete(notExistingClient));
        assertFalse(this.entityManagerClient.exists(notExistingClient));


        // Client exists in the database and is afterwards deleted
        Client existingClient = createCompliantClient();
        this.entityManagerClient.create(existingClient);
        assertTrue(this.entityManagerClient.exists(existingClient));
        assertDoesNotThrow(() -> this.entityManagerClient.delete(existingClient));
        assertFalse(this.entityManagerClient.exists(existingClient));
    }

    /*
        Use cases :
            - Client does not exist in the database, so it shouldn't change
            - Client exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final String CLIENT_SURNAME_BEFORE_MODIFICATION = "Some surname";
        final String CLIENT_SURNAME_AFTER_MODIFICATION = "Some other surname";


        // Client does not exist in the database
        Client notExistingClient = createCompliantClient();
        notExistingClient.setSurname(CLIENT_SURNAME_AFTER_MODIFICATION);

        long notExistingClientIDBeforeModification = notExistingClient.getId();

        assertDoesNotThrow(() -> this.entityManagerClient.update(notExistingClient));

        assertEquals(notExistingClientIDBeforeModification, notExistingClient.getId());
        assertNotEquals(notExistingClient.getSurname(), CLIENT_SURNAME_BEFORE_MODIFICATION);


        // Client exists in the database and is afterwards deleted
        Client existingClient = createCompliantClient();
        this.entityManagerClient.create(existingClient);
        existingClient.setSurname(CLIENT_SURNAME_AFTER_MODIFICATION);

        this.entityManagerClient.update(existingClient);
        long existingClientIDBeforeModification = existingClient.getId();

        assertEquals(existingClientIDBeforeModification, existingClient.getId());
        assertNotEquals(existingClient.getSurname(), CLIENT_SURNAME_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no clients in the database, so the iterator shouldn't iterate
            - There are clients in the database, so the iterator should iterate through all the clients
     */
    @Test
    void getAll() {
        // There are no clients in the database
        CloseableIterator<Client> clientsIterator = this.entityManagerClient.getAll();
        boolean areThereResults = clientsIterator.hasNext();
        assertFalse(areThereResults);


        // There are clients in the database
        List<Client> iteratedClients = new ArrayList<>();

        this.entityManagerClient.create(createCompliantClient());
        this.entityManagerClient.create(createCompliantClient());

        clientsIterator = this.entityManagerClient.getAll();
        assertTrue(clientsIterator.hasNext());
        for (CloseableIterator<Client> it = clientsIterator; it.hasNext(); ) {
            iteratedClients.add(it.next());
        }

        assertEquals(2, iteratedClients.size());
        assertFalse(clientsIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerClient.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerClient.executeQuery(this.entityManagerClient.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no clients in the database, so the list should be empty
            - There are clients in the database, so inserted clients should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no clients in the database
        List<Client> clientList = this.entityManagerClient.getQueryForAll();
        assertTrue(clientList.isEmpty());


        // There are clients in the database
        this.entityManagerClient.create(createCompliantClient());
        this.entityManagerClient.create(createCompliantClient());

        clientList = this.entityManagerClient.getQueryForAll();
        assertFalse(clientList.isEmpty());
        assertEquals(2, clientList.size());
    }

    /*
       Simple wrapper, nothing should have to be tested
   */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerClient.getQueryBuilder());
    }


    /*
        Use cases :
            - Client exists in the database
            - Client does not exists in the database
            - Client is null
    */
    @Test
    void exists() {
        // Client exists
        Client existingClient = createCompliantClient();
        this.entityManagerClient.create(existingClient);
        assertTrue(this.entityManagerClient.exists(existingClient));

        // Client doesn't exist
        Client nonExistingClient = createCompliantClient();
        assertFalse(this.entityManagerClient.exists(nonExistingClient));

        // Client is null
        assertFalse(this.entityManagerClient.exists(null));
    }
}