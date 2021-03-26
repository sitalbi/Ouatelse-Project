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

class EntityManagerProductTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerProduct entityManagerProduct;

    private Product createCompliantProduct() {
        Product compliantProduct = new Product();
        // Generate a random of type long for the reference of the product (unique attribute)
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
        this.entityManagerProduct = databaseManager.getEntityManagerProduct();

        CloseableIterator<Product> iterator = this.entityManagerProduct.getAll();
        iterator.forEachRemaining(product -> this.entityManagerProduct.delete(product));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Product> iterator = this.entityManagerProduct.getAll();
        iterator.forEachRemaining(product -> this.entityManagerProduct.delete(product));

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
            - Product is not compliant with database scheme
            - Product is compliant with database scheme
     */
    @Test
    void create() {
        // Product is not compliant
        Product notCompliantProduct = new Product();
        assertDoesNotThrow(() -> this.entityManagerProduct.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerProduct.exists(notCompliantProduct));


        // Product is compliant
        Product compliantProduct = createCompliantProduct();
        assertDoesNotThrow(() -> this.entityManagerProduct.create(compliantProduct));
        assertTrue(this.entityManagerProduct.exists(compliantProduct));
    }

    /*
        Use cases:
            - the product we want to delete exists
            - the product we want to delete doesn't exist
     */
    @Test
    void delete() {
        // Product does not exist in the database
        Product notExistingProduct = createCompliantProduct();
        assertFalse(this.entityManagerProduct.exists(notExistingProduct));
        assertDoesNotThrow(() -> this.entityManagerProduct.delete(notExistingProduct));
        assertFalse(this.entityManagerProduct.exists(notExistingProduct));


        // Product exists in the database and is afterwards deleted
        Product existingProduct = createCompliantProduct();
        this.entityManagerProduct.create(existingProduct);
        assertTrue(this.entityManagerProduct.exists(existingProduct));
        assertDoesNotThrow(() -> this.entityManagerProduct.delete(existingProduct));
        assertFalse(this.entityManagerProduct.exists(existingProduct));
    }

    /*
        Use cases :
            - Product does not exist in the database, so it shouldn't change
            - Product exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final String PRODUCT_NAME_BEFORE_MODIFICATION = "Some name";
        final String PRODUCT_NAME_AFTER_MODIFICATION = "Some other name";

        // Product does not exist in the database
        Product notExistingProduct = createCompliantProduct();
        notExistingProduct.setName(PRODUCT_NAME_AFTER_MODIFICATION);

        long notExistingProductIDBeforeModification = notExistingProduct.getId();

        assertDoesNotThrow(() -> this.entityManagerProduct.update(notExistingProduct));

        assertEquals(notExistingProductIDBeforeModification, notExistingProduct.getId());
        assertNotEquals(notExistingProduct.getName(), PRODUCT_NAME_BEFORE_MODIFICATION);


        // Product exists in the database and is afterwards deleted
        Product existingProduct = createCompliantProduct();
        this.entityManagerProduct.create(existingProduct);
        existingProduct.setName(PRODUCT_NAME_AFTER_MODIFICATION);

        this.entityManagerProduct.update(existingProduct);
        long existingProductIDBeforeModification = existingProduct.getId();

        assertEquals(existingProductIDBeforeModification, existingProduct.getId());
        assertNotEquals(existingProduct.getName(), PRODUCT_NAME_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no products in the database, so the iterator shouldn't iterate
            - There are products in the database, so the iterator should iterate through all the products
     */
    @Test
    void getAll() {
        // There are no products in the database
        CloseableIterator<Product> productIterator = this.entityManagerProduct.getAll();
        boolean areThereResults = productIterator.hasNext();
        assertFalse(areThereResults);


        // There are products in the database
        List<Product> iteratedProducts
                = new ArrayList<>();

        Product product1 = createCompliantProduct();
        Product product2 = createCompliantProduct();
        this.entityManagerProduct.create(product1);
        this.entityManagerProduct.create(product2);

        productIterator = this.entityManagerProduct.getAll();
        assertTrue(productIterator.hasNext());
        for (CloseableIterator<Product> it = productIterator; it.hasNext(); ) {
            iteratedProducts.add((it.next()));
        }

        assertEquals(2, iteratedProducts.size());
        assertFalse(productIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() throws SQLException {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerProduct.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerProduct.executeQuery(this.entityManagerProduct.getQueryBuilder().prepare()));
        this.entityManagerProduct.create(createCompliantProduct());
        List<Product> results = this.entityManagerProduct.executeQuery(this.entityManagerProduct.getQueryBuilder().prepare());

        assertEquals(1, results.size());
    }

    /*
        Use cases :
            - There are no products in the database, so the list should be empty
            - There are products in the database, so inserted products should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no products in the database
        List<Product> productsLists = this.entityManagerProduct.getQueryForAll();
        assertTrue(productsLists.isEmpty());


        // There are products in the database
        this.entityManagerProduct.create(createCompliantProduct());
        this.entityManagerProduct.create(createCompliantProduct());

        productsLists = this.entityManagerProduct.getQueryForAll();
        assertFalse(productsLists.isEmpty());
        assertEquals(2, productsLists.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerProduct.getQueryBuilder());
    }

    /*
        Use cases :
            - Product exists in the database
            - Product does not exists in the database
            - Product is null
     */
    @Test
    void exists() {
        // Product exists
        Product existingProduct = createCompliantProduct();
        this.entityManagerProduct.create(existingProduct);
        assertTrue(this.entityManagerProduct.exists(existingProduct));

        // Product doesn't exist
        Product nonExistingProduct = createCompliantProduct();
        assertFalse(this.entityManagerProduct.exists(nonExistingProduct));

        // Product is null
        assertFalse(this.entityManagerProduct.exists(null));
    }
}