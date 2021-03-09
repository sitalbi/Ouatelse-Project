package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
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
import javafx.scene.input.KeyCode;

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
    private final EntityManagerProductStock entityManagerProductStock = Main.getDatabaseManager().getEntityManagerProductStock();
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
    private JFXComboBox<Store> informationStoreComboBox;
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

        // Fill the combo box with vendors (2nd tab)
        this.entityManagerVendor.getAll().forEachRemaining(vendor -> descriptionVendorComboBox.getItems().add(vendor));

        // Fill the combo boxes with stores (1st & 3rd tab)
        this.entityManagerStore.getAll().forEachRemaining(store -> {
            this.stockStoreComboBox.getItems().add(store);
            this.informationStoreComboBox.getItems().add(store);
        });

        // deselect an item in the product tree table
        this.productsTreeView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            TreeItem<Product.ProductTree> product = productsTreeView.getSelectionModel().getSelectedItem();
            if (product == null) return;
            productsTreeView.getSelectionModel().clearSelection();
            currentProduct = null;

            this.clearInformation();
        });

        // On the window's top sheet click, load the selected product
        this.productsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    this.currentProduct = entityManagerProduct.executeQuery(entityManagerProduct.getQueryBuilder()
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
                List<Product> searchResults = entityManagerProduct.executeQuery(
                        entityManagerProduct.getQueryBuilder().where().like("name", "%" + input + "%").prepare()
                );
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
        // Check if all fields are filled
        if (informationNameInput.getText().trim().isEmpty() || informationReferenceInput.getText().trim().isEmpty()
                || informationBarcodeInput.getText().trim().isEmpty() || informationStoreComboBox.getSelectionModel().isEmpty()) {
            this.informationErrorLabel.setText(NOT_ALL_FIELDS_FILLED);
            return;
        }

        this.informationErrorLabel.setText("");

        Pattern referenceCheckerPattern = Pattern.compile("[0-9]+");
        Matcher referenceMatcher = referenceCheckerPattern.matcher(informationReferenceInput.getText().trim());

        if (!referenceMatcher.matches()) {
            this.informationErrorLabel.setText(REFERENCE_NOT_NUMBERS);
            return;
        }

        // If product already exists
        if (!this.isEditing()) {
            Product product = entityManagerProduct.executeQuery(
                    entityManagerProduct.getQueryBuilder()
                            .where().eq("reference", informationReferenceInput.getText().trim())
                            .prepare()
            ).stream().findFirst().orElse(null);
            if (product != null) {
                this.informationErrorLabel.setText(PRODUCT_ALREADY_EXISTS);
                this.informationReferenceInput.getParent().requestFocus();
                return;
            }
        }

        Store selectedStore = informationStoreComboBox.getSelectionModel().getSelectedItem();
        if (this.isEditing()) {
            // Edits product
            this.currentProduct.setName(informationNameInput.getText().trim());
            this.currentProduct.setReference(Long.parseLong(informationReferenceInput.getText().trim()));
            this.currentProduct.setBarCode(informationBarcodeInput.getText().trim());
            this.currentProduct.setState(informationStockCheckBox.isSelected() ? ProductState.OUT_OF_STOCK : ProductState.IN_STOCK);
            this.currentProduct.setStore(selectedStore);

            // update product
            this.entityManagerProduct.update(this.currentProduct);
        } else {
            // Creation of a new product
            Product newProduct = new Product();

            newProduct.setName(informationNameInput.getText().trim());
            newProduct.setReference(Long.parseLong(informationReferenceInput.getText().trim()));
            newProduct.setBarCode(informationBarcodeInput.getText().trim());
            newProduct.setState(informationStockCheckBox.isSelected() ? ProductState.OUT_OF_STOCK : ProductState.IN_STOCK);
            newProduct.setStore(selectedStore);
            newProduct.setBrand("");
            newProduct.setCategory("");
            newProduct.setPurchasePrice(0);

            // update product
            this.entityManagerProduct.create(newProduct);
            this.currentProduct = newProduct;
        }
        // creates a product stock
        ProductStock productStock = new ProductStock();
        productStock.setProduct(this.currentProduct);
        productStock.setStore(selectedStore);
        this.entityManagerProductStock.create(productStock);

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
        this.clearInformation();
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
     * Loads the list of products into the table
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
     * Loads the product's information into the fields
     */
    private void loadProductInformation() {
        this.clearInformation();

        if (!this.isEditing()) return;

        this.informationNameInput.setText(this.currentProduct.getName());
        this.informationReferenceInput.setText(Long.toString(this.currentProduct.getReference()));
        this.informationBarcodeInput.setText(this.currentProduct.getBarCode());
        this.informationStockCheckBox.setSelected(this.currentProduct.getState() == ProductState.OUT_OF_STOCK);
        this.informationStoreComboBox.getItems().stream()
                .filter(store -> store.getId().equals(this.currentProduct.getStore() != null ? this.currentProduct.getStore().getId() : ""))
                .findFirst()
                .ifPresent(store -> informationStoreComboBox.getSelectionModel().select(store));

        this.descriptionCategoryInput.setText(this.currentProduct.getCategory());
        this.descriptionBrandInput.setText(this.currentProduct.getBrand());
        this.descriptionVendorComboBox.getItems().stream()
                .filter(vendor -> vendor.getId() == (this.currentProduct.getSoldBy() != null ? this.currentProduct.getSoldBy().getId() : -1))
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

        this.productsTreeView.getRoot().getChildren().remove(productsTreeView.getSelectionModel().getSelectedItem());
        this.productsTreeView.getRoot().getChildren().add(productRow);
        this.productsTreeView.getSelectionModel().select(productRow);
        this.currentProduct = product;
    }

    /**
     * Clears the products' information from the differents fields for all tabs
     */
    private void clearInformation() {
        // First tab fields
        this.informationNameInput.setText("");
        this.informationReferenceInput.setText("");
        this.informationBarcodeInput.setText("");
        this.informationStockCheckBox.setSelected(false);
        this.informationStoreComboBox.getSelectionModel().select(-1);

        // Second tabs fields
        this.descriptionCategoryInput.setText("");
        this.descriptionBrandInput.setText("");
        this.descriptionVendorComboBox.getSelectionModel().select(-1);

        // Third tab fields
        this.stockStoreComboBox.getSelectionModel().select(-1);
        // TODO : Finish the third tab
    }

    /**
     * Checks if a product is selected in the table at the top of the window
     *
     * @return true if a product is selected, else false
     */
    private boolean isEditing() {
        return this.currentProduct != null;
    }
}
