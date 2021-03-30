package fr.s4e2.ouatelse.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import fr.s4e2.ouatelse.utils.PDFUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementSalesScreen}
 */
public class ManagementSalesController extends BaseController {

    private final EntityManagerClient entityManagerClient = Main.getDatabaseManager().getEntityManagerClient();
    private final EntityManagerCart entityManagerCart = Main.getDatabaseManager().getEntityManagerCart();
    private final EntityManagerClientStock entityManagerClientStock = Main.getDatabaseManager().getEntityManagerClientStock();
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML
    private Button removeSampleButton;
    @FXML
    private Button addSampleButton;

    @FXML
    private JFXTextField clientSearchBar;
    @FXML
    private JFXTreeTableView<Client.ClientTree> clientsTreeTableView;
    @FXML
    private JFXTreeTableView<Cart.CartTree> currentClientsCartTreeTableView;
    @FXML
    private JFXTreeTableView<ClientStock.ClientStockTree> currentCartProductsTreetableView;

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
            currentCart = null;
            currentClientStock = null;
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

            this.clearInformation();
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

            //load products
            currentCartProductsTreetableView.getRoot().getChildren().clear();
            getClientStocks().forEach(clientStock -> currentCartProductsTreetableView.getRoot().getChildren().add(new TreeItem<>(clientStock.toClientStockTree())));
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

                if (product != null && this.currentCart != null) {
                    this.currentClientStock = this.entityManagerClientStock.executeQuery(
                            this.entityManagerClientStock.getQueryBuilder().where()
                                    .eq("product_id", product.getId()).and()
                                    .eq("cart_id", this.currentCart.getId())
                                    .prepare()
                    ).stream().findFirst().orElse(null);

                    if (this.currentClientStock != null) {
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
        JFXTreeTableColumn<Client.ClientTree, Long> id = new JFXTreeTableColumn<>("Identifiant");
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
        JFXTreeTableColumn<Cart.CartTree, Long> id = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<Cart.CartTree, String> date = new JFXTreeTableColumn<>("Date");
        JFXTreeTableColumn<Cart.CartTree, String> hour = new JFXTreeTableColumn<>("Heure");
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
        this.currentClientsCartTreeTableView.getSelectionModel().clearSelection();
        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.currentCartProductsTreetableView.getSelectionModel().clearSelection();
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
            this.currentClientsCartTreeTableView.getSelectionModel().clearSelection();
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

        int numberOfClosedCarts = (int) currentClient.getCarts().stream().filter(Cart::isClosed).count();
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
    public void onCreateBillButtonClick() throws IOException, DocumentException {
        if (!this.isCartSelected()) return;
        if (this.currentCart.getClientStocks() == null || this.currentCart.getClientStocks().isEmpty()) return;

        this.currentCart.setClosed(true);
        this.entityManagerCart.update(currentCart);
        this.currentClientsCartTreeTableView.getRoot().getChildren().remove(
                currentClientsCartTreeTableView.getSelectionModel().getSelectedItem()
        );
        this.addCartToTreeTable(currentCart);

        this.generateInvoice();
    }

    /**
     * Generates the invoice for the current selected cart
     */
    private void generateInvoice() throws IOException, DocumentException {
        // generate document

        String filePath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()
                + File.separator + currentCart.getClient().getId() + "-" + currentCart.getId() + ".pdf";
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        // header
        document.addTitle("Facture Ouatelse");
        document.addHeader("title", "Ouatelse");

        // top section
        Paragraph headerTitle = PDFUtils.buildH1Title("FACTURE OUATELSE #" + currentCart.getClient().getId() + "-" + currentCart.getId());
        headerTitle.getFont().setSize(20f);
        headerTitle.setAlignment(Element.ALIGN_RIGHT);
        headerTitle.setSpacingAfter(25);
        document.add(headerTitle);

        // client section
        Client client = currentCart.getClient();
        Paragraph id = PDFUtils.buildH2Title("N° Client : " + client.getId());
        id.setAlignment(Element.ALIGN_LEFT);
        document.add(id);

        Paragraph date = PDFUtils.buildH2Title("Date : " + new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        date.setAlignment(Element.ALIGN_LEFT);
        date.setSpacingAfter(15);
        document.add(date);

        Paragraph clientName = PDFUtils.buildH2Title(client.getSurname() + ", " + client.getName());
        clientName.setAlignment(Element.ALIGN_LEFT);
        document.add(clientName);

        Paragraph clientEmail = PDFUtils.buildH2Title(client.getEmail());
        clientEmail.setAlignment(Element.ALIGN_LEFT);
        document.add(clientEmail);

        Paragraph clientAdress = PDFUtils.buildH2Title(client.getAddress().getStreetNameAndNumber());
        clientAdress.setAlignment(Element.ALIGN_LEFT);
        document.add(clientAdress);

        Paragraph clientCity = PDFUtils.buildH2Title(client.getAddress().getZipCode() + ", " + client.getAddress().getCity());
        clientCity.setAlignment(Element.ALIGN_LEFT);
        document.add(clientCity);

        // cart section
        document.add(PDFUtils.buildH1Title("Informations Panier"));
        document.add(getCartTable());

        // products section
        document.add(PDFUtils.buildH1Title("Produits"));
        getProductsTable().forEach(table -> {
            try {
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
        document.close();
        pdfWriter.close();

        Desktop.getDesktop().open(new File(filePath));
    }

    /**
     * Returns the pdf tables for the products in the cart
     *
     * @return the pdf tables for the products in the cart
     */
    private List<PdfPTable> getProductsTable() {
        List<PdfPTable> tables = new ArrayList<>();

        // PRODUCTS IN CART TABLE
        PdfPTable productsTable = new PdfPTable(5);

        // add headers
        Stream.of("Référence", "Produit", "Marque", "Quantité", "Prix TTC").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            productsTable.addCell(header);
        });

        AtomicReference<Double> totalCost = new AtomicReference<>((double) 0);
        AtomicInteger articleCount = new AtomicInteger();
        // add cells
        currentCart.getClientStocks().forEach(clientStock -> {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase("#" + clientStock.getProduct().getReference()));
            productsTable.addCell(cell);
            cell.setPhrase(new Phrase(clientStock.getProduct().getName()));
            productsTable.addCell(cell);
            cell.setPhrase(new Phrase(clientStock.getProduct().getBrand()));
            productsTable.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(clientStock.getQuantity())));
            productsTable.addCell(cell);
            cell.setPhrase(new Phrase(clientStock.getQuantity() * clientStock.getProduct().getSellingPrice() + " €"));
            productsTable.addCell(cell);

            totalCost.updateAndGet(v -> v + clientStock.getQuantity() * clientStock.getProduct().getSellingPrice());
            articleCount.incrementAndGet();
        });
        productsTable.setSpacingAfter(50);
        tables.add(productsTable);

        // TOTAL TABLE
        PdfPTable totalTable = new PdfPTable(4);
        Stream.of("Nb. Articles", "", "", "Prix Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            totalTable.addCell(header);
        });

        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(String.valueOf(articleCount.get())));
        totalTable.addCell(cell);
        cell.setPhrase(new Phrase(""));
        totalTable.addCell(cell);
        cell.setPhrase(new Phrase(""));
        totalTable.addCell(cell);
        cell.setPhrase(new Phrase(totalCost.get() + " €"));
        totalTable.addCell(cell);
        tables.add(totalTable);

