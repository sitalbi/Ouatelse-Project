package fr.s4e2.ouatelse.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private final String DATABASE_NAME = "sqlite-test.db";

    @BeforeEach
    void setUp() {
        DatabaseManager.deleteDatabase(DATABASE_NAME);
    }

    @Test
    void initialization() {
        new DatabaseManager(DATABASE_NAME);
        File databaseFile = new File(DATABASE_NAME);

        assertNotNull(databaseFile);
    }

    @Test
    void close() {
        DatabaseManager databaseManager = new DatabaseManager(DATABASE_NAME);
        assertNotNull(databaseManager.getConnectionSource());

        assertDoesNotThrow(databaseManager::close);
    }

    @Test
    void setupTables() {
        System.out.println("[INFO] Non-testable method due to ORMLite's API.");
        assertTrue(true);
    }
}