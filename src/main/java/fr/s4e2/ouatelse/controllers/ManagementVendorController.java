package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerAddress;
import fr.s4e2.ouatelse.managers.EntityManagerVendor;
import fr.s4e2.ouatelse.objects.Address;
import fr.s4e2.ouatelse.objects.Vendor;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static fr.s4e2.ouatelse.objects.Vendor.VendorTree;

public class ManagementVendorController extends BaseController {

    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String USER_ALREADY_EXISTS = "Cet utilisateur existe déjà!";
    private static final String NOT_A_ZIPCODE = "Le code postal est incorrect!";
    private final EntityManagerAddress entityManagerAddress = Main.getDatabaseManager().getEntityManagerAddress();
    private final EntityManagerVendor entityManagerVendor = Main.getDatabaseManager().getEntityManagerVendor();
    @FXML
    private JFXTextField vendorNameInput;
    @FXML
    private JFXTextField vendorAddressInput;
    @FXML
    private JFXTextField vendorCityInput;
    @FXML
    private JFXTextField vendorZipcodeInput;
    @FXML
    private JFXTextField vendorEmailInput;
    @FXML
    private JFXTextField vendorPhoneInput;
    @FXML
    private JFXCheckBox vendorContractCheckBox;
    @FXML
    private Label errorMessage;
    @FXML
    private JFXTreeTableView<VendorTree> vendorsTreeTableView;
    private Vendor currentVendor;

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

        this.loadVendorsTreeTable();

