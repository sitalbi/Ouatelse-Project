package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.User;

public class AuthStoreScreen extends BaseScreen {

    public AuthStoreScreen(User user) {
        super("auth_store.fxml", "Ouatelse - Authentification Magasin", user);
    }
}
