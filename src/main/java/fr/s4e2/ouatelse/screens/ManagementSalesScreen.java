package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Sales Management Screen
 */
public class ManagementSalesScreen extends BaseScreen {

    public ManagementSalesScreen(Store authentificationStore) {
        super("management_sales.fxml", "Menu Gestion des Ventes", authentificationStore);
    }
}
