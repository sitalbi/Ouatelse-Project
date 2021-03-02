package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.AuthUserScreen;
import fr.s4e2.ouatelse.screens.ManagementRoleScreen;
import fr.s4e2.ouatelse.screens.ManagementStoreScreen;
import fr.s4e2.ouatelse.screens.ManagementUserScreen;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller of the the home menu
 */
public class HomeController extends BaseController {

    private static final double DEFAULT_BUTTON_SIZE = 1000;
    private static final double MINIMUM_BUTTON_HEIGHT = 74;

    public VBox verticalButtonsBar;
    public Label roleField;
    public ScrollPane scrollPanel;
    private User currentUser;
    @Setter
    private Store currentStore;
    @FXML
    private Label homeAdminName;

    @FXML
    private Label homeAdminEmail;

    /**
     * Initializes the controller
     * @param location {@inheritDoc}
     * @param resources {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    /**
     * Sets user's datas on the application
     * @param user the user of the application
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
     * Handles the disconnection button
     */
    public void onDisconnectClick() {
        this.currentUser = null;
        Stage stage = (Stage) this.homeAdminName.getScene().getWindow();
        stage.close();

        new AuthUserScreen().open();
    }

    /**
     * Opens an user management screen
     */
    private void onUserManagementButtonClick() { new ManagementUserScreen().open(); }

    /**
     * Opens a role management screen
     */
    private void onRoleManagementButtonClick() {
        new ManagementRoleScreen().open();
    }

    /**
     * Opens a store management screen
     */
    private void onStoresButtonClick() {
        new ManagementStoreScreen().open();
    }

    /**
     * Opens a monitoring screen
     */
    private void onMonitoringButtonClick() {
        //todo : open monitoring screen

        System.out.println("Open monitoring screen");
    }

    /**
     * Opens an employee management screen
     */
    private void onEmployeeManagementButtonClick() {
        //todo : open employee management screen

        System.out.println("Open employee management screen");
    }

    /**
     * Opens a planning screen
     */
    private void onPlanningButtonClick() {
        //todo : open planning screen

        System.out.println("Open planning screen");
    }

    /**
     * Opens a salary management screen
     */
    private void onSalaryManagementButtonClick() {
        //todo : open salaray management screen

        System.out.println("open salaray management screen");
    }

    /**
     * Opens a statistics screen
     */
    private void onStatisticsButtonClick() {
        //todo : open statistics screen

        System.out.println("Open statistics screen");
    }

    /**
     * Opens a stocks management screen
     */
    private void onStocksButtonClick() {
        //todo : open stocks screen

        System.out.println("Open stocks screen");
    }

    /**
     * Opens a sales screen
     */
    private void onSalesButtonClick() {
        //todo : open sales screen

        System.out.println("Open sales screen");
    }

    /**
     * Opens a clients management screen
     */
    private void onClientsButtonClick() {
        //todo : open monitoring screen

        System.out.println("Open monitoring screen");
    }

    /**
     * Opens a products management screen
     */
    private void onProductsButtonClick() {
        //todo : open parameters screen

        System.out.println("Open parameters screen");
    }

    /**
     * Builds buttons for user's permissions
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
                        newButton.setText("TODO");
                        newButton.setOnMouseClicked(event -> onSalesButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.USER_MD);
                        break;
                    // ###############################################################################
                    case CLIENTS_MANAGEMENT:
                        newButton.setText("Gestion des clients");
                        newButton.setOnMouseClicked(event -> onClientsButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.USERS);
                        break;
                    case EMPLOYEES_MANAGEMENT:
                        newButton.setText("Gestion des employés");
                        newButton.setOnMouseClicked(event -> onEmployeeManagementButtonClick());
                        fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.WRENCH);
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
