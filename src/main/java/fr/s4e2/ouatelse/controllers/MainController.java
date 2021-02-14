package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    private static final String WINDOW_TITLE_ROLES = "Ouatelse - Menu Gestion des Rôles";
    private static final String WINDOW_TITLE_STORES = "Ouatelse - Menu Gestion des Magasins";

    public void onRolesMenuClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("rolesMenuWindow.fxml")));

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(WINDOW_TITLE_ROLES);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void onStoresMenuClick(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("storesMenuWindow.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(WINDOW_TITLE_STORES);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
