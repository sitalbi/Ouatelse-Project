package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerUser;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.AuthStoreScreen;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.AuthUserScreen}
 */
public class AuthUserController extends BaseController {
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField idField;
    @FXML
    private Label errorMessageField;
    private final EntityManagerUser entityManagerUser = Main.getDatabaseManager().getEntityManagerUser();

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
     * Connects as a user (User Authentication)
     */
    public void onConnectionButtonClick() {
        if (idField.getText().trim().isEmpty() && passwordField.getText().trim().isEmpty()) {
            this.errorMessageField.setText("Veuillez remplir tout les champs");
            return;
        }

        User user = this.entityManagerUser.getUserIfExists(this.idField.getText().trim(), this.passwordField.getText().trim());

        if (user == null) {
            this.errorMessageField.setText("Utilisateur inexistant / mauvais mot de passe");
            return;
        }

        new AuthStoreScreen(user).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
