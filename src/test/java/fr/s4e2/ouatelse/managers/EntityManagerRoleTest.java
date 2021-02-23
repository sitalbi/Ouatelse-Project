package fr.s4e2.ouatelse.managers;

import fr.s4e2.ouatelse.objects.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerRoleTest {

    private final DatabaseManager databaseManager = new DatabaseManager("sqlite-test.db");
    private final EntityManagerRole entityManagerRole = databaseManager.getEntityManagerRole();

    /*
        Use cases:
            - Role name is null
            - Role name is compliant
     */
    @Test
    void createNewRole() {
        assertDoesNotThrow(() -> this.entityManagerRole.create(null));

        Role newlyCreatedRole = this.entityManagerRole.create("New role");

        assertNotNull(newlyCreatedRole);
        assertFalse(newlyCreatedRole.getName().isEmpty());
        assertTrue(newlyCreatedRole.getId() > 0);
    }

    @Test
    void deleteRole() {

    }

    @Test
    void updateRole() {
    }
}