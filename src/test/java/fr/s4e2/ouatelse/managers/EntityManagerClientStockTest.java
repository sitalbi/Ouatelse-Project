package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerClientStockTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerClientStock entityManagerClientStock;

    private Product createCompliantProduct() {
        Product compliantProduct = new Product();
        // Generate a random of type long for the reference of the stock (unique attribute)
        Random r = new Random();
        long ref = (long) (r.nextDouble() * 1000);

        compliantProduct.setName("Some name");
        compliantProduct.setBarCode("Barcode");
        compliantProduct.setReference(ref);
        compliantProduct.setMargin(25);
        compliantProduct.setTaxes(25);
        compliantProduct.setPurchasePrice(15);
        compliantProduct.setBrand("Some brand");
        compliantProduct.setState(ProductState.IN_STOCK);
        compliantProduct.setCategory("Category");
        compliantProduct.setStore(new Store());

        return compliantProduct;
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerClientStock = databaseManager.getEntityManagerClientStock();

        CloseableIterator<ClientStock> iterator = this.entityManagerClientStock.getAll();
        iterator.forEachRemaining(stock -> this.entityManagerClientStock.delete(stock));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<ClientStock> iterator = this.entityManagerClientStock.getAll();
        iterator.forEachRemaining(stock -> this.entityManagerClientStock.delete(stock));

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
            - Stock is not compliant with database scheme
            - Stock is compliant with database scheme
     */
    @Test
    void create() {
        // Stock is not compliant
        ClientStock notCompliantStock = new ClientStock();
        assertDoesNotThrow(() -> this.entityManagerClientStock.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerClientStock.exists(notCompliantStock));


        // Stock is compliant
        ClientStock compliantStock = new ClientStock();
        compliantStock.setQuantity(0);
        assertDoesNotThrow(() -> this.entityManagerClientStock.create(compliantStock));
        assertTrue(this.entityManagerClientStock.exists(compliantStock));
    }

    /*
        Use cases:
            - the stock we want to delete exists
            - the stock we want to delete doesn't exist
     */
    @Test
    void delete() {
        // Stock does not exist in the database
        ClientStock notExistingStock = new ClientStock();
        assertFalse(this.entityManagerClientStock.exists(notExistingStock));
        assertDoesNotThrow(() -> this.entityManagerClientStock.delete(notExistingStock));
        assertFalse(this.entityManagerClientStock.exists(notExistingStock));


        // Stock exists in the database and is afterwards deleted
        ClientStock existingStock = new ClientStock();
        existingStock.setQuantity(0);
        this.entityManagerClientStock.create(existingStock);
        assertTrue(this.entityManagerClientStock.exists(existingStock));
        assertDoesNotThrow(() -> this.entityManagerClientStock.delete(existingStock));
        assertFalse(this.entityManagerClientStock.exists(existingStock));
    }

    /*
        Use cases :
            - Stock does not exist in the database, so it shouldn't change
            - Stock exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        Product firstProduct = createCompliantProduct();
        Product secondProduct = createCompliantProduct();

        // Stock does not exist in the database
        ClientStock notExistingStock = new ClientStock();

        notExistingStock.setQuantity(0);
        notExistingStock.setProduct(firstProduct);
        notExistingStock.setClient(new Client());

        long notExistingStockIDBeforeModification = notExistingStock.getId();

        assertDoesNotThrow(() -> this.entityManagerClientStock.update(notExistingStock));

        assertEquals(notExistingStockIDBeforeModification, notExistingStock.getId());
        assertNotEquals(notExistingStock.getProduct(), secondProduct);


        // Stock exists in the database and is afterwards deleted
        ClientStock existingStock = new ClientStock();

        existingStock.setQuantity(0);
        existingStock.setProduct(firstProduct);
        existingStock.setClient(new Client());

        this.entityManagerClientStock.create(existingStock);
        existingStock.setProduct(secondProduct);

        this.entityManagerClientStock.update(existingStock);
        long existingStockIDBeforeModification = existingStock.getId();

        assertEquals(existingStockIDBeforeModification, existingStock.getId());
        assertNotEquals(existingStock.getProduct(), firstProduct);
    }

    /*
        Use cases :
            - There are no stocks in the database, so the iterator shouldn't iterate
            - There are stocks in the database, so the iterator should iterate through all the products
     */
    @Test
    void getAll() {
        // There are no stocks in the database
        CloseableIterator<ClientStock> stockIterator = this.entityManagerClientStock.getAll();
        boolean areThereResults = stockIterator.hasNext();
        assertFalse(areThereResults);


        // There are stocks in the database
        List<ClientStock> iteratedStock
                = new ArrayList<>();

        ClientStock stock1 = new ClientStock();
        ClientStock stock2 = new ClientStock();

        stock1.setQuantity(0);
        stock2.setQuantity(0);
        stock1.setClient(new Client());
        stock2.setClient(new Client());

        this.entityManagerClientStock.create(stock1);
        this.entityManagerClientStock.create(stock2);

        stockIterator = this.entityManagerClientStock.getAll();
        assertTrue(stockIterator.hasNext());
        for (CloseableIterator<ClientStock> it = stockIterator; it.hasNext(); ) {
            iteratedStock.add((it.next()));
        }

        assertEquals(2, iteratedStock.size());
        assertFalse(stockIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() throws SQLException {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerClientStock.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerClientStock.executeQuery(this.entityManagerClientStock.getQueryBuilder().prepare()));
        ClientStock clientStock = new ClientStock();
        clientStock.setProduct(createCompliantProduct());
        clientStock.setClient(new Client());

        this.entityManagerClientStock.create(clientStock);
        List<ClientStock> results = this.entityManagerClientStock.executeQuery(this.entityManagerClientStock.getQueryBuilder().prepare());

        assertEquals(1, results.size());
    }

    /*
        Use cases :
            - There are no stocks in the database, so the list should be empty
            - There are stocks in the database, so inserted stocks should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no stocks in the database
        List<ClientStock> stockList = this.entityManagerClientStock.getQueryForAll();
        assertTrue(stockList.isEmpty());


        // There are stocks in the database
        ClientStock firstStock = new ClientStock();
        ClientStock secondStock = new ClientStock();

        firstStock.setQuantity(0);
        secondStock.setQuantity(0);
        firstStock.setClient(new Client());
        secondStock.setClient(new Client());

        this.entityManagerClientStock.create(firstStock);
        this.entityManagerClientStock.create(secondStock);

        stockList = this.entityManagerClientStock.getQueryForAll();
        assertFalse(stockList.isEmpty());
        assertEquals(2, stockList.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() { assertNotNull(this.entityManagerClientStock.getQueryBuilder());
    }

    /*
        Use cases :
            - Stock exists in the database
            - Stock does not exists in the database
            - Stock is null
     */
    @Test
    void exists() {
        // Stock exists
        ClientStock existingStock = new ClientStock();
        existingStock.setQuantity(0);
        existingStock.setClient(new Client());
        this.entityManagerClientStock.create(existingStock);
        assertTrue(this.entityManagerClientStock.exists(existingStock));

        // Stock doesn't exist
        ClientStock nonExistingStock = new ClientStock();
        assertFalse(this.entityManagerClientStock.exists(nonExistingStock));

        // Stock is null
        assertFalse(this.entityManagerClientStock.exists(null));
    }
}