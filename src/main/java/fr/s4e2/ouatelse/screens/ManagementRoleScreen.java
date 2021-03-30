package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Role Management Screen
 */
public class ManagementRoleScreen extends BaseScreen {

    /**
     * Automatically configures the role management screen
     */
    public ManagementRoleScreen(Store authentificationStore) {
        super("management_roles.fxml", "Menu Gestion des Rôles", authentificationStore);
    }
}
