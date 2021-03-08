package fr.s4e2.ouatelse.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import lombok.Getter;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class BaseController implements Initializable {

    @FXML
    @Getter
    private MenuBar menuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void onQuit() {
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Voulez-vous vraiment quitter l'application?",
                "Quitter", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            Stage stage = (Stage) this.menuBar.getScene().getWindow();
            stage.close();
        }
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(
                null,
                "Logiciel Outaelse.\n Développeurs: S. CAILLAT, M.CARAVATI, V. LEROY, S. TALBI, L. VOLANT",
                "A propos", JOptionPane.PLAIN_MESSAGE);
    }

    public void onPreferences() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
