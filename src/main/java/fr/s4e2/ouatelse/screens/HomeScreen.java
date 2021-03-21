package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;

/**
 * Home Screen
 */
public class HomeScreen extends BaseScreen {

    /**
     * Automatically configures the home menu according to the following parameters
     *
     * @param user  the User to be displayed
     * @param store the Store to be displayed
     */
    public HomeScreen(User user, Store store) {
        super("home.fxml", "Accueil", store, user);
    }
}
