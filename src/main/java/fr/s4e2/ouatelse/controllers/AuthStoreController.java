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