        return tables;
    }

    /**
     * Returns the pdf table for the cart
     *
     * @return the pdf table for the cart
     */
    private PdfPTable getCartTable() {
        PdfPTable cartTable = new PdfPTable(3);

        // add headers
        Stream.of("Identifiant", "Date", "Heure").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            cartTable.addCell(header);
        });

        // add cell
        Date date = currentCart.getDate();
        PdfPCell cell = new PdfPCell();

        cell.setPhrase(new Phrase(String.valueOf(currentCart.getId())));
        cartTable.addCell(cell);
        cell.setPhrase(new Phrase(date != null ? new SimpleDateFormat("yyyy/MM/dd").format(date) : ""));
        cartTable.addCell(cell);
        cell.setPhrase(new Phrase(date != null ? new SimpleDateFormat("hh:mm:ss").format(date) : ""));
        cartTable.addCell(cell);

        cartTable.setSpacingAfter(50);
        return cartTable;
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

    public void onAddSampleButton() {
        if (this.currentClientStock == null) return;

        this.currentClientStock.setQuantity(this.currentClientStock.getQuantity() + 1);
        this.entityManagerClientStock.update(this.currentClientStock);

        this.currentCartProductsTreetableView.getRoot().getChildren().clear();
        this.currentClientsCartTreeTableView.getRoot().getChildren().clear();
        this.loadInformation();
    }

    public void onRemoveSampleButton() {
        if (this.currentClientStock == null || this.currentCart == null) return;

        this.currentClientStock.setQuantity(this.currentClientStock.getQuantity() - 1);

        if (this.currentClientStock.getQuantity() == 0) {
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