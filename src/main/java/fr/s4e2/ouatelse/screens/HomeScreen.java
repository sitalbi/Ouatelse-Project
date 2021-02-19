package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;

public class HomeScreen extends BaseScreen {
    public HomeScreen(User user, Store store) {
        super("home.fxml", "Ouatelse - Accueil", user, store);
    }
}
