package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.ProductState;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.Vendor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManagementProductController extends BaseController {

    // Error messages
    private static final String PRODUCT_ALREADY_EXISTS = "Ce produit existe déjà!";
    private static final String NOT_ALL_FIELDS_FILLED = "Informations sur le produit inexistantes ou manquantes, création de celui-ci impossible";

    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    private final EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();
    // top
    public JFXTreeTableView<Product.ProductTree> productsTreeView;
    public JFXTreeTableView stockOrderTreeView;

    // Error labels
    public Label stockErrorLabel;
    public Label descriptionErrorLabel;
    public Label informationErrorLabel;

    // Information Tab
    @FXML
    private JFXTextField informationNameInput;
    @FXML
    private JFXTextField informationReferenceInput;
    @FXML
    private JFXTextField informationBarcodeInput;
    @FXML
    private JFXCheckBox informationStockCheckBox;
    @FXML
    private Button confirmInformationButton;
    @FXML
    private Button deleteProductButton;
    // Description Tab
    @FXML
    private JFXTextField descriptionCategoryInput;
    @FXML
    private JFXTextField descriptionBrandInput;
    @FXML
    private ComboBox<Vendor> descriptionVendorComboBox;
    @FXML
    private Button confirmDescriptionButton;
    // Prices & Stock Tab
    @FXML
    private ComboBox<Store> stockStoreComboBox;

    @FXML
    private Button confirmPriceStockButton;
    public JFXTreeTableView stockPricesTreeView; // todo : create another Tree
    private Product currentProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        this.loadProductTreeTable();

        this.entityManagerStore.getAll().forEachRemaining(store -> this.stockStoreComboBox.getItems().add(store));

        this.productsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    this.currentProduct = this.entityManagerProduct.executeQuery(this.entityManagerProduct.getQueryBuilder()
                            .where().eq("reference", newValue.getValue().getReference().getValue())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                this.loadProductInformation();
            } else {
                this.currentProduct = null;
            }
        });
    }

    // First tab ################################
    public void onConfirmInformationButton() throws SQLException {
        this.informationErrorLabel.setText("");

        // Check if all fields are filled
        if (informationNameInput.getText().trim().isEmpty()
                || informationReferenceInput.getText().trim().isEmpty()
                || informationBarcodeInput.getText().trim().isEmpty()
                || descriptionBrandInput.getText().trim().isEmpty()
                || descriptionVendorComboBox.getValue() == null
                || descriptionCategoryInput.getText().isEmpty()
        ) {
            this.informationErrorLabel.setText(NOT_ALL_FIELDS_FILLED);
            return;
        }

        // TODO : Check types

        // If product already exists
        if (!this.isEditing()) {
            Product product = this.entityManagerProduct.executeQuery(
                    this.entityManagerProduct.getQueryBuilder()
                            .where().eq("reference", informationReferenceInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (product != null) {
                this.informationErrorLabel.setText(PRODUCT_ALREADY_EXISTS);
                this.informationReferenceInput.getParent().requestFocus();
                return;
            }
        }

        if (this.isEditing()) {
            // Edits product
            this.currentProduct.setName(informationNameInput.getText().trim());
            this.currentProduct.setReference(Long.parseLong(informationReferenceInput.getText().trim()));
            this.currentProduct.setBarCode(informationBarcodeInput.getText().trim());

            this.entityManagerProduct.update(currentProduct);

            // Updates product in the table
            this.addProductToTreeTable(this.currentProduct);
            this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
            this.productsTreeView.getSelectionModel().clearSelection();

        } else {
            // Creation of a new product
            Product newProduct = new Product();

            newProduct.setName(informationNameInput.getText().trim());
            newProduct.setReference(Long.parseLong(informationReferenceInput.getText().trim()));
            newProduct.setBarCode(informationBarcodeInput.getText().trim());
            newProduct.setState(this.informationStockCheckBox.isSelected() ? ProductState.OUT_OF_STOCK : ProductState.IN_STOCK);

            newProduct.setBrand(this.descriptionBrandInput.getText().trim());
            newProduct.setCategory(this.descriptionCategoryInput.getText().trim());

            newProduct.setSoldBy(this.descriptionVendorComboBox.getValue());

            newProduct.setSellingPrice(0);
            newProduct.setPurchasePrice(0);

            // Adds created product to the table
            this.addProductToTreeTable(newProduct);
        }
        this.clearInformations();
    }

    public void onDeleteProductButton() {
        if (this.currentProduct == null) return;

        this.entityManagerProduct.delete(this.currentProduct);
        this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
        this.productsTreeView.getSelectionModel().clearSelection();
        this.clearInformations();
    }
    // ##########################################

    // Second tab ###############################
    public void onConfirmDescriptionButton() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    // ##########################################

    // Third tab ################################
    public void onConfirmPriceStockButton() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    // ##########################################

    private void loadProductTreeTable() {
        JFXTreeTableColumn<Product.ProductTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<Product.ProductTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Product.ProductTree, Double> sellingPrice = new JFXTreeTableColumn<>("Prix de vente");
        JFXTreeTableColumn<Product.ProductTree, Double> purchasePrice = new JFXTreeTableColumn<>("Prix d'achat");
        JFXTreeTableColumn<Product.ProductTree, String> brand = new JFXTreeTableColumn<>("Marque");
        JFXTreeTableColumn<Product.ProductTree, String> state = new JFXTreeTableColumn<>("Etat");
        JFXTreeTableColumn<Product.ProductTree, String> category = new JFXTreeTableColumn<>("Catégorie");
        JFXTreeTableColumn<Product.ProductTree, String> soldByName = new JFXTreeTableColumn<>("Vendeur");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        sellingPrice.setCellValueFactory(param -> param.getValue().getValue().getSellingPrice().asObject());
        purchasePrice.setCellValueFactory(param -> param.getValue().getValue().getPurchasePrice().asObject());
        brand.setCellValueFactory(param -> param.getValue().getValue().getBrand());
        state.setCellValueFactory(param -> param.getValue().getValue().getBrand());
        category.setCellValueFactory(param -> param.getValue().getValue().getCategory());
        soldByName.setCellValueFactory(param -> param.getValue().getValue().getSoldByName());

        reference.setContextMenu(null);
        name.setContextMenu(null);
        sellingPrice.setContextMenu(null);
        purchasePrice.setContextMenu(null);
        brand.setContextMenu(null);
        state.setContextMenu(null);
        category.setContextMenu(null);
        soldByName.setContextMenu(null);

        ObservableList<Product.ProductTree> products = FXCollections.observableArrayList();
        this.entityManagerProduct.getQueryForAll().forEach(product -> products.add(new Product.ProductTree(
                product.getReference(),
                product.getName(),
                product.getSellingPrice(),
                product.getPurchasePrice(),
                product.getBrand(),
                product.getState().toString(),
                product.getCategory(),
                product.getSoldBy().getName()
        )));

        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.productsTreeView.getColumns().setAll(reference, name, sellingPrice, purchasePrice, brand, state, category, soldByName);
        this.productsTreeView.setRoot(root);
        this.productsTreeView.setShowRoot(false);
    }

    private void loadProductInformation() {
        this.clearInformations();

        if (this.currentProduct == null) return;

        this.informationNameInput.setText(this.currentProduct.getName());
        this.informationReferenceInput.setText(Long.toString(this.currentProduct.getReference()));
        this.informationBarcodeInput.setText(this.currentProduct.getBarCode());
        this.informationStockCheckBox.setSelected(this.currentProduct.getState() == ProductState.OUT_OF_STOCK);

        this.descriptionCategoryInput.setText(this.currentProduct.getCategory());
        this.descriptionBrandInput.setText(this.currentProduct.getBrand());
        this.descriptionVendorComboBox.getItems().stream()
                .filter(vendor -> vendor.getName().equals(this.currentProduct.getSoldBy().getName()))
                .findFirst()
                .ifPresent(vendor -> descriptionVendorComboBox.getSelectionModel().select(vendor));
    }

    private void addProductToTreeTable(Product product) {
        this.productsTreeView.getRoot().getChildren().add(new TreeItem<>(new Product.ProductTree(
                product.getReference(),
                product.getName(),
                product.getSellingPrice(),
                product.getPurchasePrice(),
                product.getBrand(),
                product.getState().toString(),
                product.getCategory(),
                product.getSoldBy().getName()
        )));
    }

    // TODO : Finish the last table's fields
    private void clearInformations() {
        // First tab fields
        this.informationNameInput.setText("");
        this.informationReferenceInput.setText("");
        this.informationBarcodeInput.setText("");
        this.informationStockCheckBox.setSelected(false);

        // Second tabs fields
        this.descriptionCategoryInput.setText("");
        this.descriptionBrandInput.setText("");
        this.descriptionVendorComboBox.getSelectionModel().select(-1);

        // Third tab fields
        this.stockStoreComboBox.getSelectionModel().select(-1);
    }

    private boolean isEditing() {
        return this.currentProduct != null;
    }
}
