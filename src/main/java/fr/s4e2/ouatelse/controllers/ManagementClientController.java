package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerAddress;
import fr.s4e2.ouatelse.managers.EntityManagerClient;
import fr.s4e2.ouatelse.objects.Address;
import fr.s4e2.ouatelse.objects.Civility;
import fr.s4e2.ouatelse.objects.Client;
import fr.s4e2.ouatelse.utils.JFXUtils;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementClientScreen}
 */
public class ManagementClientController extends BaseController {

    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String CLIENT_ALREADY_EXISTS = "Ce client existe déjà!";
    private static final String NOT_A_ZIPCODE = "Le code postal est incorrect!";
    private static final String NOT_A_VALID_DOB = "La date de naissance est invalide!";

    private final EntityManagerClient entityManagerClient = Main.getDatabaseManager().getEntityManagerClient();
    private final EntityManagerAddress entityManagerAddress = Main.getDatabaseManager().getEntityManagerAddress();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @FXML
    private Label errorMessage;
    @FXML
    private JFXComboBox<Civility> clientCivilityDropdown;
    @FXML
    private JFXTextField clientLastNameInput;
    @FXML
    private JFXTextField clientFirstNameInput;
    @FXML
    private JFXTextField clientEmailInput;
    @FXML
    private JFXTextField clientPhoneInput;
    @FXML
    private JFXTextField clientAddressInput;
    @FXML
    private JFXTextField clientCityInput;
    @FXML
    private JFXTextField clientZipInput;
    @FXML
    private JFXTextField clientFaxInput;
    @FXML
    private JFXTextArea clientDetailsInput;
    @FXML
    private JFXDatePicker clientBirthDate;
    @FXML
    private JFXTreeTableView<Client.ClientTree> clientTreeTableView;
    @FXML
    private JFXTextField clientSearchBar;
    private Client currentClient;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        Arrays.stream(Civility.values()).forEach(value -> clientCivilityDropdown.getItems().add(value));

        this.loadClientTreeTable();
        this.clientBirthDate.setConverter(JFXUtils.getDateConverter());

        // escape to unselect item in the table
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;
            if (clientTreeTableView.getSelectionModel().getSelectedItem() == null) return;

