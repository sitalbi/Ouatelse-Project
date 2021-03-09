package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.objects.ProductStock;
import fr.s4e2.ouatelse.objects.ProductStock.ProductStockTree;
import fr.s4e2.ouatelse.objects.Store;
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

public class ManagementStockController extends BaseController {

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
    private JFXTextField stockQuantityInput;

    private ProductStock currentStock;
    private Store currentStore;

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();
    private final EntityManagerProductStock entityManagerProductStock = Main.getDatabaseManager().getEntityManagerProductStock();

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
        this.stockTreeTableView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            TreeItem<ProductStockTree> stock = stockTreeTableView.getSelectionModel().getSelectedItem();
            if (stock == null) return;
            stockTreeTableView.getSelectionModel().clearSelection();
            currentStock = null;

            this.clearInformation();
        });

        // Handles the selection of a stock
        this.stockTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    this.currentStock = entityManagerProductStock.executeQuery(entityManagerProductStock.getQueryBuilder()
                            .where().eq("id", newValue.getValue().getReference().getValue())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                this.loadProductStockInformation();
            } else {
                this.currentStock = null;
            }
        });

        // Handles the slection of a store
        this.stockStoreComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.currentStore = newValue;
                this.loadProductStockTreeTable();
            } else {
                this.currentStock = null;
            }

        });

        this.stockSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchProductFromText(newValue.trim()));
    }

    /**
     * Handles the button click event for the schedule order button
     * <p>
     * Prepares an order for a selected product
     */
    public void onScheduleOrderButtonClick() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Handles the button click event for the delete button
     * <p>
     * Deletes the stock of a product
     */
    public void onDeleteStockButtonClick() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Handles the button click event for the add button
     * <p>
     * Adds to the stock of a product
     */
    public void onAddStockButtonClick() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Clears the product stocks' information from the differents fields
     */
    private void clearInformation() {
        this.stockQuantityInput.setText("");
    }

    /**
     * Searches a product stock in the database from its name
     *
     * @param input the searched product name
     */
    private void searchProductFromText(String input) {
        this.stockTreeTableView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.loadProductStockTreeTable();
        } else {
            try {
                List<ProductStock> searchResults = entityManagerProductStock.executeQuery(
                        entityManagerProductStock.getQueryBuilder().where().like("product.name", "%" + input + "%").prepare()
                );
                searchResults.forEach(this::addProductStockToTreeTable);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }
    }

    /**
     * Loads the product stock information into the fields
     */
    private void loadProductStockInformation() {
        this.clearInformation();

        if (!this.isEditing()) return;

        this.stockQuantityInput.setText(String.valueOf(currentStock.getQuantity()));
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

        reference.setContextMenu(null);
        product.setContextMenu(null);
        unitValue.setContextMenu(null);
        stockQuantity.setContextMenu(null);
        state.setContextMenu(null);

        ObservableList<ProductStockTree> productStocks = FXCollections.observableArrayList();

        if (currentStore != null) {
            try {
                // loads all product stocks for selected store
                this.entityManagerProductStock.executeQuery(
                        entityManagerProductStock.getQueryBuilder().where().eq("store_id", currentStore.getId()).prepare()
                ).forEach(productStock -> {
                    if (productStock.getProduct() != null) {
                        productStocks.add(new ProductStockTree(
                                productStock.getProduct().getReference(),
                                productStock.getProduct().getName(),
                                productStock.getProduct().getPurchasePrice(),
                                productStock.getQuantity(),
                                productStock.getProduct().getState()
                        ));
                    }
                });
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }

        TreeItem<ProductStockTree> root = new RecursiveTreeItem<>(productStocks, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.stockTreeTableView.getColumns().setAll(reference, product, unitValue, stockQuantity, state);
        this.stockTreeTableView.setRoot(root);
        this.stockTreeTableView.setShowRoot(false);
    }

    /**
     * Adds a product stock to the table
     *
     * @param productStock The product stock to add to the table
     */
    private void addProductStockToTreeTable(ProductStock productStock) {
        TreeItem<ProductStockTree> productStockRow = new TreeItem<>(new ProductStockTree(
                productStock.getProduct().getReference(),
                productStock.getProduct().getName(),
                productStock.getProduct().getPurchasePrice(),
                productStock.getQuantity(),
                productStock.getProduct().getState()
        ));

        this.stockTreeTableView.getRoot().getChildren().remove(stockTreeTableView.getSelectionModel().getSelectedItem());
        this.stockTreeTableView.getRoot().getChildren().add(productStockRow);
        this.stockTreeTableView.getSelectionModel().select(productStockRow);
    }

    /**
     * Checks if a product stock is selected in the table at the top of the window
     *
     * @return true if a product stock is selected, else false
     */
    private boolean isEditing() {
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
