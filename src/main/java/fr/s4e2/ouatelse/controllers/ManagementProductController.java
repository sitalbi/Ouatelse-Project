package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import fr.s4e2.ouatelse.objects.Order;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.Vendor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class ManagementProductController extends BaseController {

    // top
    @FXML
    private TreeView productsTreeView; // todo : create ProductTree

    // Information Tab
    @FXML
    private JFXTextField informationNameInput;
    @FXML
    private JFXTextField informationReferenceInput;
    @FXML
    private JFXTextField informationBarcodeInput;
    @FXML
    private JFXTextField informationCommentInput;
    @FXML
    private JFXCheckBox informationStockCheckBox;
    @FXML
    private Button confirmInformationButton;
    @FXML
    private Button deleteProductButton;

    // Description Tab
    @FXML
    private JFXTextField descriptionTypeInput;
    @FXML
    private JFXTextField descriptionCategoryInput;
    @FXML
    private JFXTextField descriptionBrandInput;
    @FXML
    private JFXTextField descriptionModelInput;
    @FXML
    private ComboBox<Vendor> descriptionVendorComboBox;
    @FXML
    private Button confirmDescriptionButton;

    // Prices & Stock Tab
    @FXML
    private ComboBox<Store> stockStoreComboBox;
    @FXML
    private TreeView<Order> stockOrderTreeView;
    @FXML
    private TreeView stockPricesTreeView; // todo : create another Tree
    @FXML
    private Button confirmPriceStockButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void onConfirmInformationButton() {
    }

    public void onDeleteProductButton() {
    }

    public void onConfirmDescriptionButton() {
    }

    public void onConfirmPriceStockButton() {
    }
}
