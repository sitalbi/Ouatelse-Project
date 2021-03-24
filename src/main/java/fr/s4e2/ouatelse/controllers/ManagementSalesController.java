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
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.Client;
import fr.s4e2.ouatelse.objects.ClientStock;
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
    private final Logger logger = Logger.getLogger(this.getClass().getName());

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
    private Button saveCurrentSaleButton;
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

        // On the window's top table select client
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

            this.loadInformation();
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
        JFXTreeTableColumn<Cart.CartTree, String> date = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<Cart.CartTree, String> hour = new JFXTreeTableColumn<>("Prénom");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId().asObject());
        date.setCellValueFactory(param -> param.getValue().getValue().getDate());
        hour.setCellValueFactory(param -> param.getValue().getValue().getHour());

        ObservableList<Cart.CartTree> carts = FXCollections.observableArrayList();
        TreeItem<Cart.CartTree> root = new RecursiveTreeItem<>(carts, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.currentClientsCartTreeTableView.getColumns().setAll(id, date, hour);
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
        new ProductsCatalogScreen(this.getAuthentificationStore(), this.currentCart).open();
    }

    public void onNewSaleButtonClick(MouseEvent mouseEvent) {
    }

    public void onSaveCurrentSaleButtonClick(MouseEvent mouseEvent) {
    }

    public void onCreateBillButtonClick(MouseEvent mouseEvent) {
    }

    public void onCancelSaleButtonClick(MouseEvent mouseEvent) {
    }


    /**
     * Checks if a client is selected in the table at the top of the window
     *
     * @return true if a client is selected, else false
     */
    private boolean isClientSelected() {
        return this.currentClient != null;
    }
}