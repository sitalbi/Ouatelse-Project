package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.screens.AuthUserScreen;
import fr.s4e2.ouatelse.utils.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class Main extends Application {

    @Getter
    @Setter
    private static DatabaseManager databaseManager;

    public static void main(String[] args) {
        Main.setDatabaseManager(new DatabaseManager());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new AuthUserScreen().open();
    }
}
