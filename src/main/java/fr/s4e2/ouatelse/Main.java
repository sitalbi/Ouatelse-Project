package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.screens.AuthUserScreen;
import fr.s4e2.ouatelse.utils.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * Software's main class
 */
public class Main extends Application {

    @Getter
    @Setter
    private static DatabaseManager databaseManager;

    /**
     * Software's main function
     *
     * @param args
     */
    public static void main(String[] args) {
        Main.setDatabaseManager(new DatabaseManager());
        launch(args);
    }

    /**
     * Allows to launch the software directly on a specific panel
     *
     * @param primaryStage the panel to be displayed
     */
    @Override
    public void start(Stage primaryStage) {
        new AuthUserScreen().open();
    }
}
