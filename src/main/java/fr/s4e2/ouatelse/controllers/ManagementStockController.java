package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import fr.s4e2.ouatelse.objects.Store;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class ManagementStockController extends BaseController {

    @FXML
    private JFXTextField stockSearchBar;
    @FXML
    private JFXTreeTableView stockTreeTableView; //todo : create a new tree object
    @FXML
    private JFXComboBox<Store> stockStoreComboBox;
    @FXML
    private JFXTextField stockCurrentQuantityInput;
    @FXML
    private JFXTextField stockWorthInput;
    @FXML
    private JFXTextField stockQuantityInput;

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
    }

    /**
     * Handles the button click event for the schedule order button
     *
     * Prepares an order for a selected product
     */
    public void onScheduleOrderButtonClick() {
    }

    /**
     * Handles the button click event for the delete button
     *
     * Deletes the stock of a product
     */
    public void onDeleteStockButtonClick() {
    }

    /**
     * Handles the button click event for the add button
     *
     * Adds to the stock of a product
     */
    public void onAddStockButtonClick() {
    }
}
