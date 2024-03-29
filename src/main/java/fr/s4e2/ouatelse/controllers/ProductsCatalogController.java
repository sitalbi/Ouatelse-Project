package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerCart;
import fr.s4e2.ouatelse.managers.EntityManagerClientStock;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.ClientStock;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.ProductStock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductsCatalogController extends BaseController {
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    private final EntityManagerProductStock entityManagerProductStock = Main.getDatabaseManager().getEntityManagerProductStock();
    private final EntityManagerCart entityManagerCart = Main.getDatabaseManager().getEntityManagerCart();
    private final EntityManagerClientStock entityManagerClientStock = Main.getDatabaseManager().getEntityManagerClientStock();
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML
    private JFXTextField notInCartSearchBar;
    @FXML
    private JFXTreeTableView<Product.ProductTree> notInCartTableView;
    @FXML
    private Button putInCartButton;
    @FXML
    private Button removeFromCartButton;
    @FXML
    private JFXTextField inCartSearchBar;
    @FXML
    private JFXTreeTableView<Product.ProductTree> inCartTreeTableView;
    private Cart currentCart;
    private Product currentProduct;

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

        this.putInCartButton.setDisable(true);
        this.removeFromCartButton.setDisable(true);

        this.buildNotInCartTableView();
        this.buildInCartTreeTableView();

        // Manages left search bar
        this.notInCartSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            notInCartTableView.getRoot().getChildren().clear();

            if (newValue.trim().isEmpty()) {
                loadNotInCartTableView();
            } else {
                try {
                    List<Product> searchResults = entityManagerProduct.executeQuery(
                            entityManagerProduct.getQueryBuilder().where().like("name", "%" + newValue.trim() + "%").prepare()
                    );
                    searchResults.forEach(product -> {
                        if (!isInClientsCart(product)) {
                            this.addProductToTreeTable(product, this.notInCartTableView);
                        }
                    });
                } catch (SQLException exception) {
                    this.logger.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }
        });

        // Manages right search bar
        this.inCartSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            inCartTreeTableView.getRoot().getChildren().clear();

            if (newValue.trim().isEmpty()) {
                loadInCartTableView();
            } else {
                try {
                    List<Product> searchResults = entityManagerProduct.executeQuery(
                            entityManagerProduct.getQueryBuilder().where().like("name", "%" + newValue.trim() + "%").prepare()
                    );
                    searchResults.forEach(product -> {
                        if (isInClientsCart(product)) {
                            this.addProductToTreeTable(product, this.inCartTreeTableView);
                        }
                    });
                } catch (SQLException exception) {
                    this.logger.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }
        });

        // Enables or disables button on select and unselect
        this.notInCartTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectProductFromTable(newValue);

            if (this.currentProduct == null) {
                this.putInCartButton.setDisable(true);
                this.removeFromCartButton.setDisable(true);
            } else {
                this.putInCartButton.setDisable(false);
                this.removeFromCartButton.setDisable(true);
            }
        });

        // Enables or disables button on select and unselect
        this.inCartTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectProductFromTable(newValue);

            if (this.currentProduct == null) {
                this.putInCartButton.setDisable(true);
                this.removeFromCartButton.setDisable(true);
            } else {
                this.putInCartButton.setDisable(true);
                this.removeFromCartButton.setDisable(false);
            }
        });
    }

    private void selectProductFromTable(TreeItem<Product.ProductTree> newValue) {
        if (newValue != null) {
            try {
                currentProduct = entityManagerProduct.executeQuery(entityManagerProduct.getQueryBuilder()
                        .where().eq("reference", newValue.getValue().getReference().getValue()).prepare())
                        .stream().findFirst().orElse(null);

                putInCartButton.setDisable(false);
                removeFromCartButton.setDisable(false);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }

        } else {
            currentProduct = null;
            putInCartButton.setDisable(true);
            removeFromCartButton.setDisable(true);
        }
    }

    /**
     * Builds the list of products into the table
     */
    private void buildNotInCartTableView() {
        JFXTreeTableColumn<Product.ProductTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<Product.ProductTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Product.ProductTree, Double> sellingPrice = new JFXTreeTableColumn<>("Prix de vente");
        JFXTreeTableColumn<Product.ProductTree, String> brand = new JFXTreeTableColumn<>("Marque");

        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        sellingPrice.setCellValueFactory(param -> param.getValue().getValue().getSellingPrice().asObject());
        brand.setCellValueFactory(param -> param.getValue().getValue().getBrand());

        ObservableList<Product.ProductTree> products = FXCollections.observableArrayList();
        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.notInCartTableView.getColumns().setAll(reference, name, sellingPrice, brand);
        this.notInCartTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.notInCartTableView.setRoot(root);
        this.notInCartTableView.setShowRoot(false);
    }

    /**
     * Loads the items in notInCartTableView
     */
    private void loadNotInCartTableView() {
        try {
            List<ProductStock> productStocks = this.entityManagerProductStock.executeQuery(
                    this.entityManagerProductStock.getQueryBuilder().where()
                            .eq("store_id", this.getAuthentificationStore().getId()).prepare()
            );

            productStocks.forEach(productStock -> {
                if (
                        productStock.getQuantity() > 0
                                && !isInClientsCart(productStock.getProduct())
                ) {
                    this.addProductToTreeTable(productStock.getProduct(), this.notInCartTableView);
                }
            });
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Loads the items in inCartTableView
     */
    private void loadInCartTableView() {
        this.getClientStocks().forEach(clientStock -> this.addProductToTreeTable(clientStock.getProduct(), this.inCartTreeTableView));
    }

    /**
     * Check if a product is already in the client's cart
     *
     * @param product The product to be checked
     * @return true if the items is present, else false
     */
    private boolean isInClientsCart(Product product) {
        for (ClientStock clientStock : this.getClientStocks()) {
            if (clientStock.getProduct().getId() == product.getId()) return true;
        }

        return false;
    }

    /**
     * Gets the clientStocks from a cart because ORMLite is buggy
     *
     * @return A List<ClientStocks> of the cart's ClientStock
     */
    private List<ClientStock> getClientStocks() {
        if (this.currentCart == null) return new ArrayList<>();

        List<ClientStock> result = new ArrayList<>();

        try {
            result = this.entityManagerClientStock.executeQuery(
                    this.entityManagerClientStock.getQueryBuilder()
                            .where().eq("cart_id", this.currentCart.getId())
                            .prepare()
            );
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return result;
    }

    /**
     * Builds the list of products into the table
     */
    private void buildInCartTreeTableView() {
        JFXTreeTableColumn<Product.ProductTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<Product.ProductTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Product.ProductTree, Double> sellingPrice = new JFXTreeTableColumn<>("Prix de vente");
        JFXTreeTableColumn<Product.ProductTree, String> brand = new JFXTreeTableColumn<>("Marque");

        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        sellingPrice.setCellValueFactory(param -> param.getValue().getValue().getSellingPrice().asObject());
        brand.setCellValueFactory(param -> param.getValue().getValue().getBrand());

        ObservableList<Product.ProductTree> products = FXCollections.observableArrayList();
        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.inCartTreeTableView.getColumns().setAll(reference, name, sellingPrice, brand);
        this.inCartTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.inCartTreeTableView.setRoot(root);
        this.inCartTreeTableView.setShowRoot(false);
    }

    /**
     * Removes the selected item from the client's cart
     */
    public void onRemoveFromCartButton() {
        ClientStock clientStockToBeRemoved = null;

        for (ClientStock clientStock : this.getClientStocks()) {
            if (clientStock.getProduct().getId() == this.currentProduct.getId()) {
                clientStockToBeRemoved = clientStock;
            }
        }

        if (clientStockToBeRemoved == null) return;

        inCartTreeTableView.getRoot().getChildren().remove(inCartTreeTableView.getSelectionModel().getSelectedItem());
        addProductToTreeTable(clientStockToBeRemoved.getProduct(), notInCartTableView);
        this.getClientStocks().remove(clientStockToBeRemoved);
        this.entityManagerCart.update(this.currentCart);
        this.entityManagerClientStock.delete(clientStockToBeRemoved);
    }

    /**
     * Add the selected item to the client's cart
     */
    public void onPutInCartButton() {
        ClientStock clientStock = new ClientStock();

        clientStock.setProduct(this.currentProduct);
        clientStock.setQuantity(1);
        clientStock.setClient(this.currentCart.getClient());
        clientStock.setCart(this.currentCart);

        notInCartTableView.getRoot().getChildren().remove(notInCartTableView.getSelectionModel().getSelectedItem());
        addProductToTreeTable(clientStock.getProduct(), inCartTreeTableView);
        this.entityManagerClientStock.create(clientStock);


        try {
            this.getClientStocks().add(clientStock);
        } catch (NullPointerException exception) {
            this.logger.log(Level.WARNING, exception.getMessage(), exception);
        }

        this.entityManagerCart.update(this.currentCart);
    }

    /**
     * Adds a product to the selected sheet
     *
     * @param product The product to add to the sheet
     */
    private void addProductToTreeTable(Product product, TreeTableView<Product.ProductTree> productsTreeView) {
        TreeItem<Product.ProductTree> productRow = new TreeItem<>(product.toProductTree());

        productsTreeView.getRoot().getChildren().add(productRow);
        productsTreeView.getSelectionModel().select(productRow);
        this.currentProduct = product;
    }

    /**
     * Set the cart selected by the user
     *
     * @param currentCart The cart selected by the user
     */
    public void setCurrentCart(Cart currentCart) {
        this.currentCart = currentCart;
        this.loadNotInCartTableView();
        this.loadInCartTableView();
    }


}