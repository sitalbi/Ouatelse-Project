package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import fr.s4e2.ouatelse.objects.Civility;
import fr.s4e2.ouatelse.objects.PersonState;
import fr.s4e2.ouatelse.objects.Salary;
import fr.s4e2.ouatelse.objects.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerSalaryTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    private DatabaseManager databaseManager;
    private EntityManagerSalary entityManagerSalary;

    private Salary createCompliantSalary() {
        Salary compliantSalary = new Salary();

        compliantSalary.setDate(new Date());
        compliantSalary.setGrossSalary(2000);
        compliantSalary.setNetSalary(50);
        compliantSalary.setUser(createCompliantUser());

        return compliantSalary;
    }

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
        this.entityManagerSalary = databaseManager.getEntityManagerSalary();

        CloseableIterator<Salary> iterator = this.entityManagerSalary.getAll();
        iterator.forEachRemaining(salary -> this.entityManagerSalary.delete(salary));
    }

    @AfterEach
    void tearDown() {
        CloseableIterator<Salary> iterator = this.entityManagerSalary.getAll();
        iterator.forEachRemaining(salary -> this.entityManagerSalary.delete(salary));

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
            - Salary is not compliant with database scheme
            - Salary is compliant with database scheme
     */
    @Test
    void create() {
        // Salary is not compliant
        Salary notCompliantSalary = new Salary();
        assertDoesNotThrow(() -> this.entityManagerSalary.create(null));
        // Inserted objects have an ID > 0
        assertFalse(this.entityManagerSalary.exists(notCompliantSalary));


        // Salary is compliant
        Salary compliantSalary = createCompliantSalary();
        assertDoesNotThrow(() -> this.entityManagerSalary.create(compliantSalary));
        assertTrue(this.entityManagerSalary.exists(compliantSalary));
    }

    /*
        Use cases :
            - Salary does not exist in the database, so its ID shouldn't change
            - Salary exists in the database, so its ID should change
        In both cases, no exception should be thrown as they should be catched by the code
     */
    @Test
    void delete() {
        // Salary does not exist in the database
        Salary notExistingSalary = createCompliantSalary();
        assertFalse(this.entityManagerSalary.exists(notExistingSalary));
        assertDoesNotThrow(() -> this.entityManagerSalary.delete(notExistingSalary));
        assertFalse(this.entityManagerSalary.exists(notExistingSalary));


        // Salary exists in the database and is afterwards deleted
        Salary existingSalary = createCompliantSalary();
        this.entityManagerSalary.create(existingSalary);
        assertTrue(this.entityManagerSalary.exists(existingSalary));
        assertDoesNotThrow(() -> this.entityManagerSalary.delete(existingSalary));
        assertFalse(this.entityManagerSalary.exists(existingSalary));
    }


    /*
        Use cases :
            - Salary does not exist in the database, so it shouldn't change
            - Salary exists in the database, so its attributes should change
        In both cases, no exception should be thrown as they should be caught by the code
    */
    @Test
    void update() {
        final double SALARY_GROSS_BEFORE_MODIFICATION = 5000;
        final double SALARY_GROSS_AFTER_MODIFICATION = 6000;


        // Salary does not exist in the database
        Salary notExistingSalary = createCompliantSalary();
        notExistingSalary.setGrossSalary(SALARY_GROSS_AFTER_MODIFICATION);

        long notExistingSalaryIDBeforeModification = notExistingSalary.getId();

        assertDoesNotThrow(() -> this.entityManagerSalary.update(notExistingSalary));

        assertEquals(notExistingSalaryIDBeforeModification, notExistingSalary.getId());
        assertNotEquals(notExistingSalary.getGrossSalary(), SALARY_GROSS_BEFORE_MODIFICATION);


        // Salary exists in the database and is afterwards deleted
        Salary existingSalary = createCompliantSalary();
        this.entityManagerSalary.create(existingSalary);
        existingSalary.setGrossSalary(SALARY_GROSS_AFTER_MODIFICATION);

        this.entityManagerSalary.update(existingSalary);
        long existingSalaryIDBeforeModification = existingSalary.getId();

        assertEquals(existingSalaryIDBeforeModification, existingSalary.getId());
        assertNotEquals(existingSalary.getGrossSalary(), SALARY_GROSS_BEFORE_MODIFICATION);
    }

    /*
        Use cases :
            - There are no salary in the database, so the iterator shouldn't iterate
            - There are salary in the database, so the iterator should iterate through all the salary
     */
    @Test
    void getAll() {
        // There are no salary in the database
        CloseableIterator<Salary> salaryIterator = this.entityManagerSalary.getAll();
        boolean areThereResults = salaryIterator.hasNext();
        assertFalse(areThereResults);


        // There are salary in the database
        List<Salary> iteratedSalary = new ArrayList<>();

        this.entityManagerSalary.create(createCompliantSalary());
        this.entityManagerSalary.create(createCompliantSalary());

        salaryIterator = this.entityManagerSalary.getAll();
        assertTrue(salaryIterator.hasNext());
        for (CloseableIterator<Salary> it = salaryIterator; it.hasNext(); ) {
            iteratedSalary.add(it.next());
        }

        assertEquals(2, iteratedSalary.size());
        assertFalse(salaryIterator.hasNext());
    }

    /*
        Use cases :
                - The query is empty, a NullPointerException is thrown
                - The query is not empty, nothing is thrown
     */
    @Test
    void executeQuery() {
        // Query is empty
        assertThrows(NullPointerException.class, () -> this.entityManagerSalary.executeQuery(null));


        // Query is not empty
        assertDoesNotThrow(() -> this.entityManagerSalary.executeQuery(this.entityManagerSalary.getQueryBuilder().prepare()));
    }

    /*
        Use cases :
            - There are no salary in the database, so the list should be empty
            - There are salary in the database, so inserted salary should be in the list
    */
    @Test
    void getQueryForAll() {
        // There are no salary in the database
        List<Salary> salaryList = this.entityManagerSalary.getQueryForAll();
        assertTrue(salaryList.isEmpty());


        // There are salary in the database
        this.entityManagerSalary.create(createCompliantSalary());
        this.entityManagerSalary.create(createCompliantSalary());

        salaryList = this.entityManagerSalary.getQueryForAll();
        assertFalse(salaryList.isEmpty());
        assertEquals(2, salaryList.size());
    }

    /*
        Simple wrapper, nothing should have to be tested
    */
    @Test
    void getQueryBuilder() {
        assertNotNull(this.entityManagerSalary.getQueryBuilder());
    }

    /*
        Use cases :
            - Salary exists in the database
            - Salary does not exists in the database
            - Salary is null
     */
    @Test
    void exists() {
        // Salary exists
        Salary existingSalary= createCompliantSalary();
        this.entityManagerSalary.create(existingSalary);
        assertTrue(this.entityManagerSalary.exists(existingSalary));

        // Salary doesn't exist
        Salary nonExistingSalary = createCompliantSalary();
        assertFalse(this.entityManagerSalary.exists(nonExistingSalary));

        // Salary is null
        assertFalse(this.entityManagerSalary.exists(null));
    }
}