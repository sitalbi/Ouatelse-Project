package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base Controller
 */
public class BaseController implements Initializable {

    @FXML
    @Getter
    private MenuBar menuBar;

    /**
     * Called to initialize a controller after its root element has been completely processed
     *
     * @param location  The Location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources The Resources used to localize the root object, or null if the resources are not found
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Manages what happens when you leave the current stage
     */
    public void onQuit() {
        Stage stage = (Stage) this.menuBar.getScene().getWindow();
        stage.close();
    }

    /**
     * TODO
     */
    public void onAbout() {
    }

    /**
     * TODO
     */
    public void onPreferences() {
    }
}
