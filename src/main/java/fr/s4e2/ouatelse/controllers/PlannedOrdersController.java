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
    private Store currentStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        this.orderDate.setConverter(JFXUtils.getDateConverter());

        this.loadOrdersTreeTable();
    }

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

    public void onConfirmButtonClick() throws SQLException {
        if (this.articleReferenceField.getText().trim().isEmpty()
                || this.quantityReferenceField.getText().trim().isEmpty()
                || this.orderDate.getValue() == null) {
            this.errorMessage.setText("Veuillez remplir tous les champs");
            return;
        }

        long productReference;
        int quantity;

        try {
            productReference = Long.parseLong(this.articleReferenceField.getText().trim());
            quantity = Integer.parseInt(this.quantityReferenceField.getText().trim());
        } catch (NumberFormatException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
            this.errorMessage.setText("Veuillez entrer des caractères numériques");
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

        ScheduledOrder order = new ScheduledOrder(product, this.currentStore, Utils.localDateToDate(this.orderDate.getValue()), quantity);
        this.entityManagerScheduledOrder.create(order);
        addProductStockToTreeTable(order);
        this.clearInformation();
    }

    private void addProductStockToTreeTable(ScheduledOrder scheduledOrder) {
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

    public void setCurrentStore(Store store) {
        this.currentStore = store;
    }
}
