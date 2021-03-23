package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerCartTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerCart entityManagerCart;

    private Cart createCompliantCart() {
        Cart compliantCart = new Cart();
        Client compliantCLient = createCompliantClient();

        compliantCart.setClient(compliantCLient);
        compliantCart.setDate(new Date());

        return compliantCart;
    }

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
        this.entityManagerCart = databaseManager.getEntityManagerCart();

        CloseableIterator<Cart> iterator = this.entityManagerCart.getAll();
        iterator.forEachRemaining(cart -> this.entityManagerCart.delete(cart));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Cart> iterator = this.entityManagerCart.getAll();
        iterator.forEachRemaining(cart -> this.entityManagerCart.delete(cart));

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
           - Cart is not compliant with database scheme
           - Cart is compliant with database scheme
    */
    @Test
    void create() {
        // Cart is not compliant
        Cart notCompliantCart = new Cart();
        assertDoesNotThrow(() -> this.entityManagerCart.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerCart.exists(notCompliantCart));


        // Cart is compliant
        Cart compliantCart = createCompliantCart();
        assertDoesNotThrow(() -> this.entityManagerCart.create(compliantCart));
        assertTrue(this.entityManagerCart.exists(compliantCart));
    }

    /*
        Use cases :
            - Cart does not exist in the database, so its ID shouldn't change
            - Cart exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Cart does not exist in the database
        Cart notExistingCart = createCompliantCart();
        assertFalse(this.entityManagerCart.exists(notExistingCart));
        assertDoesNotThrow(() -> this.entityManagerCart.delete(notExistingCart));
        assertFalse(this.entityManagerCart.exists(notExistingCart));


        // Cart exists in the database and is afterwards deleted
        Cart existingCart = createCompliantCart();
        this.entityManagerCart.create(existingCart);
        assertTrue(this.entityManagerCart.exists(existingCart));
        assertDoesNotThrow(() -> this.entityManagerCart.delete(existingCart));
        assertFalse(this.entityManagerCart.exists(existingCart));
    }

    /*
        Use cases :
            - Cart does not exist in the database, so it shouldn't change
            - Cart exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final Client CART_CLIENT_BEFORE_MODIFICATION = createCompliantClient();
        final Client CART_CLIENT_AFTER_MODIFICATION = new Client();


        // Cart does not exist in the database
        Cart notExistingCart = createCompliantCart();
        notExistingCart.setClient(CART_CLIENT_AFTER_MODIFICATION);

        long notExistingCartIDBeforeModification = notExistingCart.getId();

        assertDoesNotThrow(() -> this.entityManagerCart.update(notExistingCart));

        assertEquals(notExistingCartIDBeforeModification, notExistingCart.getId());
        assertNotEquals(notExistingCart.getClient(), CART_CLIENT_BEFORE_MODIFICATION);


        // Cart exists in the database and is afterwards deleted
        Cart existingCart = createCompliantCart();
        this.entityManagerCart.create(existingCart);
        existingCart.setClient(CART_CLIENT_AFTER_MODIFICATION);

        this.entityManagerCart.update(existingCart);
        long existingCartIDBeforeModification = existingCart.getId();

        assertEquals(existingCartIDBeforeModification, existingCart.getId());
        assertNotEquals(existingCart.getClient(), CART_CLIENT_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no carts in the database, so the iterator shouldn't iterate
            - There are carts in the database, so the iterator should iterate through all the carts
     */
    @Test
    void getAll() {
        // There are no carts in the database
        CloseableIterator<Cart> cartsIterator = this.entityManagerCart.getAll();
        boolean areThereResults = cartsIterator.hasNext();
        assertFalse(areThereResults);


        // There are carts in the database
        List<Cart> iteratedCarts = new ArrayList<>();

        this.entityManagerCart.create(createCompliantCart());
        this.entityManagerCart.create(createCompliantCart());

        cartsIterator = this.entityManagerCart.getAll();
        assertTrue(cartsIterator.hasNext());
        for (CloseableIterator<Cart> it = cartsIterator; it.hasNext(); ) {
            iteratedCarts.add(it.next());
        }

        assertEquals(2, iteratedCarts.size());
        assertFalse(cartsIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerCart.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerCart.executeQuery(this.entityManagerCart.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no carts in the database, so the list should be empty
            - There are carts in the database, so the list should not be empty
    */
    @Test
    void getQueryForAll() {
        // There are no carts in the database
        List<Cart> cartList = this.entityManagerCart.getQueryForAll();
        assertTrue(cartList.isEmpty());


        // There are carts in the database
        this.entityManagerCart.create(createCompliantCart());
        this.entityManagerCart.create(createCompliantCart());

        cartList = this.entityManagerCart.getQueryForAll();
        assertFalse(cartList.isEmpty());
        assertEquals(2, cartList.size());
    }

    /*
       Simple wrapper, nothing should have to be tested
   */
    @Test
    void getQueryBuilder() { assertNotNull(this.entityManagerCart.getQueryBuilder());
    }

    /*
        Use cases :
            - Cart exists in the database
            - Cart does not exists in the database
            - Cart is null
    */
    @Test
    void exists() {
        // Cart exists
        Cart existingCart= createCompliantCart();
        this.entityManagerCart.create(existingCart);
        assertTrue(this.entityManagerCart.exists(existingCart));

        // Cart doesn't exist
        Cart nonExistingCart = createCompliantCart();
        assertFalse(this.entityManagerCart.exists(nonExistingCart));

        // Cart is null
        assertFalse(this.entityManagerCart.exists(null));
    }
}