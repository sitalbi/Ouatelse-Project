package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerProductStock;
import fr.s4e2.ouatelse.objects.ProductStock;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.HomeScreen}
 */
public class HomeController extends BaseController {

    private static final double DEFAULT_BUTTON_SIZE = 1000;
    private static final double MINIMUM_BUTTON_HEIGHT = 74;

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final EntityManagerProductStock entityManagerProductStock = Main.getDatabaseManager().getEntityManagerProductStock();
    private User currentUser;
    private Store currentStore;

    @FXML
    private VBox verticalButtonsBar;
    @FXML
    private Label roleField;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private Label homeAdminName;
    @FXML
    private Label homeAdminEmail;
    @FXML
    private Pane leftPane;
    @FXML
    private JFXButton disconnectionButton;

    /**
     * Sets the current user for this Screen
     *
     * @param user the {@link User} to set
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;

        this.homeAdminName.setText(this.currentUser.getName() + " " + this.currentUser.getSurname());
        this.homeAdminEmail.setText(this.currentUser.getEmail());
        this.roleField.setText(this.currentUser.getRole().toString());

        // Use the VBox to emulate a CSS flexbox
        this.scrollPanel.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (MINIMUM_BUTTON_HEIGHT * this.verticalButtonsBar.getChildren().size() > this.scrollPanel.heightProperty().get()) {
                this.verticalButtonsBar.setMinHeight(MINIMUM_BUTTON_HEIGHT * this.verticalButtonsBar.getChildren().size());
            } else {
                this.verticalButtonsBar.setMinHeight(this.scrollPanel.heightProperty().get());
                this.verticalButtonsBar.getChildren().forEach(button -> ((JFXButton) button).prefHeightProperty().bind(this.verticalButtonsBar.heightProperty().divide(this.verticalButtonsBar.getChildren().size())));
            }
        });

        this.buildButtonsFromPermissions();
    }

    public void setCurrentStore(Store store) {
        this.currentStore = store;
    }

    /**
     * Handles the button click event for the disconnect button
     * <p>
     * Disconnects from the Ouatelse application
     */
    public void onDisconnectClick() {
        this.currentUser = null;
        Stage stage = (Stage) this.homeAdminName.getScene().getWindow();
        stage.close();

        new AuthUserScreen().open();
    }

    /**
     * Handles the button click event for the user management button
     * <p>
     * Opens the User Management Screen
     */
    private void onUserManagementButtonClick() {
        new ManagementUserScreen().open();
    }

    /**
     * Handles the button click event for the role management button
     * <p>
     * Opens the Role Management Screen
     */
    private void onRoleManagementButtonClick() {
        new ManagementRoleScreen().open();
    }

    /**
     * Handles the button click event for the store management button
     * <p>
     * Opens the Store Management Screen
     */
    private void onStoresButtonClick() {
        new ManagementStoreScreen().open();
    }

    /**
     * Handles the button click event for the monitoring button
     * <p>
     * Opens the Monitoring Screen
     */
    private void onMonitoringButtonClick() {
        //todo : open monitoring screen

        System.out.println("Open monitoring screen");
    }

    /**
     * Handles the button click event for the planning button
     * <p>
     * Opens the Planning Management Screen
     */
    private void onPlanningButtonClick() {
        //todo : open planning screen

        System.out.println("Open planning screen");
    }

    /**
     * Handles the button click event for the salaray management button
     * <p>
     * Opens the Salary Management Screen
     */
    private void onSalaryManagementButtonClick() {
        //todo : open salaray management screen

        System.out.println("open salaray management screen");
    }

    /**
     * Handles the button click event for the statistics button
     * <p>
     * Opens the Statistics Screen
     */
    private void onStatisticsButtonClick() {
        //todo : open statistics screen

        System.out.println("Open statistics screen");
    }

    /**
     * Handles the button click event for the stocks button
     * <p>
     * Opens the Stock Management Screen
     */
    private void onStocksButtonClick() {
        new ManagementStockScreen().open();
    }

    /**
     * Handles the button click event for the sales button
     * <p>
     * Opens the Sales Screen
     */
    private void onSalesButtonClick() {
        //todo : open sales screen

        System.out.println("Open sales screen");
    }

    /**
     * Handles the button click event for the clients button
     * <p>
     * Opens the Client Management Screen
     */
    private void onClientsButtonClick() {
        new ManagementClientScreen().open();
    }

    /**
     * Handles the button click event for the vendor button
     * <p>
     * Opens the Vendor Management Screen
     */
    private void onVendorButtonClick() {
        new ManagementVendorScreen().open();
    }

    /**
     * Handles the button click event for the products button
     * <p>
     * Opens the Product Management Screen
     */
    private void onProductsButtonClick() {
        new ManagementProductScreen().open();
    }

