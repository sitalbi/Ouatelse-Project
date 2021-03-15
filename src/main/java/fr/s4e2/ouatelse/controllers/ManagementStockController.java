package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.stmt.QueryBuilder;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.ProductStock;
import fr.s4e2.ouatelse.objects.ProductStock.ProductStockTree;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementStockScreen}
 */
public class ManagementStockController extends BaseController {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();
    private final EntityManagerProductStock entityManagerProductStock = Main.getDatabaseManager().getEntityManagerProductStock();
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();

    @FXML
    private JFXTextField stockSearchBar;
    @FXML
    private JFXTreeTableView<ProductStockTree> stockTreeTableView;
    @FXML
    private JFXComboBox<Store> stockStoreComboBox;
    @FXML
    private JFXTextField stockCurrentQuantityInput;
    @FXML
    private JFXTextField stockWorthInput;
    @FXML
    private JFXTextField stockSellingWorthInput;
    @FXML
    private JFXTextField stockQuantityInput;

    private ProductStock currentStock;
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
        this.loadProductStockTreeTable();
        this.entityManagerStore.getQueryForAll().forEach(store -> stockStoreComboBox.getItems().add(store));

        // deselect an item in the stock tree table
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;
            if (stockTreeTableView.getSelectionModel().getSelectedItem() == null) return;

            stockTreeTableView.getSelectionModel().clearSelection();
            currentStock = null;
            this.clearInformation();
        });

        // Handles the selection of a stock
        this.stockTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentStock = null;
                return;
            }

            try {
                this.currentStock = entityManagerProductStock.executeQuery(entityManagerProductStock.getQueryBuilder()
                        .where().eq("id", newValue.getValue().getId().getValue()).prepare())
                        .stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
            this.loadInformation();

        });

        // Handles the selection of a store
        this.stockStoreComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentStore = null;
                return;
            }

            this.currentStore = newValue;
            this.loadProductStockTreeTable();
        });

        this.stockSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchProductFromText(newValue.trim()));
    }

    /**
     * Handles the button click event for the schedule order button
     * <p>
     * Prepares an order for a selected product
     */
    public void onScheduleOrderButtonClick() {
        if (!this.isStockEditing()) return;

        // todo : implement this
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Handles the button click event for the delete button
     * <p>
     * Deletes the stock of a product
     */
    public void onDeleteStockButtonClick() {
        if (!this.isStockEditing()) return;

        this.currentStock.setQuantity(0);
        this.entityManagerProductStock.update(currentStock);
        this.stockQuantityInput.setText("");
        this.addProductStockToTreeTable(currentStock);
    }

    /**
     * Handles the button click event for the add button
     * <p>
     * Adds to the stock of a product
     */
    public void onAddStockButtonClick() {
        if (!this.isStockEditing()) return;

        Integer quantity = Utils.getNumber(stockQuantityInput.getText().trim());
        if (quantity == null) return;

        this.currentStock.setQuantity(currentStock.getQuantity() + quantity);
        this.entityManagerProductStock.update(currentStock);
        this.stockQuantityInput.setText("");
        this.addProductStockToTreeTable(currentStock);
    }

    /**
     * Clears the product stocks' information from the differents fields
     */
    private void clearInformation() {
        this.stockCurrentQuantityInput.setText("");
        this.stockWorthInput.setText("");
        this.stockSellingWorthInput.setText("");
        this.stockQuantityInput.setText("");
    }

    /**
     * Loads the product stock's information
     */
    private void loadInformation() {
        if (!this.isStockEditing()) return;

        this.stockCurrentQuantityInput.setText(String.valueOf(currentStock.getQuantity()));
        this.stockWorthInput.setText(String.valueOf(currentStock.getProduct().getPurchasePrice() * currentStock.getQuantity()));
        this.stockSellingWorthInput.setText(String.valueOf(currentStock.getProduct().getSellingPrice() * currentStock.getQuantity()));
    }

    /**
     * Searches a product stock in the database from its name
     *
     * @param input the searched product name
     */
    private void searchProductFromText(String input) {
        if (!this.isStoreSelected()) return;

        this.stockTreeTableView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.loadProductStockTreeTable();
            return;
        }

        try {
            QueryBuilder<Product, Long> productQueryBuilder = entityManagerProduct.getQueryBuilder();
            productQueryBuilder.where().like("name", "%" + input + "%");

            List<ProductStock> searchResults = entityManagerProductStock.executeQuery(
                    entityManagerProductStock.getQueryBuilder()
                            .join(productQueryBuilder)
                            .where()
                            .eq("store_id", this.currentStore.getId())
                            .prepare()
            );
            searchResults.forEach(this::addProductStockToTreeTable);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Loads the list of product stocks into the table
     */
    private void loadProductStockTreeTable() {
        JFXTreeTableColumn<ProductStockTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ProductStockTree, String> product = new JFXTreeTableColumn<>("Product");
        JFXTreeTableColumn<ProductStockTree, Double> unitValue = new JFXTreeTableColumn<>("Val. Unitaire");
        JFXTreeTableColumn<ProductStockTree, Integer> stockQuantity = new JFXTreeTableColumn<>("Inventaire");
        JFXTreeTableColumn<ProductStockTree, String> state = new JFXTreeTableColumn<>("État");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        product.setCellValueFactory(param -> param.getValue().getValue().getArticle());
        unitValue.setCellValueFactory(param -> param.getValue().getValue().getUnitValue().asObject());
        stockQuantity.setCellValueFactory(param -> param.getValue().getValue().getStockQuantity().asObject());
        state.setCellValueFactory(param -> param.getValue().getValue().getProductState());

        ObservableList<ProductStockTree> productStocks = FXCollections.observableArrayList();

        if (this.isStoreSelected()) {
            try {
                // loads all product stocks for selected store
                this.entityManagerProductStock.executeQuery(
                        entityManagerProductStock.getQueryBuilder().where().eq("store_id", currentStore.getId()).prepare()
                ).forEach(productStock -> {
                    if (productStock.getProduct() != null) {
                        productStocks.add(productStock.toProductStockTree());
                    }
                });
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }

        TreeItem<ProductStockTree> root = new RecursiveTreeItem<>(productStocks, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.stockTreeTableView.getColumns().setAll(reference, product, unitValue, stockQuantity, state);
        this.stockTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.stockTreeTableView.setRoot(root);
        this.stockTreeTableView.setShowRoot(false);
    }

    /**
     * Adds a product stock to the table
     *
     * @param productStock The product stock to add to the table
     */
    private void addProductStockToTreeTable(ProductStock productStock) {
        TreeItem<ProductStockTree> productStockRow = new TreeItem<>(productStock.toProductStockTree());

        this.stockTreeTableView.getRoot().getChildren().remove(stockTreeTableView.getSelectionModel().getSelectedItem());
        this.stockTreeTableView.getRoot().getChildren().add(productStockRow);
        this.stockTreeTableView.getSelectionModel().select(productStockRow);
        this.loadInformation();
    }

    /**
     * Checks if a product stock is selected in the table at the top of the window
     *
     * @return true if a product stock is selected, else false
     */
    private boolean isStockEditing() {
        return this.currentStock != null;
    }

    /**
     * Checks if a store is selected in the combo box
     *
     * @return true if a storeis selected, else false
     */
    private boolean isStoreSelected() {
        return this.currentStore != null;
    }
}
