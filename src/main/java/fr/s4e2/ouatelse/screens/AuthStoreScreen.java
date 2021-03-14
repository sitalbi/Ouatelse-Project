package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.User;

/**
 * Store Authentication Screen
 */
public class AuthStoreScreen extends BaseScreen {

    public AuthStoreScreen(User user) {
        super("auth_store.fxml", "Authentification Magasin", user);
    }
}
