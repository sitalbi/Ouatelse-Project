package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

/**
 * Stock Management Screen
 */
public class ManagementStockScreen extends BaseScreen {

    public ManagementStockScreen(Store authentificationStore) {
        super("management_stocks.fxml", "Menu Gestion des Stocks", authentificationStore);
    }
}
