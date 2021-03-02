package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller of the database of the application
 */
public class BaseController implements Initializable {

    @FXML
    @Getter
    private MenuBar menuBar;

    /**
     * Initializes the controller
     * @param location The location used to resolve relative paths for the root object,
     *                 or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or null if the location is not known.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Handles the quit button on the application
     */
    public void onQuit() {
        Stage stage = (Stage) this.menuBar.getScene().getWindow();
        stage.close();
    }

    public void onAbout() {
    }

    public void onPreferences() {
    }
}
