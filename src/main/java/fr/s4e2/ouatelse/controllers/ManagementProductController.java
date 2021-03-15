package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.managers.EntityManagerVendor;
import fr.s4e2.ouatelse.objects.*;
import fr.s4e2.ouatelse.objects.Product.ProductPricesTree;
import fr.s4e2.ouatelse.objects.Product.ProductTree;
import fr.s4e2.ouatelse.objects.ProductStock.ProductStockInfoTree;
import fr.s4e2.ouatelse.utils.JFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementProductScreen}
 */
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
    private JFXTreeTableView<ProductTree> productsTreeView;

    // Error labels
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
    private JFXTreeTableView<ProductStockInfoTree> stockOrderTreeView;
    @FXML
    private JFXTreeTableView<ProductPricesTree> stockPricesTreeView;

    @FXML
    private Label informationErrorLabel;
    @FXML
    private JFXTextField productSearchBar;

    private Product currentProduct;
    private Store currentStore;

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
        this.initializeProductPricesTreeTable();
        this.initializeProductStockInfoTreeTable();

        // Fill the combo box with vendors (2nd tab)
        this.entityManagerVendor.getAll().forEachRemaining(vendor -> descriptionVendorComboBox.getItems().add(vendor));

        // Fill the combo boxes with stores (1st & 3rd tab)
        this.entityManagerStore.getAll().forEachRemaining(store -> {
            this.stockStoreComboBox.getItems().add(store);
            this.informationStoreComboBox.getItems().add(store);
        });

        // deselect an item in the product tree table
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;
            if (productsTreeView.getSelectionModel().getSelectedItem() == null) return;

            productsTreeView.getSelectionModel().clearSelection();
            currentProduct = null;
            currentStore = null;

            this.clearInformation();
        });

        // On the window's top sheet click, load the selected product
        this.productsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentProduct = null;
                return;
            }

            try {
                this.currentProduct = entityManagerProduct.executeQuery(entityManagerProduct.getQueryBuilder()
                        .where().eq("reference", newValue.getValue().getReference().getValue()).prepare())
                        .stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
            this.loadProductInformation();
        });

        // Handles the selection of a store
        this.stockStoreComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentStore = null;
                return;
            }
            if (!this.isSelected()) return;

            this.currentStore = newValue;
            this.loadProductStockInfo();
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
            return;
        }

        try {
            List<Product> searchResults = entityManagerProduct.executeQuery(
                    entityManagerProduct.getQueryBuilder().where().like("name", "%" + input + "%").prepare()
            );
            searchResults.forEach(this::addProductToTreeTable);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
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
        if (!this.isSelected()) {
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
        if (this.isSelected()) {
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
        if (!this.isSelected()) return;

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
        if (!this.isSelected()) {
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
     * Sets the prices related to the currently selected product
     */
    public void onConfirmPriceStockButton() {
        TreeItem<ProductPricesTree> pricesTreeTreeItem = stockPricesTreeView.getTreeItem(0);
        if (pricesTreeTreeItem == null) return;
        if (currentProduct == null) return;

        this.currentProduct.setMargin(pricesTreeTreeItem.getValue().getMargin().getValue());
        this.currentProduct.setTaxes(pricesTreeTreeItem.getValue().getTaxes().getValue());
        this.currentProduct.setPurchasePrice(pricesTreeTreeItem.getValue().getBuyingPrice().getValue());

        this.entityManagerProduct.update(currentProduct);
    }
    // ##########################################

    /**
     * Loads the list of products into the table
     */
    private void loadProductTreeTable() {
        JFXTreeTableColumn<ProductTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ProductTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<ProductTree, Double> sellingPrice = new JFXTreeTableColumn<>("Prix de vente");
        JFXTreeTableColumn<ProductTree, Double> purchasePrice = new JFXTreeTableColumn<>("Prix d'achat");
        JFXTreeTableColumn<ProductTree, String> brand = new JFXTreeTableColumn<>("Marque");
        JFXTreeTableColumn<ProductTree, String> state = new JFXTreeTableColumn<>("Etat");
        JFXTreeTableColumn<ProductTree, String> category = new JFXTreeTableColumn<>("Catégorie");
        JFXTreeTableColumn<ProductTree, String> soldByName = new JFXTreeTableColumn<>("Vendeur");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        sellingPrice.setCellValueFactory(param -> param.getValue().getValue().getSellingPrice().asObject());
        purchasePrice.setCellValueFactory(param -> param.getValue().getValue().getPurchasePrice().asObject());
        brand.setCellValueFactory(param -> param.getValue().getValue().getBrand());
        state.setCellValueFactory(param -> param.getValue().getValue().getState());
        category.setCellValueFactory(param -> param.getValue().getValue().getCategory());
        soldByName.setCellValueFactory(param -> param.getValue().getValue().getSoldByName());


        ObservableList<ProductTree> products = FXCollections.observableArrayList();
        this.entityManagerProduct.getQueryForAll().forEach(product -> products.add(product.toProductTree()));

        TreeItem<ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.productsTreeView.getColumns().setAll(reference, name, sellingPrice, purchasePrice, brand, state, category, soldByName);
        this.productsTreeView.getColumns().forEach(c -> c.setContextMenu(null));
        this.productsTreeView.setRoot(root);
        this.productsTreeView.setShowRoot(false);
    }

    /**
     * Sets up the table for product prices
     */
    private void initializeProductPricesTreeTable() {
        JFXTreeTableColumn<ProductPricesTree, Double> buyingPrice = new JFXTreeTableColumn<>("Prix achat");
        JFXTreeTableColumn<ProductPricesTree, Double> margin = new JFXTreeTableColumn<>("Marge HT");
        JFXTreeTableColumn<ProductPricesTree, Double> priceWithoutTaxes = new JFXTreeTableColumn<>("Prix HT");
        JFXTreeTableColumn<ProductPricesTree, Double> tax = new JFXTreeTableColumn<>("Taxe");
        JFXTreeTableColumn<ProductPricesTree, Double> sellingPrice = new JFXTreeTableColumn<>("Prix TTC");
        buyingPrice.setSortNode(buyingPrice.getSortNode());

        buyingPrice.setCellValueFactory(param -> param.getValue().getValue().getBuyingPrice().asObject());
        margin.setCellValueFactory(param -> param.getValue().getValue().getMargin().asObject());
        priceWithoutTaxes.setCellValueFactory(param -> param.getValue().getValue().getPriceWithoutTaxes().asObject());
        tax.setCellValueFactory(param -> param.getValue().getValue().getTaxes().asObject());
        sellingPrice.setCellValueFactory(param -> param.getValue().getValue().getPriceWithTaxes().asObject());

        StringConverter<Double> converter = JFXUtils.getDoubleConverter();

        buyingPrice.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(converter)); // allowed to edit
        margin.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(converter)); // allowed to edit
        tax.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(converter)); // allowed to edit

        buyingPrice.setOnEditCommit(event -> stockPricesTreeView.getTreeItem(event.getTreeTablePosition().getRow()).getValue().setBuyingPrice(event.getNewValue()));
        margin.setOnEditCommit(event -> stockPricesTreeView.getTreeItem(event.getTreeTablePosition().getRow()).getValue().setMargin(event.getNewValue()));
        tax.setOnEditCommit(event -> stockPricesTreeView.getTreeItem(event.getTreeTablePosition().getRow()).getValue().setTaxes(event.getNewValue()));

        //noinspection unchecked
        this.stockPricesTreeView.getColumns().setAll(buyingPrice, margin, priceWithoutTaxes, tax, sellingPrice);
        this.stockPricesTreeView.getColumns().forEach(c -> c.setContextMenu(null));
        this.stockPricesTreeView.setRoot(new RecursiveTreeItem<ProductPricesTree>(FXCollections.observableArrayList(), RecursiveTreeObject::getChildren));
        this.stockPricesTreeView.setShowRoot(false);
    }

    /**
     * Sets up the table for stock info
     */
    private void initializeProductStockInfoTreeTable() {
        JFXTreeTableColumn<ProductStockInfoTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ProductStockInfoTree, Integer> stock = new JFXTreeTableColumn<>("Stock");
        JFXTreeTableColumn<ProductStockInfoTree, String> order = new JFXTreeTableColumn<>("Commande");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        stock.setCellValueFactory(param -> param.getValue().getValue().getStockQuantity().asObject());
        order.setCellValueFactory(param -> param.getValue().getValue().getOrder());

        //noinspection unchecked
        this.stockOrderTreeView.getColumns().setAll(reference, stock, order);
        this.stockOrderTreeView.setRoot(new RecursiveTreeItem<ProductStockInfoTree>(FXCollections.observableArrayList(), RecursiveTreeObject::getChildren));
        this.stockOrderTreeView.getColumns().forEach(c -> c.setContextMenu(null));
        this.stockOrderTreeView.setShowRoot(false);
    }

    /**
     * Loads the stock info of the product into the table (tab 3)
     */
    private void loadProductStockInfo() {
        this.stockOrderTreeView.getRoot().getChildren().clear();

        if (currentStore != null) {
            ObservableList<TreeItem<ProductStockInfoTree>> productStocks = stockOrderTreeView.getRoot().getChildren();

            try {
                // loads all product stocks for selected store
                this.entityManagerProductStock.executeQuery(
                        entityManagerProductStock.getQueryBuilder().where().eq("store_id", currentStore.getId()).prepare()
                ).forEach(productStock -> {
                    if (productStock.getProduct() != null) {
                        productStocks.add(new TreeItem<>(productStock.toProductStockInfoTree()));
                    }
                });
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }
    }

    /**
     * Loads the product's information into the fields
     */
    private void loadProductInformation() {
        this.clearInformation();

        if (!this.isSelected()) return;

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

        this.stockPricesTreeView.getRoot().getChildren().add(new TreeItem<>(currentProduct.toProductPricesTree()));

    }

    /**
     * Adds a product to the sheet at the top of the window
     *
     * @param product The product to add to the sheet
     */
    private void addProductToTreeTable(Product product) {
        TreeItem<ProductTree> productRow = new TreeItem<>(product.toProductTree());

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
        this.stockPricesTreeView.getRoot().getChildren().clear();
        this.stockOrderTreeView.getRoot().getChildren().clear();
    }

    /**
     * Checks if a product is selected in the table at the top of the window
     *
     * @return true if a product is selected, else false
     */
    private boolean isSelected() {
        return this.currentProduct != null;
    }
}
