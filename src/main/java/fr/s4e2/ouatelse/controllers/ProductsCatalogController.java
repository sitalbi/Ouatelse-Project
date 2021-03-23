package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProduct;
import fr.s4e2.ouatelse.objects.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductsCatalogController extends BaseController {
    private final EntityManagerProduct entityManagerProduct = Main.getDatabaseManager().getEntityManagerProduct();
    public JFXTextField notInCartSearchBar;
    public JFXTreeTableView<Product.ProductTree> notInCartTableView;
    public Button putInCartButton;
    public Button removeFromCartButton;
    public JFXTextField inCartSearchBar;
    public JFXTreeTableView<Product.ProductTree> inCartTreeTableView;


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

        this.loadNotInCartTableView();
        this.loadInCartTreeTableView();
    }

    /**
     * Loads the list of products into the table
     */
    private void loadNotInCartTableView() {
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
        this.entityManagerProduct.getQueryForAll().forEach(product -> products.add(product.toProductTree()));

        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.notInCartTableView.getColumns().setAll(reference, name, sellingPrice, brand);
        this.notInCartTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.notInCartTableView.setRoot(root);
        this.notInCartTableView.setShowRoot(false);
    }

    /**
     * Loads the list of products into the table
     */
    private void loadInCartTreeTableView() {
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
        this.entityManagerProduct.getQueryForAll().forEach(product -> products.add(product.toProductTree()));

        TreeItem<Product.ProductTree> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);

        //noinspection unchecked
        this.inCartTreeTableView.getColumns().setAll(reference, name, sellingPrice, brand);
        this.inCartTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.inCartTreeTableView.setRoot(root);
        this.inCartTreeTableView.setShowRoot(false);
    }

    public void onRemoveFromCartButton(MouseEvent mouseEvent) {
    }

    public void onPutInCartButton(MouseEvent mouseEvent) {
    }
}