    private void displayStocks() {
        JFXTreeTableView<ProductStock.ProductStockInfoTree> tree = new JFXTreeTableView<>();
        tree.prefWidthProperty().bind(this.leftPane.widthProperty());
        tree.prefHeightProperty().bind(this.leftPane.heightProperty());

        JFXTreeTableColumn<ProductStock.ProductStockInfoTree, Long> reference = new JFXTreeTableColumn<>("Référence");
        JFXTreeTableColumn<ProductStock.ProductStockInfoTree, Integer> stockQuantity = new JFXTreeTableColumn<>("Quantité");
        JFXTreeTableColumn<ProductStock.ProductStockInfoTree, String> order = new JFXTreeTableColumn<>("ID");

        reference.setSortNode(reference.getSortNode());

        reference.setCellValueFactory(param -> param.getValue().getValue().getReference().asObject());
        stockQuantity.setCellValueFactory(param -> param.getValue().getValue().getStockQuantity().asObject());
        order.setCellValueFactory(param -> param.getValue().getValue().getOrder());

        //noinspection unchecked
        tree.getColumns().setAll(reference, stockQuantity, order);
        tree.setRoot(new RecursiveTreeItem<ProductStock.ProductStockInfoTree>(FXCollections.observableArrayList(), RecursiveTreeObject::getChildren));
        tree.getColumns().forEach(c -> c.setContextMenu(null));
        tree.setShowRoot(false);
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        this.leftPane.getChildren().add(tree);
        this.loadStocksFromCurrentStore(tree);
    }

    private void loadStocksFromCurrentStore(JFXTreeTableView<ProductStock.ProductStockInfoTree> tree) {
        try {
            this.entityManagerProductStock.executeQuery(this.entityManagerProductStock.getQueryBuilder()
                    .where().eq("store_id", this.currentStore.getId())
                    .and().eq("quantity", 0).prepare()).forEach(productStock -> {
                if (productStock.getProduct() != null) {
                    tree.getRoot().getChildren().add(new TreeItem<>(productStock.toProductStockInfoTree()));
                }
            });
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Builds the Home Screen with buttons according to the user's permissions
     */
    public void buildButtonsFromPermissions() {
        if (this.currentUser != null) {
            this.currentUser.getRole().getPermissions().forEach(permission -> {
                FontAwesomeIconView fontAwesomeIconView = null;

                JFXButton newButton = new JFXButton();
                newButton.contentDisplayProperty().set(ContentDisplay.TOP);
                newButton.setPrefWidth(DEFAULT_BUTTON_SIZE);
                newButton.setTextAlignment(TextAlignment.CENTER);
                newButton.setTextFill(Paint.valueOf("WHITE"));
                newButton.setWrapText(true);
                newButton.setFont(Font.font(18.0));
                newButton.setCursor(Cursor.HAND);

                switch (permission) {
                    case ROLE_MANAGEMENT:
                        newButton.setText("Gestion des rôles");
                        newButton.setOnMouseClicked(event -> onRoleManagementButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.PAPERCLIP);
                        break;
                    case STORE_MANAGEMENT:
                        newButton.setText("Gestion des magasins");
                        newButton.setOnMouseClicked(event -> onStoresButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.USER_MD);
                        break;
                    case MONITORING:
                        newButton.setText("Monitoring");
                        newButton.setOnMouseClicked(event -> onMonitoringButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.SIGNAL);
                        break;
                    case STATISTICS:
                        newButton.setText("Statistiques de vente");
                        newButton.setOnMouseClicked(event -> onStatisticsButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.MONEY);
                        break;
                    case USER_MANAGEMENT:
                        newButton.setText("Gestion des utilisateurs");
                        newButton.setOnMouseClicked(event -> onUserManagementButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.USER_MD);
                        break;
                    // Same as statistics ? ##########################################################
                    case SALES_MANAGEMENT:
                        newButton.setText("Gestion des ventes");
                        newButton.setOnMouseClicked(event -> onSalesButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.SELLSY);
                        break;
                    // ###############################################################################
                    case CLIENTS_MANAGEMENT:
                        newButton.setText("Gestion des clients");
                        newButton.setOnMouseClicked(event -> onClientsButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.USERS);
                        break;
                    case SALARY_MANAGEMENT:
                        newButton.setText("Gestion des salaires");
                        newButton.setOnMouseClicked(event -> onSalaryManagementButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.MONEY);
                        break;
                    case STOCKS_MANAGEMENT:
                        newButton.setText("Gestion des stocks");
                        newButton.setOnMouseClicked(event -> onStocksButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.CUBE);
                        this.displayStocks();
                        break;
                    case PRODUCTS_MANAGEMENT:
                        newButton.setText("Gestion des produits");
                        newButton.setOnMouseClicked(event -> onProductsButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.COFFEE);
                        break;
                    case PLANNING_MANAGEMENT:
                        newButton.setText("Gestion des plannings");
                        newButton.setOnMouseClicked(event -> onPlanningButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
                        break;
                    case VENDORS_MANAGEMENT:
                        newButton.setText("Gestion des fournisseurs");
                        newButton.setOnMouseClicked(event -> onVendorButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.CUBE);
                        break;
                }

                fontAwesomeIconView.setSize("30.0px");
                fontAwesomeIconView.setFill(Paint.valueOf("RED"));
                newButton.setGraphic(fontAwesomeIconView);
                newButton.setWrapText(true);
                newButton.setTextAlignment(TextAlignment.CENTER);

                this.verticalButtonsBar.getChildren().add(newButton);
            });
        }
    }
}
