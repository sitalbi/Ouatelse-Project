package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.screens.ManagementRoleScreen;
import fr.s4e2.ouatelse.screens.ManagementStoreScreen;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HomeController extends BaseController {

    private static final double DEFAULT_BUTTON_SIZE = 1000;

    public VBox verticalButtonsBar;
    public Label roleField;
    private User currentUser;
    @FXML
    private Label homeAdminName;

    @FXML
    private Label homeAdminEmail;
    private Dao<User, Long> userDao;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // TODO : Used for debugging, remove in production ###################################################
        try {
            this.userDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), User.class);
            this.setCurrentUser(this.userDao.query(this.userDao.queryBuilder().prepare()).get(0));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        // ###################################################################################################

        this.homeAdminName.setText(this.currentUser.getName() + " " + this.currentUser.getSurname());
        this.homeAdminEmail.setText(this.currentUser.getEmail());
        this.roleField.setText(this.currentUser.getRole().toString());
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.buildButtonsFromPermissions();
    }

    public void onDisconnectClick() {
        //todo : handle disconnect

        Stage stage = (Stage) this.homeAdminName.getScene().getWindow();
        stage.close();
    }

    private void onUserManagementButtonClick() {
        //todo : open user management screen

        System.out.println("Open user management screen");
    }

    private void onRoleManagementButtonClick() {
        System.out.println("Open role management screen");
        new ManagementRoleScreen().open();
    }

    private void onParametersButtonClick() {
        //todo : open parameters screen

        System.out.println("Open parameters screen");
    }

    private void onStoresButtonClick() {
        System.out.println("Open stores screen");
        new ManagementStoreScreen().open();
    }

    private void onMonitoringButtonClick() {
        //todo : open monitoring screen

        System.out.println("Open monitoring screen");
    }

    private void onEmployeeManagementButtonClick() {
        //todo : open employee management screen

        System.out.println("Open employee management screen");
    }

    private void onPlanningButtonClick() {
        //todo : open planning screen

        System.out.println("Open planning screen");
    }

    private void onSalaryManagementButtonClick() {
        //todo : open salaray management screen

        System.out.println("open salaray management screen");
    }

    private void onStatisticsButtonClick() {
        //todo : open statistics screen

        System.out.println("Open statistics screen");
    }

    private void onStocksButtonClick() {
        //todo : open stocks screen

        System.out.println("Open stocks screen");
    }

    private void onSalesButtonClick() {
        //todo : open sales screen

        System.out.println("Open sales screen");
    }

    private void onClientsButtonClick() {
        //todo : open monitoring screen

        System.out.println("Open monitoring screen");
    }

    private void onProductsButtonClick() {
        //todo : open parameters screen

        System.out.println("Open parameters screen");
    }

    public void buildButtonsFromPermissions() {
        if (this.currentUser != null) {
            this.currentUser.getRole().getPermissions().forEach(permission -> {
                FontAwesomeIconView fontAwesomeIconView = null;

                JFXButton newButton = new JFXButton();
                newButton.contentDisplayProperty().set(ContentDisplay.TOP);
                newButton.setPrefHeight(DEFAULT_BUTTON_SIZE);
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
