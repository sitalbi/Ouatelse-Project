package fr.s4e2.ouatelse.controllers;

public class AuthUserController extends BaseController {

    public void onConnectionButtonClick() {
        if (idField.getText().trim().isEmpty() && passwordField.getText().trim().isEmpty()) {
            this.errorMessageField.setText("Veuillez remplir tout les champs");
            return;
        }

        User user = null;
        try {
            //noinspection UnstableApiUsage
            user = this.userDao.query(this.userDao.queryBuilder().where()
                    .eq("credentials", this.idField.getText().trim())
                    .and().eq("password", Hashing.sha256().hashString(this.passwordField.getText().trim(), StandardCharsets.UTF_8).toString())
                    .prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (user == null) {
            this.errorMessageField.setText("Utilisateur inexistant / mauvais mot de passe");
            return;
        }

        new AuthStoreScreen(user).open();
        ((Stage) this.errorMessageField.getScene().getWindow()).close();
    }
}
