package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Product Management Screen
 */
public class ManagementProductScreen extends BaseScreen {

    public ManagementProductScreen(Store authentificationStore) {
        super("management_products.fxml", "Menu Gestion des Produits", authentificationStore);
    }
}
