package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerProductStockTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerProductStock entityManagerProductStock;

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
        this.entityManagerProductStock = databaseManager.getEntityManagerProductStock();

        CloseableIterator<ProductStock> iterator = this.entityManagerProductStock.getAll();
        iterator.forEachRemaining(stock -> this.entityManagerProductStock.delete(stock));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<ProductStock> iterator = this.entityManagerProductStock.getAll();
        iterator.forEachRemaining(stock -> this.entityManagerProductStock.delete(stock));

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
        ProductStock notCompliantStock = new ProductStock();
        assertDoesNotThrow(() -> this.entityManagerProductStock.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerProductStock.exists(notCompliantStock));


        // Stock is compliant
        ProductStock compliantStock = new ProductStock();
        compliantStock.setQuantity(0);
        assertDoesNotThrow(() -> this.entityManagerProductStock.create(compliantStock));
        assertTrue(this.entityManagerProductStock.exists(compliantStock));
    }

    /*
        Use cases:
            - the stock we want to delete exists
            - the stock we want to delete doesn't exist
     */
    @Test
    void delete() {
        // Stock does not exist in the database
        ProductStock notExistingStock = new ProductStock();
        assertFalse(this.entityManagerProductStock.exists(notExistingStock));
        assertDoesNotThrow(() -> this.entityManagerProductStock.delete(notExistingStock));
        assertFalse(this.entityManagerProductStock.exists(notExistingStock));


        // Stock exists in the database and is afterwards deleted
        ProductStock existingStock = new ProductStock();
        existingStock.setQuantity(0);
        this.entityManagerProductStock.create(existingStock);
        assertTrue(this.entityManagerProductStock.exists(existingStock));
        assertDoesNotThrow(() -> this.entityManagerProductStock.delete(existingStock));
        assertFalse(this.entityManagerProductStock.exists(existingStock));
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
        ProductStock notExistingStock = new ProductStock();

        notExistingStock.setQuantity(0);
        notExistingStock.setProduct(firstProduct);

        long notExistingStockIDBeforeModification = notExistingStock.getId();

        assertDoesNotThrow(() -> this.entityManagerProductStock.update(notExistingStock));

        assertEquals(notExistingStockIDBeforeModification, notExistingStock.getId());
        assertNotEquals(notExistingStock.getProduct(), secondProduct);


        // Stock exists in the database and is afterwards deleted
        ProductStock existingStock = new ProductStock();

        existingStock.setQuantity(0);
        existingStock.setProduct(firstProduct);

        this.entityManagerProductStock.create(existingStock);
        existingStock.setProduct(secondProduct);

        this.entityManagerProductStock.update(existingStock);
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
        CloseableIterator<ProductStock> stockIterator = this.entityManagerProductStock.getAll();
        boolean areThereResults = stockIterator.hasNext();
        assertFalse(areThereResults);


        // There are stocks in the database
        List<ProductStock> iteratedStock
                = new ArrayList<>();

        ProductStock product1 = new ProductStock();
        ProductStock product2 = new ProductStock();

        product1.setQuantity(0);
        product2.setQuantity(0);

        this.entityManagerProductStock.create(product1);
        this.entityManagerProductStock.create(product2);

        stockIterator = this.entityManagerProductStock.getAll();
        assertTrue(stockIterator.hasNext());
        for (CloseableIterator<ProductStock> it = stockIterator; it.hasNext(); ) {
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
        assertThrows(NullPointerException.class, () -> this.entityManagerProductStock.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerProductStock.executeQuery(this.entityManagerProductStock.getQueryBuilder().prepare()));

        ProductStock productStock = new ProductStock();
        productStock.setProduct(createCompliantProduct());
        productStock.setQuantity(1);

        this.entityManagerProductStock.create(productStock);
        List<ProductStock> results = this.entityManagerProductStock.executeQuery(this.entityManagerProductStock.getQueryBuilder().prepare());

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
        List<ProductStock> stockList = this.entityManagerProductStock.getQueryForAll();
        assertTrue(stockList.isEmpty());


        // There are stocks in the database
        ProductStock firstStock = new ProductStock();
        ProductStock secondStock = new ProductStock();

        firstStock.setQuantity(0);
        secondStock.setQuantity(0);

        this.entityManagerProductStock.create(firstStock);
        this.entityManagerProductStock.create(secondStock);

        stockList = this.entityManagerProductStock.getQueryForAll();
        assertFalse(stockList.isEmpty());
        assertEquals(2, stockList.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerProductStock.getQueryBuilder());
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
        ProductStock existingStock = new ProductStock();
        existingStock.setQuantity(0);
        this.entityManagerProductStock.create(existingStock);
        assertTrue(this.entityManagerProductStock.exists(existingStock));

        // Stock doesn't exist
        ProductStock nonExistingStock = new ProductStock();
        assertFalse(this.entityManagerProductStock.exists(nonExistingStock));

        // Stock is null
        assertFalse(this.entityManagerProductStock.exists(null));
    }
}