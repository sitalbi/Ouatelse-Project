package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.User;

/**
 * Store Authentication Screen
 */
public class AuthStoreScreen extends BaseScreen {

    /**
     * Automatically configures the user login screen to a store
     *
     * @param user The user who logs in
     */
    public AuthStoreScreen(User user) {
        super("auth_store.fxml", "Authentification Magasin", user);
    }
}
