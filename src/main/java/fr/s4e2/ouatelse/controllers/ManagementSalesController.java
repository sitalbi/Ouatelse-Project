package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerCart;
import fr.s4e2.ouatelse.managers.EntityManagerClient;
import fr.s4e2.ouatelse.managers.EntityManagerClientStock;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.Client;
import fr.s4e2.ouatelse.objects.ClientStock;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.screens.ProductsCatalogScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementSalesScreen}
 */
public class ManagementSalesController extends BaseController {

    private final EntityManagerClient entityManagerClient = Main.getDatabaseManager().getEntityManagerClient();
    private final EntityManagerCart entityManagerCart = Main.getDatabaseManager().getEntityManagerCart();
    private final EntityManagerClientStock entityManagerClientStock = Main.getDatabaseManager().getEntityManagerClientStock();
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    public Button removeSampleButton;
    public Button addSampleButton;

    @FXML
    private JFXTextField clientSearchBar;
    @FXML
    private JFXTreeTableView<Client.ClientTree> clientsTreeTableView;
    @FXML
    private Label errorMessageField;
    @FXML
    private JFXTreeTableView<Cart.CartTree> currentClientsCartTreeTableView;
    @FXML
    private Button productCatalogButton;
    @FXML
    private JFXTreeTableView<ClientStock.ClientStockTree> currentCartProductsTreetableView;
    @FXML
    private Button newSaleButton;
    @FXML
    private Button createBillButton;
    @FXML
    private Button cancelSaleButton;

    // left -> all the carts of the client
    // right -> client stock

    private Client currentClient;
    private Cart currentCart;
    private ClientStock currentClientStock;

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

        this.buildClientsTreeTableView();
        this.buildCurrentClientsCartTreeTableView();
        this.buildCurrentCartProductsTreetableView();

        this.entityManagerClient.getQueryForAll().forEach(client -> clientsTreeTableView.getRoot().getChildren().add(new TreeItem<>(client.toClientTree())));

        // deselect an item in the stock tree table
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;
            if (clientsTreeTableView.getSelectionModel().getSelectedItem() == null) return;

