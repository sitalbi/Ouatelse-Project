package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Vendor Management Screen
 */
public class ManagementVendorScreen extends BaseScreen {

    public ManagementVendorScreen(Store authentificationStore) {
        super("management_vendors.fxml", "Menu Gestion des Fournisseurs", authentificationStore);
    }
}
