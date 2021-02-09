package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.utils.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.close();
    }
}
