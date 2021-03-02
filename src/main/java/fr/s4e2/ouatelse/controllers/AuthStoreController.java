package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.HomeScreen;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller of the authentification of the store
 */
public class AuthStoreController extends BaseController {

    public Label errorMessageField;
    public JFXPasswordField passwordField;
    public JFXTextField idField;
    @Setter
    @Getter
    private User currentUser;
    @Setter
    private EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();

    /**
     * Initializes the controller
     *
     * @param location  The location used to resolve relative paths for the root object,
     *                  or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or null if the location is not known.
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
     * Handles the connection to a store
     */
    public void onConnectionButtonClick() {
        if (idField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            this.errorMessageField.setText("Veuillez remplir tout les champs");
            return;
        }

        Store store = this.entityManagerStore.getStoreIfExists(idField.getText().trim(), passwordField.getText().trim());

        if (store == null) {
            this.errorMessageField.setText("Erreur : Le magasin n'existe pas / mot de passe faux");
            return;
        }

        new HomeScreen(currentUser, store).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
