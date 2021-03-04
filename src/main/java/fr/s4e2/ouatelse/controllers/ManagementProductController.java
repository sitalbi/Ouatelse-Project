package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.managers.EntityManagerVendor;
import fr.s4e2.ouatelse.objects.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagementProductController extends BaseController {

    // Error messages
    private static final String PRODUCT_ALREADY_EXISTS = "Ce produit existe déjà!";
    private static final String NOT_ALL_FIELDS_FILLED = "Informations sur le produit inexistantes ou manquantes, création de celui-ci impossible";
    private static final String CURRENT_USER_NOT_SET = "Veuillez sélectionner un produit";
    private static final String REFERENCE_NOT_NUMBERS = "La référence doit être un nombre";

    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    private final EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();
    private final EntityManagerVendor entityManagerVendor = Main.getDatabaseManager().getEntityManagerVendor();
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    // top
    @FXML
    private JFXTreeTableView<Product.ProductTree> productsTreeView;

    // Error labels
    @FXML
    private Label stockErrorLabel;
    @FXML
    private Label descriptionErrorLabel;

    // Information Tab
    @FXML
    private JFXTextField informationNameInput;
    @FXML
    private JFXTextField informationReferenceInput;
    @FXML
    private JFXTextField informationBarcodeInput;
    @FXML
    private JFXCheckBox informationStockCheckBox;

    // Description Tab
    @FXML
    private JFXTextField descriptionCategoryInput;
    @FXML
    private JFXTextField descriptionBrandInput;
    @FXML
    private ComboBox<Vendor> descriptionVendorComboBox;

    // Prices & Stock Tab
    @FXML
    private ComboBox<Store> stockStoreComboBox;
    @FXML
    private JFXTreeTableView<ProductStock.ProductStockTreeTop> stockOrderTreeView;
    @FXML
    private JFXTreeTableView<ProductStock.ProductStockTreeBottom> stockPricesTreeView;

    @FXML
    private Button confirmPriceStockButton;
    @FXML
    private Label informationErrorLabel;
    private Product currentProduct;
    @FXML
    private JFXTextField productSearchBar;

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
        this.loadProductTreeTable();

        // Fill the combo box of the second tab
        this.entityManagerVendor.getAll().forEachRemaining(vendor -> this.descriptionVendorComboBox.getItems().add(vendor));
        // Fill the combo box of the third tab
        this.entityManagerStore.getAll().forEachRemaining(store -> this.stockStoreComboBox.getItems().add(store));

        // On the window's top sheet click, load the selected product
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

        this.productSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchProductFromText(newValue.trim()));
    }

    /**
     * Searches a product in the database from its name
     *
     * @param input the searched product name
     */
    private void searchProductFromText(String input) {
        this.productsTreeView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.loadProductTreeTable();
        } else {
            try {
                List<Product> searchResults = this.entityManagerProduct.executeQuery(this.entityManagerProduct.getQueryBuilder().where().like("name", "%" + input + "%").prepare());
                searchResults.forEach(this::addProductToTreeTable);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }
    }


    // First tab ################################

    /**
     * Creates a product from the informations of the first tab
     *
     * @throws SQLException All errors related to database scheme, shouldn't happen
     */
    public void onConfirmInformationButton() throws SQLException {
        this.informationErrorLabel.setText("");

        // Check if all fields are filled
        if (informationNameInput.getText().trim().isEmpty()
                || informationReferenceInput.getText().trim().isEmpty()
                || informationBarcodeInput.getText().trim().isEmpty()
        ) {
            this.informationErrorLabel.setText(NOT_ALL_FIELDS_FILLED);
            return;
        }

        this.informationErrorLabel.setText("");

        Pattern referenceCheckerPattern = Pattern.compile("[0-9]+");
        Matcher referenceMatcher = referenceCheckerPattern.matcher(this.informationReferenceInput.getText().trim());

        if (!referenceMatcher.matches()) {
            this.informationErrorLabel.setText(REFERENCE_NOT_NUMBERS);
            return;
        }

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
            this.currentProduct.setState(this.informationStockCheckBox.isSelected() ? ProductState.OUT_OF_STOCK : ProductState.IN_STOCK);

            this.entityManagerProduct.update(currentProduct);

            this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
        } else {
            // Creation of a new product
            Product newProduct = new Product();

            newProduct.setName(informationNameInput.getText().trim());
            newProduct.setReference(Long.parseLong(informationReferenceInput.getText().trim()));
            newProduct.setBarCode(informationBarcodeInput.getText().trim());
            newProduct.setState(this.informationStockCheckBox.isSelected() ? ProductState.OUT_OF_STOCK : ProductState.IN_STOCK);

            newProduct.setBrand("");
            newProduct.setCategory("");

            newProduct.setPurchasePrice(0);

            this.entityManagerProduct.create(newProduct);
            this.currentProduct = newProduct;
        }
        // Updates product in the table
        this.addProductToTreeTable(this.currentProduct);
    }

    /**
     * Delete the currently selected product
     */
    public void onDeleteProductButton() {
        if (!this.isEditing()) return;

        this.entityManagerProduct.delete(this.currentProduct);
        this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
        this.productsTreeView.getSelectionModel().clearSelection();
        this.clearInformations();
    }
    // ##########################################

    // Second tab ###############################

    /**
     * Sets the currently selected product's informations from the second tab
     */
    public void onConfirmDescriptionButton() {
        if (!this.isEditing()) {
            this.descriptionErrorLabel.setText(CURRENT_USER_NOT_SET);
            return;
        }
        this.descriptionErrorLabel.setText("");

        this.currentProduct.setBrand(this.descriptionBrandInput.getText().trim());
        this.currentProduct.setCategory(this.descriptionCategoryInput.getText().trim());
        this.currentProduct.setSoldBy(this.descriptionVendorComboBox.getValue());

        this.entityManagerProduct.update(this.currentProduct);

        this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
        this.addProductToTreeTable(this.currentProduct);
    }
    // ##########################################


    // Third tab ################################

    /**
     * Sets the stocks related to the currently selected product
     */
    public void onConfirmPriceStockButton() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    // ##########################################

    /**
     * Loads the list of products into the top sheet
     */
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
        state.setCellValueFactory(param -> param.getValue().getValue().getState());
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
                product.getMargin(),
                product.getPurchasePrice(),
                product.getBrand(),
                product.getState().toString(),
                product.getCategory(),
                (product.getSoldBy() != null) ? product.getSoldBy().getName() : "",
                product.getTaxes()
        )));

        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.productsTreeView.getColumns().setAll(reference, name, sellingPrice, purchasePrice, brand, state, category, soldByName);
        this.productsTreeView.setRoot(root);
        this.productsTreeView.setShowRoot(false);
    }

    /**
     * Loads the product's informations into the fields
     */
    private void loadProductInformation() {
        this.clearInformations();

        if (!this.isEditing()) return;

        this.informationNameInput.setText(this.currentProduct.getName());
        this.informationReferenceInput.setText(Long.toString(this.currentProduct.getReference()));
        this.informationBarcodeInput.setText(this.currentProduct.getBarCode());
        this.informationStockCheckBox.setSelected(this.currentProduct.getState() == ProductState.OUT_OF_STOCK);

        this.descriptionCategoryInput.setText(this.currentProduct.getCategory());
        this.descriptionBrandInput.setText(this.currentProduct.getBrand());
        this.descriptionVendorComboBox.getItems().stream()
                .filter(vendor -> vendor.getId() == ((this.currentProduct.getSoldBy() != null) ? this.currentProduct.getSoldBy().getId() : -1))
                .findFirst()
                .ifPresent(vendor -> descriptionVendorComboBox.getSelectionModel().select(vendor));

        // TODO : Fill the third tab's fields
    }

    /**
     * Adds a product to the sheet at the top of the window
     *
     * @param product The product to add to the sheet
     */
    private void addProductToTreeTable(Product product) {
        TreeItem<Product.ProductTree> productRow = new TreeItem<>(new Product.ProductTree(
                product.getReference(),
                product.getName(),
                product.getMargin(),
                product.getPurchasePrice(),
                product.getBrand(),
                product.getState().toString(),
                product.getCategory(),
                (product.getSoldBy() == null) ? "" : product.getSoldBy().getName(),
                product.getTaxes()
        ));

        this.productsTreeView.getRoot().getChildren().add(productRow);
        this.productsTreeView.getSelectionModel().select(productRow);
    }


    /**
     * Clears the product's informations from the differents fields for all tabs
     */
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
        // TODO : Finish the third tab
    }

    /**
     * Check if a product is selected in the sheet at the top of the window
     *
     * @return true if a product is selected, else false
     */
    private boolean isEditing() {
        return this.currentProduct != null;
    }
}