            this.clientTreeTableView.getSelectionModel().clearSelection();
            this.clearInformation();
        });

        // selected element in table listener
        this.clientTreeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                this.currentClient = null;
                return;
            }
            try {
                this.currentClient = this.entityManagerClient.executeQuery(this.entityManagerClient.getQueryBuilder()
                        .where().eq("id", newSelection.getValue().getId().getValue())
                        .prepare()
                ).stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }

            this.loadClientInformation();

        });

        // search employee
        this.clientSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchClientFromText(newValue.trim()));
    }

    /**
     * Handles the button click event for the confirm button
     * <p>
     * Confirms the creation or edit of a client
     *
     * @throws SQLException if the client couldn't be created
     */
    public void onConfirmButtonClick() throws SQLException {
        //necessary fields
        if (clientFirstNameInput.getText().trim().isEmpty() || clientLastNameInput.getText().trim().isEmpty() ||
                clientPhoneInput.getText().trim().isEmpty() || clientEmailInput.getText().trim().isEmpty() ||
                clientCivilityDropdown.getSelectionModel().isEmpty() || clientBirthDate.getValue() == null) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            return;
        }

        // client already exists!
        if (!this.isEditing()) {
            Client client = this.entityManagerClient.executeQuery(
                    this.entityManagerClient.getQueryBuilder()
                            .where().eq("name", clientFirstNameInput.getText().trim())
                            .and().eq("surname", clientLastNameInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (client != null) {
                this.errorMessage.setText(CLIENT_ALREADY_EXISTS);
                this.clientLastNameInput.getParent().requestFocus();
                return;
            }
        }

        // incorrect zipcode
        Integer zipCode = Utils.getNumber(clientZipInput.getText().trim());
        if (zipCode == null || zipCode > 99999) {
            this.errorMessage.setText(NOT_A_ZIPCODE);
            this.clientZipInput.getParent().requestFocus();
            return;
        }

        // incorrect date of birth
        Date dob = Utils.localDateToDate(clientBirthDate.getValue());
        if (dob.after(new Date())) {
            this.errorMessage.setText(NOT_A_VALID_DOB);
            return;
        }

        if (this.isEditing()) {
            // edits client
            this.currentClient.getAddress().setZipCode(zipCode);
            this.currentClient.getAddress().setCity(clientCityInput.getText().trim());
            this.currentClient.getAddress().setStreetNameAndNumber(clientAddressInput.getText().trim());
            this.entityManagerAddress.update(currentClient.getAddress());

            this.updateClient(currentClient);
            this.entityManagerClient.update(currentClient);

            // updates client in the table
            this.addClientToTreeTable(currentClient);
            this.clientTreeTableView.getRoot().getChildren().remove(clientTreeTableView.getSelectionModel().getSelectedItem());
            this.clientTreeTableView.getSelectionModel().clearSelection();

        } else {
            // creates address
            Address newAddress = new Address(zipCode, clientCityInput.getText().trim(), clientAddressInput.getText().trim());
            this.entityManagerAddress.create(newAddress);

            // creation of a new client
            Client newClient = new Client();
            newClient.setAddress(newAddress);
            this.updateClient(newClient);
            this.entityManagerClient.create(newClient);

            // adds created client to the table
            this.addClientToTreeTable(newClient);
        }
        this.clearInformation();
    }

    /**
     * Handles the button click event for the delete button
     * <p>
     * Deletes the selected client
     */
    public void onDeleteButtonClick() {
        if (currentClient == null) return;

        this.entityManagerClient.delete(currentClient);
        this.clientTreeTableView.getRoot().getChildren().remove(clientTreeTableView.getSelectionModel().getSelectedItem());
        this.clientTreeTableView.getSelectionModel().clearSelection();
        this.clearInformation();
    }

    /**
     * Clears all the inputs and resets them to default
     */
    private void clearInformation() {
        this.errorMessage.setText("");
        this.clientFirstNameInput.setText("");
        this.clientLastNameInput.setText("");
        this.clientEmailInput.setText("");
        this.clientPhoneInput.setText("");
        this.clientAddressInput.setText("");
        this.clientCityInput.setText("");
        this.clientZipInput.setText("");
        this.clientFaxInput.setText("");
        this.clientBirthDate.getEditor().clear();
        this.clientCivilityDropdown.getSelectionModel().select(-1);
        this.clientDetailsInput.setText("");
    }

    /**
     * Loads selected client's information in the inputs
     */
    private void loadClientInformation() {
        this.clearInformation();

        if (currentClient == null) return;

        this.clientFirstNameInput.setText(currentClient.getName());
        this.clientLastNameInput.setText(currentClient.getSurname());
        this.clientPhoneInput.setText(currentClient.getMobilePhoneNumber());
        this.clientEmailInput.setText(currentClient.getEmail());
        this.clientAddressInput.setText(currentClient.getAddress().getStreetNameAndNumber());
        this.clientCityInput.setText(currentClient.getAddress().getCity());
        this.clientZipInput.setText(String.valueOf(currentClient.getAddress().getZipCode()));
        this.clientFaxInput.setText(currentClient.getFax());
        this.clientCivilityDropdown.getSelectionModel().select(currentClient.getCivility());
        this.clientBirthDate.setValue(Utils.dateToLocalDate(currentClient.getBirthDate()));
        this.clientBirthDate.getEditor().setText(clientBirthDate.getConverter().toString(clientBirthDate.getValue()));
        this.clientDetailsInput.setText(currentClient.getDetails());
    }

    /**
     * Loads the client tree table on the right hand side
     */
    private void loadClientTreeTable() {
        JFXTreeTableColumn<Client.ClientTree, String> id = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<Client.ClientTree, String> lastName = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Client.ClientTree, String> firstName = new JFXTreeTableColumn<>("Prénom");
        JFXTreeTableColumn<Client.ClientTree, String> email = new JFXTreeTableColumn<>("Email");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId().asString());
        lastName.setCellValueFactory(param -> param.getValue().getValue().getSurname());
        firstName.setCellValueFactory(param -> param.getValue().getValue().getName());
        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        ObservableList<Client.ClientTree> clients = FXCollections.observableArrayList();
        this.entityManagerClient.getQueryForAll().forEach(client -> clients.add(client.toClientTree()));

        TreeItem<Client.ClientTree> root = new RecursiveTreeItem<>(clients, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.clientTreeTableView.getColumns().setAll(id, lastName, firstName, email);
        this.clientTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.clientTreeTableView.setRoot(root);
        this.clientTreeTableView.setShowRoot(false);
    }

    /**
     * Updates the client object with all of the inputted values (after checks)
     *
     * @param client the User to update
     */
    private void updateClient(Client client) {
        client.setSurname(clientLastNameInput.getText().trim().isEmpty() ? "" : clientLastNameInput.getText().trim());
        client.setName(clientFirstNameInput.getText().trim().isEmpty() ? "" : clientFirstNameInput.getText().trim());
        client.setEmail(clientEmailInput.getText().trim().isEmpty() ? "" : clientEmailInput.getText().trim());
        client.setMobilePhoneNumber(clientPhoneInput.getText().trim().isEmpty() ? "" : clientPhoneInput.getText().trim());
        client.setHomePhoneNumber(clientPhoneInput.getText().trim().isEmpty() ? "" : clientPhoneInput.getText().trim());
        client.setWorkPhoneNumber(clientPhoneInput.getText().trim().isEmpty() ? "" : clientPhoneInput.getText().trim());
        client.setFax(clientFaxInput.getText() == null ? "" : clientFaxInput.getText().trim());
        client.setBirthDate(Utils.localDateToDate(clientBirthDate.getValue()));
        client.setCivility(clientCivilityDropdown.getValue());
        client.setDetails(clientDetailsInput.getText() == null ? "" : clientDetailsInput.getText().trim());
    }

    /**
     * Adds a client to the tree table with a user instance
     *
     * @param client a Client to add in the tree table
     */
    private void addClientToTreeTable(Client client) {
        this.clientTreeTableView.getRoot().getChildren().add(new TreeItem<>(client.toClientTree()));
    }

    /**
     * Returns if a client is selected/being edited
     *
     * @return if a client is being edited
     */
    private boolean isEditing() {
        return this.currentClient != null;
    }

    /**
     * Searches a client in the database from its credentials
     *
     * @param input the searched client name
     */
    private void searchClientFromText(String input) {
        this.clientTreeTableView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.loadClientTreeTable();
            return;
        }

        this.clearInformation();

        try {
            List<Client> searchResults = entityManagerClient.executeQuery(entityManagerClient.getQueryBuilder()
                    .where().like("surname", "%" + input + "%")
                    .prepare()
            );
            searchResults.forEach(client -> this.clientTreeTableView.getRoot().getChildren().add(new TreeItem<>(client.toClientTree())));
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }
}
