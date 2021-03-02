package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.*;
import fr.s4e2.ouatelse.objects.User.UserTree;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * User Management Controller
 */
public class ManagementUserController extends BaseController {

    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String USER_ALREADY_EXISTS = "Cet utilisateur existe déjà!";
    private static final String NOT_A_ZIPCODE = "Le code postal est incorrect!";
    private static final String PASSWORD_NOT_MATCHING = "Mot de passe non concordants!";

    @FXML
    private Label errorMessage;
    @FXML
    private JFXTextField userIdInput;
    @FXML
    private JFXComboBox<Civility> userCivilityDropdown;
    @FXML
    private JFXTextField userLastNameInput;
    @FXML
    private JFXTextField userFirstNameInput;
    @FXML
    private JFXTextField userEmailInput;
    @FXML
    private JFXTextField userPhoneInput;
    @FXML
    private JFXTextField userAddressInput;
    @FXML
    private JFXTextField userCityInput;
    @FXML
    private JFXTextField userZipcodeInput;
    @FXML
    private JFXComboBox<Role> userRoleDropdown;
    @FXML
    private JFXPasswordField userPasswordInput;
    @FXML
    private JFXPasswordField userConfirmPasswordInput;
    @FXML
    private JFXComboBox<Store> userStoreDropdown;
    @FXML
    private JFXDatePicker userHiringDate;
    @FXML
    private JFXDatePicker userBirthDate;
    @FXML
    private JFXCheckBox userActiveToggle;
    @FXML
    private JFXTreeTableView<UserTree> usersTreeTableView;

    private Dao<User, Long> userDao;
    private Dao<Address, Long> addressDao;
    private Dao<Role, Long> roleDao;
    private Dao<Store, Long> storeDao;
    private User currentUser;

