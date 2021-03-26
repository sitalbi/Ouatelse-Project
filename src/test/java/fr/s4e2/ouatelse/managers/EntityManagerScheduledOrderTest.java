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

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerScheduledOrderTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerScheduledOrder entityManagerScheduledOrder;

    private ScheduledOrder createCompliantScheduledOrder(long id) {
        ScheduledOrder compliantScheduledOrder = new ScheduledOrder();

        compliantScheduledOrder.setId(id);
        compliantScheduledOrder.setProduct(new Product());
        compliantScheduledOrder.setQuantity(0);
        compliantScheduledOrder.setScheduledOrderDate(new Date());
        compliantScheduledOrder.setStore(new Store("Store"));

        return compliantScheduledOrder;
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerScheduledOrder = databaseManager.getEntityManagerScheduledOrder();

        CloseableIterator<ScheduledOrder> iterator = this.entityManagerScheduledOrder.getAll();
        iterator.forEachRemaining(order -> this.entityManagerScheduledOrder.delete(order));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<ScheduledOrder> iterator = this.entityManagerScheduledOrder.getAll();
        iterator.forEachRemaining(order -> this.entityManagerScheduledOrder.delete(order));

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
            - Order is not compliant with database scheme
            - Order is compliant with database scheme
     */
    @Test
    void create() {
        // Order is not compliant
        ScheduledOrder notCompliantOrder = new ScheduledOrder();
        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerScheduledOrder.exists(notCompliantOrder));


        // Order is compliant
        ScheduledOrder compliantScheduledOrder = createCompliantScheduledOrder(1);
        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.create(compliantScheduledOrder));
        assertTrue(this.entityManagerScheduledOrder.exists(compliantScheduledOrder));
    }

    /*
        Use cases :
            - Order does not exist in the database, so its ID shouldn't change
            - Order exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Order does not exist in the database
        ScheduledOrder notExistingOrder = createCompliantScheduledOrder(1);
        assertFalse(this.entityManagerScheduledOrder.exists(notExistingOrder));
        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.delete(notExistingOrder));
        assertFalse(this.entityManagerScheduledOrder.exists(notExistingOrder));


        // Order exists in the database and is afterwards deleted
        ScheduledOrder existingOrder = createCompliantScheduledOrder(1);
        this.entityManagerScheduledOrder.create(existingOrder);
        assertTrue(this.entityManagerScheduledOrder.exists(existingOrder));
        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.delete(existingOrder));
        assertFalse(this.entityManagerScheduledOrder.exists(existingOrder));
    }

    /*
        Use cases :
            - Order does not exist in the database, so it shouldn't change
            - Order exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final Product ORDER_PRODUCT_BEFORE_MODIFICATION = new Product();
        final Product ORDER_PRODUCT_AFTER_MODIFICATION = new Product();


        // Order does not exist in the database
        ScheduledOrder notExistingOrder = createCompliantScheduledOrder(1);
        notExistingOrder.setProduct(ORDER_PRODUCT_AFTER_MODIFICATION);

        long notExistingOrderIDBeforeModification = notExistingOrder.getId();

        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.update(notExistingOrder));

        assertEquals(notExistingOrderIDBeforeModification, notExistingOrder.getId());
        assertNotEquals(notExistingOrder.getProduct(), ORDER_PRODUCT_BEFORE_MODIFICATION);


        // Order exists in the database and is afterwards deleted
        ScheduledOrder existingOrder = createCompliantScheduledOrder(1);
        this.entityManagerScheduledOrder.create(existingOrder);
        existingOrder.setProduct(ORDER_PRODUCT_AFTER_MODIFICATION);

        this.entityManagerScheduledOrder.update(existingOrder);
        long existingOrderIDBeforeModification = existingOrder.getId();

        assertEquals(existingOrderIDBeforeModification, existingOrder.getId());
        assertNotEquals(existingOrder.getProduct(), ORDER_PRODUCT_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no orders in the database, so the iterator shouldn't iterate
            - There are orders in the database, so the iterator should iterate through all the orders
     */
    @Test
    void getAll() {
        // There are no order in the database
        CloseableIterator<ScheduledOrder> ordersIterator = this.entityManagerScheduledOrder.getAll();
        boolean areThereResults = ordersIterator.hasNext();
        assertFalse(areThereResults);


        // There are orders in the database
        List<ScheduledOrder> iteratedOrders = new ArrayList<>();

        this.entityManagerScheduledOrder.create(createCompliantScheduledOrder(1));
        this.entityManagerScheduledOrder.create(createCompliantScheduledOrder(2));

        ordersIterator = this.entityManagerScheduledOrder.getAll();
        assertTrue(ordersIterator.hasNext());
        for (CloseableIterator<ScheduledOrder> it = ordersIterator; it.hasNext(); ) {
            iteratedOrders.add(it.next());
        }

        assertEquals(2, iteratedOrders.size());
        assertFalse(ordersIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() throws SQLException {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerScheduledOrder.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerScheduledOrder.executeQuery(this.entityManagerScheduledOrder.getQueryBuilder().prepare()));
        this.entityManagerScheduledOrder.create(createCompliantScheduledOrder(1));
        List<ScheduledOrder> results = this.entityManagerScheduledOrder.executeQuery(this.entityManagerScheduledOrder.getQueryBuilder().prepare());

        assertEquals(1, results.size());
    }

    /*
        Use cases :
            - There are no orders in the database, so the list should be empty
            - There are orders in the database, so inserted orders should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no orders in the database
        List<ScheduledOrder> orderList = this.entityManagerScheduledOrder.getQueryForAll();
        assertTrue(orderList.isEmpty());


        // There are orders in the database
        this.entityManagerScheduledOrder.create(createCompliantScheduledOrder(1));
        this.entityManagerScheduledOrder.create(createCompliantScheduledOrder(2));

        orderList = this.entityManagerScheduledOrder.getQueryForAll();
        assertFalse(orderList.isEmpty());
        assertEquals(2, orderList.size());
    }

    /*
       Simple wrapper, nothing should have to be tested
   */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerScheduledOrder.getQueryBuilder());
    }

    /*
        Use cases :
            - Order exists in the database
            - Order does not exists in the database
            - Order is null
    */
    @Test
    void exists() {
        // Order exists
        ScheduledOrder existingOrder = createCompliantScheduledOrder(1);
        this.entityManagerScheduledOrder.create(existingOrder);
        assertTrue(this.entityManagerScheduledOrder.exists(existingOrder));

        // Order doesn't exist
        ScheduledOrder nonExistingOrder =  createCompliantScheduledOrder(2);
        assertFalse(this.entityManagerScheduledOrder.exists(nonExistingOrder));

        // Order is null
        assertFalse(this.entityManagerScheduledOrder.exists(null));
    }
}