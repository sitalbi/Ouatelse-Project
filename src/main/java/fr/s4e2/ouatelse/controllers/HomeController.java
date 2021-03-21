package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.*;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.Setter;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.HomeScreen}
 */
public class HomeController extends BaseController {

    private static final double DEFAULT_BUTTON_SIZE = 1000;
    private static final double MINIMUM_BUTTON_HEIGHT = 74;

    private User currentUser;
    @Setter
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
        new ManagementSalaryScreen().open();
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
