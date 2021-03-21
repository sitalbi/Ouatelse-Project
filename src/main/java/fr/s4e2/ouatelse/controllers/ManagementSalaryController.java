package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerSalary;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.managers.EntityManagerUser;
import fr.s4e2.ouatelse.objects.*;
import fr.s4e2.ouatelse.objects.User.UserSalaryTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementSalaryScreen}
 */
public class ManagementSalaryController extends BaseController {

    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String NO_USER_SELECTED_HINT = "Aucun employé selectionné";

    private final EntityManagerStore entityManagerStore = Main.getDatabaseManager().getEntityManagerStore();
    private final EntityManagerUser entityManagerUser = Main.getDatabaseManager().getEntityManagerUser();
    private final EntityManagerSalary entityManagerSalary = Main.getDatabaseManager().getEntityManagerSalary();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    // TOP
    @FXML
    private JFXTextField employeeSearchBar;
    @FXML
    private JFXTreeTableView<UserSalaryTree> employeeTreeTableView;
    // TAB SALARY
    @FXML
    private JFXComboBox<Store> salaryStoreComboBox;
    @FXML
    private JFXTextField netSalaryInput;
    @FXML
    private JFXTextField grossSalaryInput;
    @FXML
    private JFXTextField hoursPerWeekInput;
    @FXML
    private Label errorLabel;
    // TAB HISTORY
    @FXML
    private JFXTreeTableView salaryHistoryTreeTableView;
    private User currentUser;
    private Salary currentUserSalary;

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

        this.errorLabel.setText("");

        try {
            this.loadEmployeeTreeTable();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }


