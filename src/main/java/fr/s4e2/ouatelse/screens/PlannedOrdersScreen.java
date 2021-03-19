package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

public class PlannedOrdersScreen extends BaseScreen {
    public PlannedOrdersScreen(Store store) {
        super("management_planned_orders.fxml", "Menu Programmation des commandes", store);
    }
}
