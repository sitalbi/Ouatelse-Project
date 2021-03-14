package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.HomeScreen;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.AuthStoreScreen}
 */
public class AuthStoreController extends BaseController {

    @FXML
    private Label errorMessageField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField idField;
    @Setter
    @Getter
    private User currentUser;
    @Setter
    private EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();

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
        super.initialize(location, resources);

        this.errorMessageField.setText("");

        // enter to connect
        this.passwordField.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ENTER) return;
            this.onConnectionButtonClick();
        });
    }

    /**
     * Handles the button click event for the connection button
     * <p>
     * Connects to a store (Store Authentication)
     */
    public void onConnectionButtonClick() {
        if (idField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            this.errorMessageField.setText("Veuillez remplir tout les champs");
            return;
        }

        Store store = this.entityManagerStore.authGetStoreIfExists(idField.getText().trim(), passwordField.getText().trim());

        if (store == null) {
            this.errorMessageField.setText("Erreur : Le magasin n'existe pas / mot de passe faux");
            return;
        }

        new HomeScreen(currentUser, store).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
