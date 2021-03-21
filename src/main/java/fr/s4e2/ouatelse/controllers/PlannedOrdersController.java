package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.managers.EntityManagerScheduledOrder;
import fr.s4e2.ouatelse.objects.Product;
import fr.s4e2.ouatelse.objects.ScheduledOrder;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.utils.JFXUtils;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlannedOrdersController extends BaseController {
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    private final EntityManagerScheduledOrder entityManagerScheduledOrder = Main.getDatabaseManager().getEntityManagerScheduledOrder();
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML
    private Label errorMessage;
    @FXML
    private Button confirmButton;
    @FXML
    private JFXTreeTableView<ScheduledOrder.ScheduledOrderInfoTree> orderTreeTableView;
    @FXML
    private JFXTextField articleReferenceField;
    @FXML
    private JFXDatePicker orderDate;
    @FXML
    private JFXTextField quantityReferenceField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        this.orderDate.setConverter(JFXUtils.getDateConverter());

        this.loadOrdersTreeTable();
    }

    /**
     * Load the order of the store in which the user is logged
     */
    private void loadOrdersTreeTableContent() {
        this.orderTreeTableView.getRoot().getChildren().clear();
        try {
            this.entityManagerScheduledOrder.executeQuery(
                    this.entityManagerScheduledOrder.getQueryBuilder()
                            .where().eq("store_id", this.getAuthentificationStore().getId())
                            .prepare()
            ).forEach(this::addScheduledOrderToTreeTable);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Builds the TreeTableView and its columns
     */
    private void loadOrdersTreeTable() {
        JFXTreeTableColumn<ScheduledOrder.ScheduledOrderInfoTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ScheduledOrder.ScheduledOrderInfoTree, Integer> quantity = new JFXTreeTableColumn<>("Produit");
        JFXTreeTableColumn<ScheduledOrder.ScheduledOrderInfoTree, String> scheduledOrderDate = new JFXTreeTableColumn<>("Date");
        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getProductReference().asObject());
        quantity.setCellValueFactory(param -> param.getValue().getValue().getQuantity().asObject());
        scheduledOrderDate.setCellValueFactory(param -> param.getValue().getValue().getScheduledOrderDate());

        ObservableList<ScheduledOrder.ScheduledOrderInfoTree> scheduledOrders = FXCollections.observableArrayList();
        TreeItem<ScheduledOrder.ScheduledOrderInfoTree> root = new RecursiveTreeItem<>(scheduledOrders, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.orderTreeTableView.getColumns().setAll(reference, quantity, scheduledOrderDate);
        this.orderTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.orderTreeTableView.setRoot(root);
        this.orderTreeTableView.setShowRoot(false);
    }

    /**
     * On confirmation button, check if fields are corrects, then insert the order in the database
     *
     * @throws SQLException
     */
    public void onConfirmButtonClick() throws SQLException {
        if (this.articleReferenceField.getText().trim().isEmpty()
                || this.quantityReferenceField.getText().trim().isEmpty()
                || this.orderDate.getValue() == null) {
            this.errorMessage.setText("Veuillez remplir tous les champs");
            return;
        }

        long productReference;
        int quantity;
        Date date = Utils.localDateToDate(this.orderDate.getValue());

        if (date.before(new Date())) {
            this.errorMessage.setText("Date passée");
            this.orderDate.getParent().requestFocus();
            return;
        }

        try {
            productReference = Long.parseLong(this.articleReferenceField.getText().trim());
            quantity = Integer.parseInt(this.quantityReferenceField.getText().trim());
        } catch (NumberFormatException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
            this.errorMessage.setText("Veuillez entrer des caractères numériques");
            return;
        }

        if (quantity <= 0) {
            this.errorMessage.setText("Veuillez entrer un nombre correct de produits à commander");
            this.quantityReferenceField.getParent().requestFocus();
            return;
        }

        Product product = this.entityManagerProduct.executeQuery(
                this.entityManagerProduct.getQueryBuilder()
                        .where().eq("reference", productReference)
                        .prepare()
        ).stream().findFirst().orElse(null);
        if (product == null) {
            this.errorMessage.setText("Produit inexistant");
            this.articleReferenceField.getParent().requestFocus();
            return;
        }

        ScheduledOrder order = new ScheduledOrder(product, this.getAuthentificationStore(), date, quantity);
        this.entityManagerScheduledOrder.create(order);
        addScheduledOrderToTreeTable(order);
        this.clearInformation();
    }

    /**
     * Add an order to the TreeTable
     *
     * @param scheduledOrder The ScheduledOrder object to add
     */
    private void addScheduledOrderToTreeTable(ScheduledOrder scheduledOrder) {
        TreeItem<ScheduledOrder.ScheduledOrderInfoTree> scheduledOrderRow = new TreeItem<>(scheduledOrder.toScheduledOrderInfoTree());

        this.orderTreeTableView.getRoot().getChildren().remove(this.orderTreeTableView.getSelectionModel().getSelectedItem());
        this.orderTreeTableView.getRoot().getChildren().add(scheduledOrderRow);
        this.orderTreeTableView.getSelectionModel().select(scheduledOrderRow);
    }

    /**
     * Clears all the inputs and resets them to default
     */
    private void clearInformation() {
        this.quantityReferenceField.setText("");
        this.articleReferenceField.setText("");
        this.orderDate.getEditor().clear();
    }

    /**
     * Set the store in which the user has logged in
     *
     * @param authentificationStore The store in which the user has logged in
     */
    @Override
    public void setAuthentificationStore(Store authentificationStore) {
        super.setAuthentificationStore(authentificationStore);
        this.loadOrdersTreeTableContent();
    }
}
