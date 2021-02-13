package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    public void onRolesMenuClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("rolesMenuWindow.fxml")));

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Roles menu");
        stage.show();
    }
}
