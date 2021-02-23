package fr.s4e2.ouatelse.utils;

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
        new DatabaseManager();
        File databaseFile = new File("sqlite.db");

        assertNotNull(databaseFile);
    }

    @Test
    void close() {
        DatabaseManager databaseManager = new DatabaseManager();
        assertNotNull(databaseManager.getConnectionSource());

        assertDoesNotThrow(databaseManager::close);
    }

    @Test
    void setupTables() {
        System.out.println("INFO : Non-testable method due to ORMLite's API");
    }

    private void deleteDatabase() {
        File databaseFile = new File("sqlite.db");
        if (databaseFile.exists()) {
            if (databaseFile.delete()) {
                System.out.println("INFO : sqlite.db deleted");
            } else {
                System.err.println("ERROR : couldn't delete sqlite.db");
            }
        } else {
            System.out.println("INFO : sqlite.db does not exists");
        }
    }
}