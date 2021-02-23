package fr.s4e2.ouatelse.controllers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.Dao;
import fr.s4e2.ouatelse.databaseInterface.databaseUserInterface;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.AuthStoreScreen;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AuthUserController extends BaseController {

    public JFXPasswordField passwordField;
    public JFXTextField idField;
    public Label errorMessageField;
    private Dao<User, Long> userDao;

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

    public void onConnectionButtonClick() {
        if (idField.getText().trim().isEmpty() && passwordField.getText().trim().isEmpty()) {
            this.errorMessageField.setText("Veuillez remplir tout les champs");
            return;
        }

        User user = databaseUserInterface.getUserIfExists(this.idField.getText().trim(), this.passwordField.getText().trim());

        if (user == null) {
            this.errorMessageField.setText("Utilisateur inexistant / mauvais mot de passe");
            return;
        }

        new AuthStoreScreen(user).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
