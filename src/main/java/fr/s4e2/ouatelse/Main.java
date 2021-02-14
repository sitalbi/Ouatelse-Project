package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.utils.DatabaseManager;
import fr.s4e2.ouatelse.utils.Utils;
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
        primaryStage = Utils.buildStage("mainWindow.fxml");
        primaryStage.setTitle("Ouatelse - Menu Principal");
        primaryStage.show();
    }
}