            clientsTreeTableView.getSelectionModel().clearSelection();
            currentClient = null;
            this.clearInformation();
        });

        // Handles the client selection event
        this.clientsTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentClient = null;
                return;
            }

            try {
                this.currentClient = entityManagerClient.executeQuery(entityManagerClient.getQueryBuilder()
                        .where().eq("id", newValue.getValue().getId().getValue()).prepare())
                        .stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }

            this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
            this.loadInformation();
        });

        // Handles the cart selection event
        this.currentClientsCartTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentCart = null;
                return;
            }

            try {
                this.currentCart = entityManagerCart.executeQuery(entityManagerCart.getQueryBuilder()
                        .where().eq("id", newValue.getValue().getId().getValue()).prepare())
                        .stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        });

        this.currentCartProductsTreetableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentClientStock = null;
                this.removeSampleButton.setDisable(true);
                this.addSampleButton.setDisable(true);
                return;
            }

            try {
                Product product = this.entityManagerProduct.executeQuery(
                        this.entityManagerProduct.getQueryBuilder().where()
                        .eq("reference", newValue.getValue().getReference().getValue())
                        .prepare()
                ).stream().findFirst().orElse(null);

                if(product != null && this.currentCart != null) {
                    this.currentClientStock = this.entityManagerClientStock.executeQuery(
                            this.entityManagerClientStock.getQueryBuilder().where()
                                    .eq("product_id", product.getId()).and()
                            .eq("cart_id", this.currentCart.getId())
                            .prepare()
                    ).stream().findFirst().orElse(null);

                    if(this.currentClientStock != null) {
                        this.addSampleButton.setDisable(false);
                        this.removeSampleButton.setDisable(false);
                    }
                }
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        });

        // handles search bar for clients
        this.clientSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchProductFromText(newValue.trim()));
    }


    /**
     * Searches a client in the database from its name or email
     *
     * @param input the searched client name or email
     */
    private void searchProductFromText(String input) {
        this.clientsTreeTableView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.entityManagerClient.getQueryForAll().forEach(client -> clientsTreeTableView.getRoot().getChildren().add(new TreeItem<>(client.toClientTree())));
            return;
        }

        try {
            List<Client> searchResults = entityManagerClient.executeQuery(
                    entityManagerClient.getQueryBuilder()
                            .where().like("name", "%" + input + "%")
                            .or().like("email", "%" + input + "%")
                            .prepare()
            );
            searchResults.forEach(this::addClientToTreeTable);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Builds the clients' table
     */
    private void buildClientsTreeTableView() {
        JFXTreeTableColumn<Client.ClientTree, Long> id = new JFXTreeTableColumn<>("ID");
        JFXTreeTableColumn<Client.ClientTree, String> name = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Client.ClientTree, String> surname = new JFXTreeTableColumn<>("Prénom");
        JFXTreeTableColumn<Client.ClientTree, String> email = new JFXTreeTableColumn<>("Email");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId().asObject());
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        surname.setCellValueFactory(param -> param.getValue().getValue().getSurname());
        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        ObservableList<Client.ClientTree> clients = FXCollections.observableArrayList();
        TreeItem<Client.ClientTree> root = new RecursiveTreeItem<>(clients, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.clientsTreeTableView.getColumns().setAll(id, name, surname, email);
        this.clientsTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.clientsTreeTableView.setRoot(root);
        this.clientsTreeTableView.setShowRoot(false);
    }

    /**
     * Build the current client's cart table
     */
    private void buildCurrentClientsCartTreeTableView() {
        JFXTreeTableColumn<Cart.CartTree, Long> id = new JFXTreeTableColumn<>("ID");
        JFXTreeTableColumn<Cart.CartTree, String> date = new JFXTreeTableColumn<>("Date");
        JFXTreeTableColumn<Cart.CartTree, String> hour = new JFXTreeTableColumn<>("Hour");
        JFXTreeTableColumn<Cart.CartTree, String> closed = new JFXTreeTableColumn<>("Fermer");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId().asObject());
        date.setCellValueFactory(param -> param.getValue().getValue().getDate());
        hour.setCellValueFactory(param -> param.getValue().getValue().getHour());
        closed.setCellValueFactory(param -> param.getValue().getValue().getClosed());

        ObservableList<Cart.CartTree> carts = FXCollections.observableArrayList();
        TreeItem<Cart.CartTree> root = new RecursiveTreeItem<>(carts, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.currentClientsCartTreeTableView.getColumns().setAll(id, date, hour, closed);
        this.currentClientsCartTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.currentClientsCartTreeTableView.setRoot(root);
        this.currentClientsCartTreeTableView.setShowRoot(false);
    }

    /**
     * Build the current cart's products table
     */
    private void buildCurrentCartProductsTreetableView() {
        JFXTreeTableColumn<ClientStock.ClientStockTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ClientStock.ClientStockTree, String> productName = new JFXTreeTableColumn<>("Produit");
        JFXTreeTableColumn<ClientStock.ClientStockTree, Integer> quantity = new JFXTreeTableColumn<>("Quantité");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        productName.setCellValueFactory(param -> param.getValue().getValue().getProductName());
        quantity.setCellValueFactory(param -> param.getValue().getValue().getStockQuantity().asObject());

        ObservableList<ClientStock.ClientStockTree> clientStocks = FXCollections.observableArrayList();
        TreeItem<ClientStock.ClientStockTree> root = new RecursiveTreeItem<>(clientStocks, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.currentCartProductsTreetableView.getColumns().setAll(reference, productName, quantity);
        this.currentCartProductsTreetableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.currentCartProductsTreetableView.setRoot(root);
        this.currentCartProductsTreetableView.setShowRoot(false);
    }

    /**
     * Adds a client to the sheet at the top of the window
     *
     * @param client The client to add to the sheet
     */
    private void addClientToTreeTable(Client client) {
        TreeItem<Client.ClientTree> clientRow = new TreeItem<>(client.toClientTree());

        this.clientsTreeTableView.getRoot().getChildren().add(clientRow);
        this.clientsTreeTableView.getSelectionModel().select(clientRow);
        this.currentClient = client;
    }

    /**
     * Adds a cart to the sheet at the bottom left of the window
     *
     * @param cart The cart to add to the sheet
     */
    private void addCartToTreeTable(Cart cart) {
        TreeItem<Cart.CartTree> cartrow = new TreeItem<>(cart.toCartTree());

        this.currentClientsCartTreeTableView.getRoot().getChildren().add(cartrow);
        this.currentClientsCartTreeTableView.getSelectionModel().select(cartrow);
        this.currentCart = cart;
    }

    /**
     * Adds a client stock to the sheet at the bottom middle of the window
     *
     * @param clientStock The client stock to add to the sheet
     */
    private void addClientStockToTreeTable(ClientStock clientStock) {
        TreeItem<ClientStock.ClientStockTree> clientStockrow = new TreeItem<>(clientStock.toClientStockTree());

        this.currentCartProductsTreetableView.getRoot().getChildren().add(clientStockrow);
        this.currentCartProductsTreetableView.getSelectionModel().select(clientStockrow);
        this.currentClientStock = clientStock;
    }


    /**
     * Clears the client sales information from the different tables
     */
    private void clearInformation() {
        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.currentCartProductsTreetableView.getRoot().getChildren().clear();
    }

    /**
     * Loads the information of client sales into the table
     */
    private void loadInformation() {
        if (!this.isClientSelected()) return;

        // loads client carts
        try {
            List<Cart> clientCarts = entityManagerCart.executeQuery(entityManagerCart.getQueryBuilder()
                    .where().eq("client_id", currentClient.getId())
                    .prepare()
            );
            clientCarts.forEach(this::addCartToTreeTable);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        // loads client stock
        try {
            List<ClientStock> clientStocks = entityManagerClientStock.executeQuery(entityManagerClientStock.getQueryBuilder()
                    .where().eq("client_id", currentClient.getId())
                    .prepare()
            );
            clientStocks.forEach(this::addClientStockToTreeTable);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public void onProductCatalogButtonClick() {
        if (this.currentCart == null) return;
        new ProductsCatalogScreen(this.getAuthentificationStore(), this.currentCart).open();
    }

    /**
     * Handles the button click event for the new sales button
     * <p>
     * Creates a new cart for a selected user
     */
    public void onNewSaleButtonClick() {
        if (!this.isClientSelected()) return;
        int numberOfClosedCarts = 0;

        // KEEP THIS BECAUSE WITH A STREAM THERE IS A BUG
        for (Cart c : currentClient.getCarts()) {
            if (c.isClosed()) numberOfClosedCarts++;
        }

        if (numberOfClosedCarts != currentClient.getCarts().size()) return;

        Cart cart = new Cart();
        cart.setClient(currentClient);

        this.currentClient.getCarts().add(cart);
        this.entityManagerClient.update(currentClient);

        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.loadInformation();

    }

    /**
     * Creates a bill from the user's cart
     */
    public void onCreateBillButtonClick() {
        throw new UnsupportedOperationException("Create bill");
    }

    /**
     * Handles the button click event for the delete sales button
     * <p>
     * Deletes cart for a selected user
     */
    public void onCancelSaleButtonClick() {
        if (!this.isClientSelected()) return;
        if (!this.isCartSelected()) return;

        this.currentClient.getCarts().remove(currentCart);
        this.entityManagerClient.update(currentClient);

        try {
            this.getClientStocks().forEach(this.entityManagerClientStock::delete);
        } catch (NullPointerException exception) {
            this.logger.log(Level.WARNING, exception.getMessage(), exception);
        }

        this.entityManagerCart.delete(currentCart);

        this.currentCartProductsTreetableView.getRoot().getChildren().clear();
        this.currentClientsCartTreeTableView.getRoot().getChildren().remove(currentClientsCartTreeTableView.getSelectionModel().getSelectedItem());
        this.currentCart = null;
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
     * Checks if a client is selected in the table at the top of the window
     *
     * @return true if a client is selected, else false
     */
    private boolean isClientSelected() {
        return this.currentClient != null;
    }

    /**
     * Checks if a client is selected in the table at the top of the window
     *
     * @return true if a client is selected, else false
     */
    private boolean isCartSelected() {
        return this.currentCart != null;
    }

    public void onAddSampleButton(MouseEvent mouseEvent) {
        if(this.currentClientStock == null) return;

        this.currentClientStock.setQuantity(this.currentClientStock.getQuantity() + 1);
        this.entityManagerClientStock.update(this.currentClientStock);

        this.currentCartProductsTreetableView.getRoot().getChildren().clear();
        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.loadInformation();
    }

    public void onRemoveSampleButton(MouseEvent mouseEvent) {
        if(this.currentClientStock == null || this.currentCart == null) return;

        this.currentClientStock.setQuantity(this.currentClientStock.getQuantity() - 1);

        if(this.currentClientStock.getQuantity() == 0) {
            this.getClientStocks().remove(this.currentClientStock);

            this.entityManagerCart.update(this.currentCart);
            this.entityManagerClientStock.delete(this.currentClientStock);
        } else {
            this.entityManagerClientStock.update(this.currentClientStock);
        }

        this.currentCartProductsTreetableView.getRoot().getChildren().clear();
        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.loadInformation();
    }
}