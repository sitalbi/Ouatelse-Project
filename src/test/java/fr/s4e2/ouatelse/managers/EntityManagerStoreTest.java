package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Store;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerStoreTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerStore entityManagerStore;

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerStore = databaseManager.getEntityManagerStore();

        CloseableIterator<Store> iterator = this.entityManagerStore.getAll();
        iterator.forEachRemaining(store -> this.entityManagerStore.delete(store));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Store> iterator = this.entityManagerStore.getAll();
        iterator.forEachRemaining(store -> this.entityManagerStore.delete(store));

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
            - Store is not compliant with database scheme
            - Store is compliant with database scheme
     */
    @Test
    void create() {
        // Store compliant
        Store store = new Store("store");
        store.setPassword("test");
        assertDoesNotThrow(() -> this.entityManagerStore.create(store));

        // Store not compliant (password is null)
        Store store2 = new Store("store2");
        assertDoesNotThrow(() -> this.entityManagerStore.create(store2));
    }

    /*
        Use cases:
            - the store we want to delete exists
            - the store we want to delete doesn't exist
     */
    @Test
    void delete() {
        // The store does not exist before delete
        Store storeDoesNotExist = new Store("theStoreDoesNotExist");
        assertFalse(this.entityManagerStore.exists(storeDoesNotExist));

        assertDoesNotThrow(() -> this.entityManagerStore.delete(storeDoesNotExist));

        assertFalse(this.entityManagerStore.exists(storeDoesNotExist));


        // The store exists before delete
        Store storeExists = new Store("theStoreExists");
        storeExists.setPassword("test");
        this.entityManagerStore.create(storeExists);
        assertTrue(this.entityManagerStore.exists(storeExists));

        assertDoesNotThrow(() -> this.entityManagerStore.delete(storeExists));

        assertFalse(this.entityManagerStore.exists(storeExists));

    }

    /*
        Use cases :
            - Store does not exist in the database, so it shouldn't change
            - Store exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be catched by the code
    */
    @Test
    void update() {
        final String STORE_BEFORE_MODIFICATION = "Store";
        final String STORE_AFTER_MODIFICATION = "StoreChanged";


        // The store does not exist in the database
        Store storeDoesNotExist = new Store(STORE_BEFORE_MODIFICATION);
        storeDoesNotExist.setPassword("test");
        storeDoesNotExist.setId(STORE_AFTER_MODIFICATION);

        String notExistingStoreIDBeforeModification = storeDoesNotExist.getId();

        assertDoesNotThrow(() -> this.entityManagerStore.update(storeDoesNotExist));

        assertEquals(notExistingStoreIDBeforeModification, storeDoesNotExist.getId());
        assertNotEquals(storeDoesNotExist.getId(), STORE_BEFORE_MODIFICATION);


        // The store exists in the database and is aftewards deleted
        Store storeExists = new Store(STORE_BEFORE_MODIFICATION);
        storeDoesNotExist.setPassword("test");
        storeExists.setId(STORE_AFTER_MODIFICATION);

        this.entityManagerStore.update(storeExists);
        String existingStoreIDBeforeModification = storeExists.getId();

        assertDoesNotThrow(() -> this.entityManagerStore.delete(storeExists));
        assertEquals(existingStoreIDBeforeModification, storeExists.getId());
        assertNotEquals(storeExists.getId(), STORE_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no store in the database, so the iterator shouldn't iterate
            - There are stores in the database, so the iterator should iterate through all the stores
     */
    @Test
    void getAll() {
        // There are no stores in the database
        CloseableIterator<Store> storesIterator = this.entityManagerStore.getAll();
        boolean areThereResults = storesIterator.hasNext();
        assertFalse(areThereResults);


        // There are stores in the database
        List<Store> stores
                = new ArrayList<>();

        Store firstStore = new Store("Store1");
        firstStore.setPassword("test");
        Store secondStore = new Store("Store2");
        secondStore.setPassword("test");

        this.entityManagerStore.create(firstStore);
        this.entityManagerStore.create(secondStore);

        storesIterator = this.entityManagerStore.getAll();
        assertTrue(storesIterator.hasNext());
        while (storesIterator.hasNext()) {
            Store store = storesIterator.next();
            stores.add(store);
        }

        assertEquals(2, stores.size());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerStore.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerStore.executeQuery(this.entityManagerStore.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no stores in the database, so the list should be empty
            - There are stores in the database, so inserted stores should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no stores in the database
        List<Store> storeList = this.entityManagerStore.getQueryForAll();
        assertTrue(storeList.isEmpty());


        // There are stores in the database
        Store firstStore = new Store("Store1");
        firstStore.setPassword("test");
        Store secondStore = new Store("Store2");
        secondStore.setPassword("test");

        this.entityManagerStore.create(firstStore);
        this.entityManagerStore.create(secondStore);

        storeList = this.entityManagerStore.getQueryForAll();
        assertFalse(storeList.isEmpty());
        assertEquals(2, storeList.size());
    }

    /*
       Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerStore.getQueryBuilder());
    }

    /*
        Use cases :
            - The store exists in the database, so the function return it
            - The store does not exist in the database, so the function return null
    */
    @Test
    void getStoreIfExists() {
        // The store is not in the database
        Store storeDoesNotExist = new Store("storeDoesNotExist");
        storeDoesNotExist.setPassword("test");

        assertFalse(this.entityManagerStore.exists(storeDoesNotExist));

        Store getStore = this.entityManagerStore.authGetStoreIfExists("storeDoesNotExist", "test");
        assertNull(getStore);


        // The store is in the database
        Store storeExists = new Store("Store");
        storeExists.setPassword("test");

        this.entityManagerStore.create(storeExists);
        assertTrue(this.entityManagerStore.exists(storeExists));

        getStore = this.entityManagerStore.authGetStoreIfExists("Store", "test");
        assertNotEquals(null, getStore);
        assertEquals(storeExists.getId(), getStore.getId());
        assertEquals(storeExists.getPassword(), getStore.getPassword());
    }
}