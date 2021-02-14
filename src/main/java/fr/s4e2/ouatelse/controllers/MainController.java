package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.utils.Utils;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController {

    private static final String WINDOW_TITLE_ROLES = "Ouatelse - Menu Gestion des Rôles";
    private static final String WINDOW_TITLE_STORES = "Ouatelse - Menu Gestion des Magasins";

    public void onRolesMenuClick() {
        Stage stage = Utils.buildStage("rolesMenuWindow.fxml");
        stage.setTitle(WINDOW_TITLE_ROLES);
        stage.show();
    }

    public void onStoresMenuClick(MouseEvent mouseEvent) {
        Stage stage = Utils.buildStage("storesMenuWindow.fxml");
        stage.setTitle(WINDOW_TITLE_STORES);
        stage.show();
    }
}
