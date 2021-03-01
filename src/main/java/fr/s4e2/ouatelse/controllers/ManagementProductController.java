package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.Vendor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManagementProductController extends BaseController {

    // Error messages
    private final String PRODUCT_ALREADY_EXISTS = "Ce produit existe déjà!";
    private final String NOT_ALL_FIELDS_FILLED = "Informations sur le produit inexistantes ou manquantes, création de celui-ci impossible";
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    // top
    public JFXTreeTableView<Product.ProductTree> productsTreeView; // todo : create ProductTree
    public JFXTreeTableView stockOrderTreeView;
    // Information Tab
    @FXML
    private JFXTextField informationNameInput;
    @FXML
    private JFXTextField informationReferenceInput;
    @FXML
    private JFXTextField informationBarcodeInput;
    @FXML
    private JFXTextField informationCommentInput;
    @FXML
    private JFXCheckBox informationStockCheckBox;
    @FXML
    private Button confirmInformationButton;
    @FXML
    private Button deleteProductButton;
    // Description Tab
    @FXML
    private JFXTextField descriptionTypeInput;
    @FXML
    private JFXTextField descriptionCategoryInput;
    @FXML
    private JFXTextField descriptionBrandInput;
    @FXML
    private JFXTextField descriptionModelInput;
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
    }

    // First tab ################################
    public void onConfirmInformationButton() throws SQLException {
        // TODO : Add an error message
        if (informationNameInput.getText().trim().isEmpty()
                || informationReferenceInput.getText().trim().isEmpty()
                || informationBarcodeInput.getText().trim().isEmpty()
                || informationCommentInput.getText().trim().isEmpty()
        ) return;

        // TODO : Check types

        // user already exists!
        if (!this.isEditing()) {
            Product product = this.entityManagerProduct.executeQuery(
                    this.entityManagerProduct.getQueryBuilder()
                            .where().eq("reference", informationReferenceInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (product != null) {
                // TODO : this.errorMessage.setText(PRODUCT_ALREADY_EXISTS);
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

            // Adds created product to the table
            this.addProductToTreeTable(newProduct);
        }
        this.clearInformation();
    }

    public void onDeleteProductButton() {
    }
    // ##########################################

    // Second tab ###############################
    public void onConfirmDescriptionButton() {
    }
    // ##########################################

    // Third tab ################################
    public void onConfirmPriceStockButton() {
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

    // TODO : this
    private void clearInformation() {

    }

    private boolean isEditing() {
        return this.currentProduct != null;
    }
}
