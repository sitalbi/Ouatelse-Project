package fr.s4e2.ouatelse.databaseInterface;

import fr.s4e2.ouatelse.objects.Role;
import fr.s4e2.ouatelse.utils.DatabaseManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseRoleInterfaceTest {

    private final DatabaseManager databaseManager = new DatabaseManager();
    private final DatabaseRoleInterface databaseRoleInterface = databaseManager.getDatabaseRoleInterface();

    /*
        Use cases:
            - Role name is null
            - Role name is compliant
     */
    @Test
    void createNewRole() {
        assertDoesNotThrow(() -> this.databaseRoleInterface.create(null));

        Role newlyCreatedRole = this.databaseRoleInterface.create("New role");

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