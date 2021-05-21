package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * User Management Screen
 */
public class ManagementUserScreen extends BaseScreen {

    /**
     * Automatically configures the user management screen
     */
    public ManagementUserScreen(Store authentificationStore) {
        super("management_users.fxml", "Menu Gestion des Utilisateurs", authentificationStore);
    }
}
