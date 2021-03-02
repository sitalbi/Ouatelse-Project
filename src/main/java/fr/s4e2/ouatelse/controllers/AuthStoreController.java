package fr.s4e2.ouatelse.controllers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.HomeScreen;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The controller of the authentification of the store
 */
public class AuthStoreController extends BaseController {

    public Label errorMessageField;
    public JFXPasswordField passwordField;
    public JFXTextField idField;
    @Setter
    private User currentUser;
    private Dao<Store, Long> storeDao;

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
        try {
            this.storeDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Store.class);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

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

        Store store = null;
        try {
            //noinspection UnstableApiUsage
            store = this.storeDao.query(this.storeDao.queryBuilder().where()
                    .eq("id", this.idField.getText().trim())
                    .and().eq("password", Hashing.sha256().hashString(this.passwordField.getText().trim(), StandardCharsets.UTF_8).toString()
                    ).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (store == null) {
            this.errorMessageField.setText("Erreur : Le magasin n'existe pas / mot de passe faux");
            return;
        }

        new HomeScreen(currentUser, store).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
