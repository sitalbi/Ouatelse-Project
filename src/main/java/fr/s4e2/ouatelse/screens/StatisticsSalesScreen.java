package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;

public class StatisticsSalesScreen extends BaseScreen {
    public StatisticsSalesScreen(Store authentificationStore) {
        super("statistics_sales.fxml", "Statistiques des Ventes", authentificationStore);
    }
}
