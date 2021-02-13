package fr.s4e2.ouatelse;

import fr.s4e2.ouatelse.utils.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class Main extends Application {

    @Getter
    @Setter
    private static DatabaseManager databaseManager;

    public static void main(String[] args) {
        Main.setDatabaseManager(new DatabaseManager());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainWindow.fxml")));

        primaryStage.setTitle("Ouatelse Main Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
