package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerRoleTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerRole entityManagerRole;

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
        this.databaseManager = new DatabaseManager(DATABASE_NAME);
        this.entityManagerRole = databaseManager.getEntityManagerRole();

        CloseableIterator<Role> iterator = this.entityManagerRole.getAll();
        iterator.forEachRemaining(role -> this.entityManagerRole.delete(role));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Role> iterator = this.entityManagerRole.getAll();
        iterator.forEachRemaining(role -> this.entityManagerRole.delete(role));

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
            - Role is not compliant with database scheme
            - Role is compliant with database scheme
     */
    @Test
    void create() {
        // Role is not compliant
        Role notCompliantRole = this.entityManagerRole.create(null);
        // Inserted objects have an ID > 0
        assertFalse(notCompliantRole.getId() > 0);


        // Role is compliant
        Role compliantRole = this.entityManagerRole.create("Some role");
        assertTrue(compliantRole.getId() > 0);
    }

    /*
        Use cases :
            - Role does not exist in the database, so its ID shouldn't change
            - Role exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Role does not exist in the database
        Role notExistingRole = new Role("Some role");
        assertFalse(this.entityManagerRole.exists(notExistingRole));
        assertDoesNotThrow(() -> this.entityManagerRole.delete(notExistingRole));
        assertFalse(this.entityManagerRole.exists(notExistingRole));


        // Role exists in the database and is afterwards deleted
        Role existingRole = this.entityManagerRole.create("Some other role");
        assertTrue(this.entityManagerRole.exists(existingRole));
        assertDoesNotThrow(() -> this.entityManagerRole.delete(existingRole));
        assertFalse(this.entityManagerRole.exists(existingRole));
    }

    /*
        Use cases :
            - Role does not exist in the database, so it shouldn't change
            - Role exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be catched by the code
    */
    @Test
    void update() {
        final String ROLE_NAME_BEFORE_MODIFICATION = "Some role";
        final String ROLE_NAME_AFTER_MODIFICATION = "Some other role";


        // Role does not exist in the database
        Role notExistingRole = new Role(ROLE_NAME_BEFORE_MODIFICATION);
        notExistingRole.setName(ROLE_NAME_AFTER_MODIFICATION);

        long notExistingRoleIDBeforeModification = notExistingRole.getId();

        assertDoesNotThrow(() -> this.entityManagerRole.update(notExistingRole));

        assertEquals(notExistingRoleIDBeforeModification, notExistingRole.getId());
        assertNotEquals(notExistingRole.getName(), ROLE_NAME_BEFORE_MODIFICATION);


        // Role exists in the database and is aftewards deleted
        Role existingRole = this.entityManagerRole.create(ROLE_NAME_BEFORE_MODIFICATION);
        existingRole.setName(ROLE_NAME_AFTER_MODIFICATION);

        this.entityManagerRole.update(existingRole);
        long existingRoleIDBeforeModification = existingRole.getId();

        assertDoesNotThrow(() -> this.entityManagerRole.delete(existingRole));
        assertEquals(existingRoleIDBeforeModification, existingRole.getId());
        assertNotEquals(existingRole.getName(), ROLE_NAME_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no roles in the database, so the iterator shouldn't iterate
            - There are roles in the database, so the iterator should iterate through all the roles
     */
    @Test
    void getAll() {
        // There are no roles in the database
        CloseableIterator<Role> rolesIterator = this.entityManagerRole.getAll();
        boolean areThereResults = rolesIterator.hasNext();
        assertFalse(areThereResults);


        // There are roles in the database
        List<Role> iteratedRoles
                = new ArrayList<>();

        this.entityManagerRole.create("Some role");
        this.entityManagerRole.create("Some other role");

        rolesIterator = this.entityManagerRole.getAll();
        assertTrue(rolesIterator.hasNext());
        for (CloseableIterator<Role> it = rolesIterator; it.hasNext(); ) {
            iteratedRoles.add(it.next());
        }

        assertEquals(iteratedRoles.size(), 2);
        assertFalse(rolesIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerRole.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerRole.executeQuery(this.entityManagerRole.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no roles in the database, so the list should be empty
            - There are roles in the database, so inserted addresses should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no roles in the database
        List<Role> roleList = this.entityManagerRole.getQueryForAll();
        assertTrue(roleList.isEmpty());


        // There are roles in the database
        Role firstRole = this.entityManagerRole.create("Some role");
        Role secondRole = this.entityManagerRole.create("Some other role");

        roleList = this.entityManagerRole.getQueryForAll();
        assertFalse(roleList.isEmpty());
        assertEquals(roleList.size(), 2);
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerRole.getQueryBuilder());
    }

    /*
        Use cases :
            - Role exists in the database
            - Role does not exists in the database
            - Role is null
     */
    @Test
    void exists() {
        // Role exists
        Role existingRole = this.entityManagerRole.create("Some role");
        assertTrue(this.entityManagerRole.exists(existingRole));

        // Role doesn't exist
        Role nonExistingRole = new Role("Some other role");
        assertFalse(this.entityManagerRole.exists(nonExistingRole));

        // Role is null
        assertFalse(this.entityManagerRole.exists(null));
    }
}