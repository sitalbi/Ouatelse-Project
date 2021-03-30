package fr.s4e2.ouatelse;

import com.j256.ormlite.logger.LocalLog;
import fr.s4e2.ouatelse.managers.DatabaseManager;
import fr.s4e2.ouatelse.screens.AuthUserScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * Software's main class
 */
public class Main extends Application {

    @Getter
    private static DatabaseManager databaseManager;

    static {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        Main.databaseManager = new DatabaseManager("sqlite.db");
    }

    /**
     * Software's main function
     *
     * @param args arguments
     */
    public static void main(String[] args) {
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