        // selected element in the list box
        this.employeeTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    this.currentUser = this.entityManagerUser.executeQuery(this.entityManagerUser.getQueryBuilder()
                            .where().eq("credentials", newValue.getValue().getId().getValue())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                try {
                    this.currentUserSalary = this.entityManagerSalary.executeQuery(this.entityManagerSalary.getQueryBuilder()
                            .where().eq("user_id", currentUser.getId())
                            .prepare()
                    ).stream().findFirst().orElse(null);
                    if(currentUserSalary != null) {
                        this.netSalaryInput.setText(String.valueOf(currentUserSalary.getGrossSalary() - currentUserSalary.getEmployerCharges()));
                        this.grossSalaryInput.setText(String.valueOf(currentUserSalary.getGrossSalary()));
                        this.hoursPerWeekInput.setText(String.valueOf(currentUser.getHoursPerWeek()));
                    }

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                this.loadInformation();
            } else {
                this.currentUser = null;
            }
        });

        // escape to unselect item in the list box
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            TreeItem<UserSalaryTree> userSalaryTree = employeeTreeTableView.getSelectionModel().getSelectedItem();
            if (userSalaryTree == null) return;
            employeeTreeTableView.getSelectionModel().clearSelection();
            this.clearInformation();
        });

        this.entityManagerStore.getQueryForAll().forEach(store -> salaryStoreComboBox.getItems().add(store));


    }

    /**
     * Handles the button click event for the valid button
     * <p>
     * Sets and edits the salary of a User
     */
    public void onValidButtonClick() {
        if (netSalaryInput.getText().trim().isEmpty() || grossSalaryInput.getText().trim().isEmpty() || hoursPerWeekInput.getText().trim().isEmpty()) {
            this.errorLabel.setText(TEXT_FIELD_EMPTY_HINT);
            return;
        }
        this.errorLabel.setText("");

        // If no user are selected
        if (!this.isSelected()) { this.errorLabel.setText(NO_USER_SELECTED_HINT); }

        if (currentUserSalary == null) {
            currentUserSalary = new Salary();
            this.currentUserSalary.setUser(currentUser);
            this.currentUserSalary.setGrossSalary(Double.parseDouble(grossSalaryInput.getText().trim()));
            this.currentUserSalary.setEmployerCharges(Double.parseDouble(grossSalaryInput.getText().trim()) - Double.parseDouble(netSalaryInput.getText().trim()));
            this.currentUser.setHoursPerWeek(Integer.parseInt(hoursPerWeekInput.getText().trim()));

            // update product
            this.entityManagerSalary.create(currentUserSalary);
            this.entityManagerUser.update(currentUser);
        } else {
            this.currentUserSalary.setUser(currentUser);
            this.currentUserSalary.setGrossSalary(Double.parseDouble(grossSalaryInput.getText().trim()));
            this.currentUserSalary.setEmployerCharges(Double.parseDouble(netSalaryInput.getText().trim())-Double.parseDouble(grossSalaryInput.getText().trim()));
            this.currentUser.setHoursPerWeek(Integer.parseInt(hoursPerWeekInput.getText().trim()));

            // update user and salary
            this.entityManagerSalary.update(this.currentUserSalary);
            this.entityManagerUser.update(this.currentUser);
        }

    }

    /**
     * Clears the salary' information from the differents fields for all tabs
     */
    private void clearInformation() {
        // tab salary
        this.netSalaryInput.setText("");
        this.grossSalaryInput.setText("");
        this.hoursPerWeekInput.setText("");
        this.errorLabel.setText("");

        // tab history
        //this.salaryHistoryTreeTableView.getRoot().getChildren().remove(0, salaryHistoryTreeTableView.getRoot().getChildren().size());
    }

    /**
     * Loads the salary's information into the fields
     */
    private void loadInformation() {
        this.clearInformation();
        if (!this.isSelected()) return;
        if(this.currentUserSalary != null) {
            this.netSalaryInput.setText(String.valueOf(currentUserSalary.getGrossSalary() - currentUserSalary.getEmployerCharges()));
            this.grossSalaryInput.setText(String.valueOf(currentUserSalary.getGrossSalary()));
            this.hoursPerWeekInput.setText(String.valueOf(currentUser.getHoursPerWeek()));
        }
    }

    /**
     * Loads the list of employees into the table
     */
    private void loadEmployeeTreeTable() throws SQLException {
        JFXTreeTableColumn<User.UserSalaryTree, String> id = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<User.UserSalaryTree, String> lastName = new JFXTreeTableColumn<>("Nom");
        JFXTreeTableColumn<User.UserSalaryTree, String> firstName = new JFXTreeTableColumn<>("Prénom");
        JFXTreeTableColumn<User.UserSalaryTree, String> role = new JFXTreeTableColumn<>("Rôle");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId());
        lastName.setCellValueFactory(param -> param.getValue().getValue().getLastName());
        firstName.setCellValueFactory(param -> param.getValue().getValue().getFirstName());
        role.setCellValueFactory(param -> param.getValue().getValue().getRole());

        id.setContextMenu(null);
        lastName.setContextMenu(null);
        firstName.setContextMenu(null);
        role.setContextMenu(null);


        ObservableList<User.UserSalaryTree> users = FXCollections.observableArrayList();
        this.salaryStoreComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //remove all the user when changing the store selected
            employeeTreeTableView.getRoot().getChildren().remove(0, employeeTreeTableView.getRoot().getChildren().size());
            try {
                this.clearInformation();
                if (salaryStoreComboBox.getValue() != null) {
                    this.entityManagerUser.executeQuery(this.entityManagerUser.getQueryBuilder()
                            .where().eq("workingStore_id", salaryStoreComboBox.getValue().getId())
                            .prepare()
                    ).forEach(user -> users.add(user.toUserSalaryTree()));
                }
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
            loadInformation();
        });

        TreeItem<User.UserSalaryTree> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.employeeTreeTableView.getColumns().setAll(id, lastName, firstName, role);
        this.employeeTreeTableView.setRoot(root);
        this.employeeTreeTableView.setShowRoot(false);
    }

    /**
     * Loads the history of salary of the selected employee into the table
     */
    private void loadHistorySalaryTreeTable() {

    }

    /**
     * Checks if a user is selected in the table at the top of the window
     *
     * @return true if a user is selected, else false
     */
    private boolean isSelected() {
        return this.currentUser != null;
    }
}