        // escape to unselect item in the table
        this.vendorsTreeTableView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            if (vendorsTreeTableView.getSelectionModel().getSelectedItem() == null) return;
            this.vendorsTreeTableView.getSelectionModel().clearSelection();
            this.clearInformation();
        });

        // selected element in table listener
        this.vendorsTreeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    this.currentVendor = this.entityManagerVendor.executeQuery(this.entityManagerVendor.getQueryBuilder()
                            .where().eq("name", newSelection.getValue().getName().getValue())
                            .or().eq("email", newSelection.getValue().getEmail().getValue())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                this.loadVendorInformation();
            } else {
                this.currentVendor = null;
            }
        });
    }

    /**
     * Handles the confirm button click event
     */
    public void onConfirmButtonClick() throws SQLException {
        if (vendorNameInput.getText().trim().isEmpty() || vendorAddressInput.getText().trim().isEmpty() || vendorCityInput.getText().trim().isEmpty()
                || vendorZipcodeInput.getText().trim().isEmpty() || vendorEmailInput.getText().trim().isEmpty() || vendorPhoneInput.getText().trim().isEmpty()) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            return;
        }

        // vendor already exists!
        if (!this.isEditing()) {
            Vendor vendor = this.entityManagerVendor.executeQuery(
                    this.entityManagerVendor.getQueryBuilder()
                            .where().eq("name", vendorNameInput.getText().trim())
                            .or().eq("email", vendorEmailInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (vendor != null) {
                this.errorMessage.setText(USER_ALREADY_EXISTS);
                this.vendorNameInput.getParent().requestFocus();
                return;
            }
        }

        // incorrect zipcode
        Integer zipCode = Utils.getNumber(vendorZipcodeInput.getText().trim());
        if (zipCode == null || zipCode > 99999) {
            this.errorMessage.setText(NOT_A_ZIPCODE);
            this.vendorZipcodeInput.getParent().requestFocus();
            return;
        }

        if (this.isEditing()) {
            // edits vendor
            this.currentVendor.getAddress().setZipCode(zipCode);
            this.currentVendor.getAddress().setCity(vendorCityInput.getText().trim());
            this.currentVendor.getAddress().setAddress(vendorAddressInput.getText().trim());
            this.entityManagerAddress.update(currentVendor.getAddress());

            this.updateVendor(currentVendor);
            this.entityManagerVendor.update(currentVendor);

            // updates vendor in the table
            this.addVendorToTreeTable(currentVendor);
            this.vendorsTreeTableView.getRoot().getChildren().remove(vendorsTreeTableView.getSelectionModel().getSelectedItem());
            this.vendorsTreeTableView.getSelectionModel().clearSelection();
        } else {
            // creates address
            Address newAddress = new Address(zipCode, vendorCityInput.getText().trim(), vendorAddressInput.getText().trim());
            this.entityManagerAddress.create(newAddress);

            // creation of a new vendor
            Vendor newVendor = new Vendor();
            newVendor.setAddress(newAddress);
            this.updateVendor(newVendor);
            this.entityManagerVendor.create(newVendor);

            // adds created user to the table
            this.addVendorToTreeTable(newVendor);
        }

        this.clearInformation();
    }

    /**
     * Handles the delete button click event
     */
    public void onDeleteButtonClick() {
        if (currentVendor == null) return;

        this.entityManagerVendor.delete(currentVendor);
        this.vendorsTreeTableView.getRoot().getChildren().remove(vendorsTreeTableView.getSelectionModel().getSelectedItem());
        this.vendorsTreeTableView.getSelectionModel().clearSelection();
        this.clearInformation();
    }

    /**
     * Loads selected vendors's information in the inputs
     */
    private void loadVendorInformation() {
        if (currentVendor == null) return;

        this.vendorNameInput.setText(currentVendor.getName());
        this.vendorAddressInput.setText(currentVendor.getAddress().getAddress());
        this.vendorCityInput.setText(currentVendor.getAddress().getCity());
        this.vendorZipcodeInput.setText(String.valueOf(currentVendor.getAddress().getZipCode()));
        this.vendorEmailInput.setText(currentVendor.getEmail());
        this.vendorPhoneInput.setText(currentVendor.getPhoneNumber());
        this.vendorContractCheckBox.setSelected(currentVendor.isContractState());
    }

    /**
     * Clears all the inputs and resets them to default
     */
    private void clearInformation() {
        this.errorMessage.setText("");
        this.vendorNameInput.setText("");
        this.vendorNameInput.setDisable(false);
        this.vendorAddressInput.setText("");
        this.vendorCityInput.setText("");
        this.vendorZipcodeInput.setText("");
        this.vendorEmailInput.setText("");
        this.vendorPhoneInput.setText("");
        this.vendorContractCheckBox.setSelected(false);
    }

    /**
     * Loads all of the vendors into the JFXTreeTableView
     */
    private void loadVendorsTreeTable() {
        JFXTreeTableColumn<VendorTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<VendorTree, String> city = new JFXTreeTableColumn<>("Ville");
        JFXTreeTableColumn<VendorTree, String> email = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<VendorTree, String> contractState = new JFXTreeTableColumn<>("Contract State");
        name.setSortNode(name.getSortNode());

        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        city.setCellValueFactory(param -> param.getValue().getValue().getCity());
        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        contractState.setCellValueFactory(param -> param.getValue().getValue().getContractState());

        ObservableList<VendorTree> vendors = FXCollections.observableArrayList();
        this.entityManagerVendor.getQueryForAll().forEach(vendor -> vendors.add(new VendorTree(
                vendor.getName(),
                vendor.getAddress().getCity(),
                vendor.getEmail(),
                vendor.isContractState()
        )));

        TreeItem<VendorTree> root = new RecursiveTreeItem<>(vendors, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.vendorsTreeTableView.getColumns().setAll(name, city, email, contractState);
        this.vendorsTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.vendorsTreeTableView.setRoot(root);
        this.vendorsTreeTableView.setShowRoot(false);
    }

    /**
     * Adds a vendor to the tree table with a vendor instance
     *
     * @param vendor a Vendor to add in the tree table
     */
    private void addVendorToTreeTable(Vendor vendor) {
        this.vendorsTreeTableView.getRoot().getChildren().add(new TreeItem<>(new VendorTree(
                vendor.getName(),
                vendor.getAddress().getCity(),
                vendor.getEmail(),
                vendor.isContractState()
        )));
    }

    /**
     * Updates the vendor object with all of the inputted values (after checks)
     *
     * @param vendor the Vendor to update
     */
    private void updateVendor(Vendor vendor) {
        vendor.setName(vendorNameInput.getText().trim());
        vendor.setEmail(vendorEmailInput.getText().trim());
        vendor.setPhoneNumber(vendorPhoneInput.getText().trim());
        vendor.setContractState(vendorContractCheckBox.isSelected());
    }

    /**
     * Returns if a vendor is selected/being edited
     *
     * @return if a vendor is being edited
     */
    private boolean isEditing() {
        return this.currentVendor != null;
    }
}
