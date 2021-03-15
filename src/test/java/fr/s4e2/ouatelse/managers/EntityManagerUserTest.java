package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Civility;
import fr.s4e2.ouatelse.objects.PersonState;
import fr.s4e2.ouatelse.objects.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerUserTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerUser entityManagerUser;

    private User createCompliantUser() {
        User compliantUser = new User();

        compliantUser.setName("Some name");
        compliantUser.setSurname("Some surname");
        compliantUser.setMobilePhoneNumber("+33 6 00 00 00 00");
        compliantUser.setEmail(Double.toString(Math.random()));
        compliantUser.setBirthDate(new Date());
        compliantUser.setCivility(Civility.M);
        compliantUser.setStatus(PersonState.EMPLOYED);

        compliantUser.setCredentials(Double.toString(Math.random()));
        compliantUser.setPassword("Some password");
        compliantUser.setHiringDate(new Date());
        compliantUser.setHoursPerWeek(35);

        return compliantUser;
    }

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerUser = databaseManager.getEntityManagerUser();

        CloseableIterator<User> iterator = this.entityManagerUser.getAll();
        iterator.forEachRemaining(user -> this.entityManagerUser.delete(user));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<User> iterator = this.entityManagerUser.getAll();
        iterator.forEachRemaining(user -> this.entityManagerUser.delete(user));

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
            - User is not compliant with database scheme
            - User is compliant with database scheme
     */
    @Test
    void create() {
        // User is not compliant
        User notCompliantUser = new User();
        assertDoesNotThrow(() -> this.entityManagerUser.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerUser.exists(notCompliantUser));


        // User is compliant
        User compliantUser = createCompliantUser();
        assertDoesNotThrow(() -> this.entityManagerUser.create(compliantUser));
        assertTrue(this.entityManagerUser.exists(compliantUser));
    }

    /*
        Use cases :
            - User does not exist in the database, so its ID shouldn't change
            - User exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // User does not exist in the database
        User notExistingUser = createCompliantUser();
        assertFalse(this.entityManagerUser.exists(notExistingUser));
        assertDoesNotThrow(() -> this.entityManagerUser.delete(notExistingUser));
        assertFalse(this.entityManagerUser.exists(notExistingUser));


        // User exists in the database and is afterwards deleted
        User existingUser = createCompliantUser();
        this.entityManagerUser.create(existingUser);
        assertTrue(this.entityManagerUser.exists(existingUser));
        assertDoesNotThrow(() -> this.entityManagerUser.delete(existingUser));
        assertFalse(this.entityManagerUser.exists(existingUser));
    }

    /*
        Use cases :
            - User does not exist in the database, so it shouldn't change
            - User exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final String USER_SURNAME_BEFORE_MODIFICATION = "Some surname";
        final String USER_SURNAME_AFTER_MODIFICATION = "Some other surname";


        // User does not exist in the database
        User notExistingUser = createCompliantUser();
        notExistingUser.setSurname(USER_SURNAME_AFTER_MODIFICATION);

        long notExistingUserIDBeforeModification = notExistingUser.getId();

        assertDoesNotThrow(() -> this.entityManagerUser.update(notExistingUser));

        assertEquals(notExistingUserIDBeforeModification, notExistingUser.getId());
        assertNotEquals(notExistingUser.getSurname(), USER_SURNAME_BEFORE_MODIFICATION);


        // User exists in the database and is afterwards deleted
        User existingUser = createCompliantUser();
        this.entityManagerUser.create(existingUser);
        existingUser.setSurname(USER_SURNAME_AFTER_MODIFICATION);

        this.entityManagerUser.update(existingUser);
        long existingUserIDBeforeModification = existingUser.getId();

        assertEquals(existingUserIDBeforeModification, existingUser.getId());
        assertNotEquals(existingUser.getSurname(), USER_SURNAME_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no users in the database, so the iterator shouldn't iterate
            - There are users in the database, so the iterator should iterate through all the users
     */
    @Test
    void getAll() {
        // There are no users in the database
        CloseableIterator<User> usersIterator = this.entityManagerUser.getAll();
        boolean areThereResults = usersIterator.hasNext();
        assertFalse(areThereResults);


        // There are users in the database
        List<User> iteratedUsers = new ArrayList<>();

        this.entityManagerUser.create(createCompliantUser());
        this.entityManagerUser.create(createCompliantUser());

        usersIterator = this.entityManagerUser.getAll();
        assertTrue(usersIterator.hasNext());
        for (CloseableIterator<User> it = usersIterator; it.hasNext(); ) {
            iteratedUsers.add(it.next());
        }

        assertEquals(2, iteratedUsers.size());
        assertFalse(usersIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerUser.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerUser.executeQuery(this.entityManagerUser.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no users in the database, so the list should be empty
            - There are users in the database, so inserted addresses should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no users in the database
        List<User> userList = this.entityManagerUser.getQueryForAll();
        assertTrue(userList.isEmpty());


        // There are users in the database
        this.entityManagerUser.create(createCompliantUser());
        this.entityManagerUser.create(createCompliantUser());

        userList = this.entityManagerUser.getQueryForAll();
        assertFalse(userList.isEmpty());
        assertEquals(2, userList.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerUser.getQueryBuilder());
    }

    /*
        Use cases :
            - User exists in the database
            - User does not exists in the database
            - User is null
     */
    @Test
    void exists() {
        // User exists
        User existingUser = createCompliantUser();
        this.entityManagerUser.create(existingUser);
        assertTrue(this.entityManagerUser.exists(existingUser));

        // User doesn't exist
        User nonExistingUser = createCompliantUser();
        assertFalse(this.entityManagerUser.exists(nonExistingUser));

        // User is null
        assertFalse(this.entityManagerUser.exists(null));
    }
}