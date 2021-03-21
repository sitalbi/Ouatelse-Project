package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Store Management Screen
 */
public class ManagementStoreScreen extends BaseScreen {

    /**
     * Automatically configures the store management screen
     */
    public ManagementStoreScreen(Store authentificationStore) {
        super("management_stores.fxml", "Menu Gestion des Magasins", authentificationStore);
    }
}
