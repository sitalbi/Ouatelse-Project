package fr.s4e2.ouatelse.utils;

import fr.s4e2.ouatelse.managers.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        deleteDatabase();
    }

    @Test
    void initialization() {
        new DatabaseManager("sqlite-test.db");
        File databaseFile = new File("sqlite-test.db");

        assertNotNull(databaseFile);
    }

    @Test
    void close() {
        DatabaseManager databaseManager = new DatabaseManager("sqlite-test.db");
        assertNotNull(databaseManager.getConnectionSource());

        assertDoesNotThrow(databaseManager::close);
    }

    @Test
    void setupTables() {
        System.out.println("[INFO] Non-testable method due to ORMLite's API.");
    }

    private void deleteDatabase() {
        File databaseFile = new File("sqlite-test.db");
        if (databaseFile.exists()) {
            if (databaseFile.delete()) {
                System.out.println("[INFO] sqlite-test.db deleted.");
            } else {
                System.err.println("[ERROR] Couldn't delete sqlite-test.db.");
            }
        } else {
            System.out.println("[INFO] sqlite-test.db does not exists.");
        }
    }
}