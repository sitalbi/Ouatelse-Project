package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends BaseController {

    @FXML
    private Label homeAdminName;

    @FXML
    private Label homeAdminEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        //todo : implement
        this.homeAdminName.setText("Simon Caillat");
        this.homeAdminEmail.setText("simon.caillat@etu.u-bordeaux.fr");
    }

    public void onDisconnectClick() {
        //todo : handle disconnect

        Stage stage = (Stage) this.homeAdminName.getScene().getWindow();
        stage.close();
    }
}
