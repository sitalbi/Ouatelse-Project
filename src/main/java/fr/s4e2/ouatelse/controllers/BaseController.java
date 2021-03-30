package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.objects.Store;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base Controller for all screens
 */
public class BaseController implements Initializable {

    @FXML
    @Getter
    private MenuBar menuBar;

    @FXML
    @Getter
    private BorderPane baseBorderPane;

    @Getter
    private Store authentificationStore;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nothing to initialize
    }

    /**
     * Handles the Quit menu item click.
     */
    public void onQuit() {
        int choice = JOptionPane.showConfirmDialog(null,
                "Voulez-vous vraiment quitter l'application?",
                "Quitter", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            Stage stage = (Stage) this.menuBar.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Handles the About menu item click.
     */
    public void onAbout() {
        JOptionPane.showMessageDialog(null,
                "Logiciel Outaelse.\n Développeurs: S. CAILLAT, M.CARAVATI, V. LEROY, S. TALBI, L. VOLANT",
                "A propos", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Handles the Preferences menu item click.
     */
    public void onPreferences() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Set the store in which the user has logged in
     *
     * @param authentificationStore The store in which the user has logged in
     */
    public void setAuthentificationStore(Store authentificationStore) {
        this.authentificationStore = authentificationStore;
    }
}