    /**
     * Initializes the controller
     *
     * @param location  The location used to resolve relative paths for the root object,
     *                  or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or null if the location is not known.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        try {
            this.userDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), User.class);
            this.addressDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Address.class);
            this.roleDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Role.class);
            this.storeDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Store.class);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.roleDao.forEach(role -> userRoleDropdown.getItems().add(role));
        this.storeDao.forEach(store -> userStoreDropdown.getItems().add(store));
        Arrays.stream(Civility.values()).forEach(value -> userCivilityDropdown.getItems().add(value));

        this.loadUserTreeTable();

        // format date
        this.userHiringDate.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            /**
             * Converts a LocalDate into a String
             * @param localDate the LocalDate
             * @return a String
             */
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) return "";
                return dateTimeFormatter.format(localDate);
            }

            /**
             * Converts a String into a LocalDate
             * @param dateString the String
             * @return a LocalDate
             */
            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
        this.userBirthDate.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            /**
             * Converts a LocalDate into a String
             * @param localDate the LocalDate
             * @return a String
             */
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) return "";
                return dateTimeFormatter.format(localDate);
            }

            /**
             * Converts a String into a LocalDate
             * @param dateString the String
             * @return a LocalDate
             */
            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });

        // escape to unselect item in the table
        this.usersTreeTableView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            TreeItem<UserTree> userTree = usersTreeTableView.getSelectionModel().getSelectedItem();
            if (userTree == null) return;
            this.usersTreeTableView.getSelectionModel().clearSelection();
            this.clearInformation();
        });

        // selected element in table listener
        this.usersTreeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    this.currentUser = userDao.query(userDao.queryBuilder()
                            .where().eq("credentials", newSelection.getValue().getId().getValue())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                this.loadUserInformation();
            } else {
                this.currentUser = null;
            }
        });
    }

    /**
     * Handles the confirm button event
     *
     * @throws SQLException if the User couldn't be created
     */
    public void onConfirmButtonClick() throws SQLException {
        // necessary fields
        if (userIdInput.getText().trim().isEmpty() || userLastNameInput.getText().trim().isEmpty() || userFirstNameInput.getText().trim().isEmpty()
                || userEmailInput.getText().trim().isEmpty() || userPhoneInput.getText().trim().isEmpty()
                || userAddressInput.getText().trim().isEmpty() || userCityInput.getText().trim().isEmpty() || userZipcodeInput.getText().trim().isEmpty()
                || userRoleDropdown.getSelectionModel().isEmpty() || userStoreDropdown.getSelectionModel().isEmpty() || userCivilityDropdown.getSelectionModel().isEmpty()
                || userHiringDate.getValue() == null || userBirthDate.getValue() == null) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            return;
        }

        if (!this.isEditing() && (userPasswordInput.getText().isEmpty() || userConfirmPasswordInput.getText().isEmpty())) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            this.userPasswordInput.getParent().requestFocus();
            return;
        }

        // user already exists!
        if (!this.isEditing()) {
            User user = userDao.query(
                    userDao.queryBuilder()
                            .where().eq("credentials", userIdInput.getText().trim())
                            .or().eq("email", userEmailInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (user != null) {
                this.errorMessage.setText(USER_ALREADY_EXISTS);
                this.userIdInput.getParent().requestFocus();
                return;
            }
        }

        // not matching passwords
        if (!userPasswordInput.getText().equals(userConfirmPasswordInput.getText())) {
            this.errorMessage.setText(PASSWORD_NOT_MATCHING);
            this.userPasswordInput.getParent().requestFocus();
            return;
        }

        // incorrect zipcode
        Integer zipCode = Utils.getNumber(userZipcodeInput.getText().trim());
        if (zipCode == null || zipCode > 99999) {
            this.errorMessage.setText(NOT_A_ZIPCODE);
            this.userZipcodeInput.getParent().requestFocus();
            return;
        }

        if (this.isEditing()) {
            // edits user
            try {
                this.currentUser.getAddress().setZipCode(zipCode);
                this.currentUser.getAddress().setCity(userCityInput.getText().trim());
                this.currentUser.getAddress().setAddress(userAddressInput.getText().trim());
                this.addressDao.update(currentUser.getAddress());

                this.updateUser(currentUser);
                this.userDao.update(currentUser);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            // updates user in the table
            this.addUserToTreeTable(currentUser);
            this.usersTreeTableView.getRoot().getChildren().remove(usersTreeTableView.getSelectionModel().getSelectedItem());
            this.usersTreeTableView.getSelectionModel().clearSelection();

        } else {
            // creates address
            Address newAddress = new Address(zipCode, userCityInput.getText().trim(), userAddressInput.getText().trim());
            try {
                this.addressDao.create(newAddress);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            // creation of a new user
            User newUser = new User();
            try {
                newUser.setAddress(newAddress);
                this.updateUser(newUser);
                this.userDao.create(newUser);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            // adds created user to the table
            this.addUserToTreeTable(newUser);
        }
        this.clearInformation();
    }

    /**
     * Handles the delete button click event
     *
     * @throws SQLException if the User wasn't successfully deleted
     */
    public void onDeleteButtonClick() throws SQLException {
        if (currentUser == null) return;

        userDao.delete(currentUser);
        this.usersTreeTableView.getRoot().getChildren().remove(usersTreeTableView.getSelectionModel().getSelectedItem());
        this.usersTreeTableView.getSelectionModel().clearSelection();
        this.clearInformation();
    }

    /**
     * Loads selected User's information in the inputs
     */
    private void loadUserInformation() {
        this.clearInformation();

        if (currentUser == null) return;

        this.userIdInput.setText(currentUser.getCredentials());
        this.userIdInput.setDisable(true);
        this.userFirstNameInput.setText(currentUser.getName());
        this.userLastNameInput.setText(currentUser.getSurname());
        this.userPhoneInput.setText(currentUser.getMobilePhoneNumber());
        this.userEmailInput.setText(currentUser.getEmail());
        this.userAddressInput.setText(currentUser.getAddress().getAddress());
        this.userCityInput.setText(currentUser.getAddress().getCity());
        this.userZipcodeInput.setText(String.valueOf(currentUser.getAddress().getZipCode()));
        //this.userRoleDropdown.getSelectionModel().select(currentUser.getRole()); <- doesn't work
        this.userRoleDropdown.getItems().stream()
                .filter(r -> r.getName().equals(currentUser.getRole().getName()))
                .findFirst()
                .ifPresent(r -> userRoleDropdown.getSelectionModel().select(r));
        //this.userStoreDropdown.getSelectionModel().select(currentUser.getWorkingStore()); <- doesn't work
        this.userStoreDropdown.getItems().stream()
                .filter(i -> i.getId().equals(currentUser.getWorkingStore().getId()))
                .findFirst()
                .ifPresent(i -> userStoreDropdown.getSelectionModel().select(i));
        this.userCivilityDropdown.getSelectionModel().select(currentUser.getCivility());
        this.userHiringDate.setValue(Utils.dateToLocalDate(currentUser.getHiringDate()));
        this.userHiringDate.getEditor().setText(userHiringDate.getConverter().toString(userHiringDate.getValue()));
        this.userHiringDate.setDisable(true);
        this.userBirthDate.setValue(Utils.dateToLocalDate(currentUser.getBirthDate()));
        this.userBirthDate.getEditor().setText(userBirthDate.getConverter().toString(userBirthDate.getValue()));
        this.userActiveToggle.setSelected(currentUser.getStatus() == PersonState.UNEMPLOYED);
    }

    /**
     * Clears all the inputs and resets them to default
     */
    private void clearInformation() {
        this.errorMessage.setText("");
        this.userIdInput.setText("");
        this.userIdInput.setDisable(false);
        this.userLastNameInput.setText("");
        this.userFirstNameInput.setText("");
        this.userEmailInput.setText("");
        this.userPhoneInput.setText("");
        this.userAddressInput.setText("");
        this.userCityInput.setText("");
        this.userZipcodeInput.setText("");
        this.userPasswordInput.setText("");
        this.userConfirmPasswordInput.setText("");
        this.userActiveToggle.setSelected(false);
        this.userHiringDate.getEditor().clear();
        this.userHiringDate.setDisable(false);
        this.userBirthDate.getEditor().clear();
        this.userCivilityDropdown.getSelectionModel().select(-1);
        this.userStoreDropdown.getSelectionModel().select(-1);
        this.userRoleDropdown.getSelectionModel().select(-1);
    }

    /**
     * Loads the User tree table on the right hand side
     */
    private void loadUserTreeTable() {
        JFXTreeTableColumn<UserTree, String> id = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<UserTree, String> lastName = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<UserTree, String> firstName = new JFXTreeTableColumn<>("Prénom");
        JFXTreeTableColumn<UserTree, String> role = new JFXTreeTableColumn<>("Rôle");
        JFXTreeTableColumn<UserTree, String> store = new JFXTreeTableColumn<>("Magasin");
        JFXTreeTableColumn<UserTree, String> status = new JFXTreeTableColumn<>("État");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId());
        lastName.setCellValueFactory(param -> param.getValue().getValue().getLastName());
        firstName.setCellValueFactory(param -> param.getValue().getValue().getFirstName());
        role.setCellValueFactory(param -> param.getValue().getValue().getRole());
        store.setCellValueFactory(param -> param.getValue().getValue().getStoreName());
        status.setCellValueFactory(param -> param.getValue().getValue().getStatus());

        id.setContextMenu(null);
        lastName.setContextMenu(null);
        firstName.setContextMenu(null);
        role.setContextMenu(null);
        store.setContextMenu(null);
        status.setContextMenu(null);

        ObservableList<UserTree> users = FXCollections.observableArrayList();
        try {
            this.userDao.queryForAll().forEach(user -> users.add(new UserTree(
                    user.getCredentials(),
                    user.getSurname(),
                    user.getName(),
                    user.getRole(),
                    user.getWorkingStore(),
                    user.getStatus()
            )));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        TreeItem<UserTree> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.usersTreeTableView.getColumns().setAll(id, lastName, firstName, role, store, status);
        this.usersTreeTableView.setRoot(root);
        this.usersTreeTableView.setShowRoot(false);
    }

    /**
     * Updates the User object with all of the inputted values (after checks)
     *
     * @param user the User to update
     */
    private void updateUser(User user) {
        user.setCredentials(userIdInput.getText().trim());
        user.setSurname(userLastNameInput.getText().trim());
        user.setName(userFirstNameInput.getText().trim());
        user.setEmail(userEmailInput.getText().trim());
        user.setMobilePhoneNumber(userPhoneInput.getText().trim());
        user.setRole(userRoleDropdown.getValue());
        if (!userPasswordInput.getText().trim().isEmpty()) user.setPassword(userPasswordInput.getText().trim());
        user.setWorkingStore(userStoreDropdown.getValue());
        user.setHiringDate(Utils.localDateToDate(userHiringDate.getValue()));
        user.setBirthDate(Utils.localDateToDate(userBirthDate.getValue()));
        user.setStatus(userActiveToggle.isSelected() ? PersonState.UNEMPLOYED : PersonState.EMPLOYED);
        user.setCivility(userCivilityDropdown.getValue());
    }

    /**
     * Adds a User to the tree table with a user instance
     *
     * @param user a User to add in the tree table
     */
    private void addUserToTreeTable(User user) {
        this.usersTreeTableView.getRoot().getChildren().add(new TreeItem<>(new UserTree(
                user.getCredentials(),
                user.getSurname(),
                user.getName(),
                user.getRole(),
                user.getWorkingStore(),
                user.getStatus()
        )));
    }

    /**
     * Returns if a User is selected/being edited
     *
     * @return if a User is being edited
     */
    private boolean isEditing() {
        return this.currentUser != null;
    }
}
