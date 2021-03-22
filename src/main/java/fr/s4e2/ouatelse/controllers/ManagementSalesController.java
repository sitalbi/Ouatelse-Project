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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
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

        // TODO : Finish here
        // On the window's top sheet click, load the selected client's carts
        this.clientsTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentClient = null;
                return;
            }
        });
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

        this.clientsTreeTableView.getRoot().getChildren().remove(clientsTreeTableView.getSelectionModel().getSelectedItem());
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

        this.currentClientsCartTreeTableView.getRoot().getChildren().remove(currentClientsCartTreeTableView.getSelectionModel().getSelectedItem());
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

        this.currentCartProductsTreetableView.getRoot().getChildren().remove(currentCartProductsTreetableView.getSelectionModel().getSelectedItem());
        this.currentCartProductsTreetableView.getRoot().getChildren().add(clientStockrow);
        this.currentCartProductsTreetableView.getSelectionModel().select(clientStockrow);
        this.currentClientStock = clientStock;
    }

    public void onProductCatalogButtonClick(MouseEvent mouseEvent) {
    }

    public void onNewSaleButtonClick(MouseEvent mouseEvent) {
    }

    public void onSaveCurrentSaleButtonClick(MouseEvent mouseEvent) {
    }

    public void onCreateBillButtonClick(MouseEvent mouseEvent) {
    }

    public void onCancelSaleButtonClick(MouseEvent mouseEvent) {
    }
}
