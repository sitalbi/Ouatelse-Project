package fr.s4e2.ouatelse.screens;

/**
 * Users management menu
 */
public class ManagementUserScreen extends BaseScreen {

    /**
     * Automatically configures the user management screen
     */
    public ManagementUserScreen() {
        super("management_users.fxml", "Ouatelse - Menu Gestion des Utilisateurs");
        this.addStyleSheet("css/management.css");
    }
}
