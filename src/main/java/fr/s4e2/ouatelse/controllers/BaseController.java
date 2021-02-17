package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseController implements Initializable {

    @FXML
    private MenuBar menuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void onQuit() {
        Stage stage = (Stage) this.menuBar.getScene().getWindow();
        stage.close();
    }

    public void onAbout() {
    }

    public void onPreferences() {
    }
}
