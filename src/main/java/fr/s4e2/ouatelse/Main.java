package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.managers.DatabaseManager;
import fr.s4e2.ouatelse.screens.AuthUserScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

public class Main extends Application {

    @Getter
    private static DatabaseManager databaseManager;

    static {
        Main.databaseManager = new DatabaseManager("sqlite.db");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new AuthUserScreen().open();
    }
}
