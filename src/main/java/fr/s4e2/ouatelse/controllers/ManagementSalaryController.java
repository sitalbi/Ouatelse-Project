package fr.s4e2.ouatelse.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerSalary;
import fr.s4e2.ouatelse.managers.EntityManagerStore;
import fr.s4e2.ouatelse.managers.EntityManagerUser;
import fr.s4e2.ouatelse.objects.Salary;
import fr.s4e2.ouatelse.objects.Salary.SalaryTree;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.objects.User.UserSalaryTree;
import fr.s4e2.ouatelse.utils.JFXUtils;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementSalaryScreen}
 */
public class ManagementSalaryController extends BaseController {

    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String NO_USER_SELECTED_HINT = "Aucun employé selectionné";
    private static final String SALARY_NOT_A_NUMBER = "Un salaire doit être un nombre!";
    private static final String SALARY_NOT_VALID = "Vous avez sans doute inversé le salaire brut et net.";
    private static final String NOT_AN_HOUR = "Les heures par semaine sont incorrectes!";
    private static final String ALREADY_PAYED_THIS_MONTH = "Ce salarié a déjà été payé pour ce mois!";

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
    private JFXDatePicker salaryDateInput;
    @FXML
    private Label errorLabel;
    // TAB HISTORY
    @FXML
    private JFXTreeTableView<SalaryTree> salaryHistoryTreeTableView;
    private User currentUser;
    private Salary currentSalary;

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
        this.salaryDateInput.setConverter(JFXUtils.getDateConverter());
        this.loadEmployeeTreeTable();
        this.loadSalaryTreeTable();

        // selected element in the employee tree table
        this.employeeTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentUser = null;
                return;
            }

            try {
                this.currentUser = this.entityManagerUser.executeQuery(this.entityManagerUser.getQueryBuilder()
                        .where().eq("credentials", newValue.getValue().getId().getValue())
                        .prepare()
                ).stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            this.loadInformation();
        });

        // selected element in the salary tree table
        this.salaryHistoryTreeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.currentSalary = null;
                return;
            }
            try {
                this.currentSalary = this.entityManagerSalary.executeQuery(entityManagerSalary.getQueryBuilder()
                        .where().eq("id", newValue.getValue().getId().getValue())
                        .prepare()
                ).stream().findFirst().orElse(null);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            this.loadSalaryInformation();
        });

        // escape to unselect item in the list box
        this.getBaseBorderPane().setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            TreeItem<UserSalaryTree> userSalaryTree = employeeTreeTableView.getSelectionModel().getSelectedItem();
            if (userSalaryTree == null) return;
            employeeTreeTableView.getSelectionModel().clearSelection();
            this.clearEverything();
        });

        this.entityManagerStore.getQueryForAll().forEach(store -> salaryStoreComboBox.getItems().add(store));

        // store selection listener
        this.salaryStoreComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            employeeTreeTableView.getRoot().getChildren().clear();
            this.clearInformation();

            try {
                this.entityManagerUser.executeQuery(entityManagerUser.getQueryBuilder()
                        .where().eq("workingStore_id", newValue.getId())
                        .prepare()
                ).forEach(user -> employeeTreeTableView.getRoot().getChildren().add(new TreeItem<>(user.toUserSalaryTree())));
            } catch (SQLException exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            }

            this.loadInformation();
        });

        // search employee
        this.employeeSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchUserFromText(newValue.trim()));
    }


    /**
     * Searches a user in the database from its credentials
     *
     * @param input the searched user credentials
     */
    private void searchUserFromText(String input) {
        this.employeeTreeTableView.getRoot().getChildren().clear();

        if (input.isEmpty()) {
            this.loadEmployeeTreeTable();
            return;
        }

        this.clearEverything();

        try {
            List<User> searchResults = entityManagerUser.executeQuery(entityManagerUser.getQueryBuilder()
                    .where().like("credentials", "%" + input + "%")
                    .prepare()
            );
            searchResults.forEach(user -> this.employeeTreeTableView.getRoot().getChildren().add(new TreeItem<>(user.toUserSalaryTree())));
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Handles the button click event for the valid button
     * <p>
     * Sets and edits the salary of a User
     */
    public void onValidButtonClick() {
        if (netSalaryInput.getText().trim().isEmpty() || grossSalaryInput.getText().trim().isEmpty() || hoursPerWeekInput.getText().trim().isEmpty()
                || salaryDateInput.getValue() == null) {
            this.errorLabel.setText(TEXT_FIELD_EMPTY_HINT);
            return;
        }

        // no user is selected
        if (!this.isUserSelected()) {
            this.errorLabel.setText(NO_USER_SELECTED_HINT);
            return;
        }

        // salaries not a number
        Double grossSalary = Utils.getDouble(grossSalaryInput.getText().trim());
        if (grossSalary == null) {
            this.errorLabel.setText(SALARY_NOT_A_NUMBER);
            return;
        }
        Double netSalary = Utils.getDouble(netSalaryInput.getText().trim());
        if (netSalary == null) {
            this.errorLabel.setText(SALARY_NOT_A_NUMBER);
            return;
        }

        // invalid salary order
        if (netSalary > grossSalary) {
            this.errorLabel.setText(SALARY_NOT_VALID);
            return;
        }

        // hours not a number
        Integer hoursPerWeek = Utils.getNumber(hoursPerWeekInput.getText().trim());
        if (hoursPerWeek == null) {
            this.errorLabel.setText(NOT_AN_HOUR);
            return;
        }

        if (this.isSalarySelected()) {
            // edits salary
            this.currentSalary.setUser(currentUser);
            this.currentSalary.setGrossSalary(grossSalary);
            this.currentSalary.setNetSalary(netSalary);
            this.currentSalary.setDate(Utils.localDateToDate(salaryDateInput.getValue()));
            this.entityManagerSalary.create(currentSalary);

            // updates user
            this.currentUser.setHoursPerWeek(hoursPerWeek);
            this.entityManagerUser.update(currentUser);

            // updates salary in the table
            this.addClientToSalaryTable(currentSalary);
            this.salaryHistoryTreeTableView.getRoot().getChildren().remove(salaryHistoryTreeTableView.getSelectionModel().getSelectedItem());
            this.salaryHistoryTreeTableView.getSelectionModel().clearSelection();
        } else {
            // user already payed this month
            Date date = Utils.localDateToDate(salaryDateInput.getValue());
            if (currentUser.getSalarySheets() != null && currentUser.getSalarySheets().stream().anyMatch(s -> s.getDate().getMonth() == date.getMonth())) {
                this.errorLabel.setText(ALREADY_PAYED_THIS_MONTH);
                return;
            }

            // creates salary
            Salary salary = new Salary();
            salary.setUser(currentUser);
            salary.setGrossSalary(grossSalary);
            salary.setNetSalary(netSalary);
            salary.setDate(Utils.localDateToDate(salaryDateInput.getValue()));
            this.entityManagerSalary.create(salary);

            // updates user
            this.currentUser.setHoursPerWeek(hoursPerWeek);
            this.entityManagerUser.update(currentUser);

            // adds created salary to the table
            this.addClientToSalaryTable(salary);
        }

        this.clearInformation();
    }

    /**
     * Clears the salary' information from the differents fields for all tabs
     */
    private void clearInformation() {
        // tab salary
        this.netSalaryInput.setText("");
        this.grossSalaryInput.setText("");
        this.hoursPerWeekInput.setText("");
        this.salaryDateInput.getEditor().clear();
        this.errorLabel.setText("");
    }

    private void clearEverything() {
        this.clearInformation();
        this.salaryHistoryTreeTableView.getRoot().getChildren().clear();
    }

    /**
     * Loads the user's information into the fields
     */
    private void loadInformation() {
        this.clearInformation();
        if (!this.isUserSelected()) return;
        if (currentUser.getSalarySheets() == null || currentUser.getSalarySheets().isEmpty()) return;

        this.currentUser.getSalarySheets().forEach(salary -> salaryHistoryTreeTableView.getRoot().getChildren().add(new TreeItem<>(salary.toUserSalaryTree())));
    }

    /**
     * Loads the salary's information into the fields
     */
    private void loadSalaryInformation() {
        if (!this.isSalarySelected()) return;

        this.netSalaryInput.setText(String.valueOf(currentSalary.getNetSalary()));
        this.grossSalaryInput.setText(String.valueOf(currentSalary.getGrossSalary()));
        this.hoursPerWeekInput.setText(String.valueOf(currentSalary.getUser().getHoursPerWeek()));
        this.salaryDateInput.setValue(Utils.dateToLocalDate(currentSalary.getDate()));
        this.salaryDateInput.getEditor().setText(salaryDateInput.getConverter().toString(salaryDateInput.getValue()));
    }

    /**
     * Loads the list of employees into the table
     */
    private void loadEmployeeTreeTable() {
        JFXTreeTableColumn<User.UserSalaryTree, String> id = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<User.UserSalaryTree, String> role = new JFXTreeTableColumn<>("Rôle");
        id.setSortNode(id.getSortNode());

        id.setCellValueFactory(param -> param.getValue().getValue().getId());
        role.setCellValueFactory(param -> param.getValue().getValue().getRole());

        ObservableList<User.UserSalaryTree> users = FXCollections.observableArrayList();
        TreeItem<User.UserSalaryTree> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.employeeTreeTableView.getColumns().setAll(id, role);
        this.employeeTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.employeeTreeTableView.setRoot(root);
        this.employeeTreeTableView.setShowRoot(false);
    }

    /**
     * Loads the history of salary of the selected employee into the table
     */
    private void loadSalaryTreeTable() {
        JFXTreeTableColumn<SalaryTree, String> date = new JFXTreeTableColumn<>("Date");
        JFXTreeTableColumn<SalaryTree, String> credentials = new JFXTreeTableColumn<>("Identifiant");
        JFXTreeTableColumn<SalaryTree, String> role = new JFXTreeTableColumn<>("Rôle");
        JFXTreeTableColumn<SalaryTree, Integer> hoursPerWeek = new JFXTreeTableColumn<>("Heures/Semaine");
        JFXTreeTableColumn<SalaryTree, Double> grossSalary = new JFXTreeTableColumn<>("Salaire Brut");
        JFXTreeTableColumn<SalaryTree, Double> netSalary = new JFXTreeTableColumn<>("Salaire Net");

        date.setCellValueFactory(param -> param.getValue().getValue().getDate());
        credentials.setCellValueFactory(param -> param.getValue().getValue().getCredentials());
        role.setCellValueFactory(param -> param.getValue().getValue().getRole());
        hoursPerWeek.setCellValueFactory(param -> param.getValue().getValue().getHoursPerWeek().asObject());
        grossSalary.setCellValueFactory(param -> param.getValue().getValue().getGrossSalary().asObject());
        netSalary.setCellValueFactory(param -> param.getValue().getValue().getNetSalary().asObject());


        ObservableList<SalaryTree> salaries = FXCollections.observableArrayList();
        TreeItem<SalaryTree> root = new RecursiveTreeItem<>(salaries, RecursiveTreeObject::getChildren);
        //noinspection unchecked
        this.salaryHistoryTreeTableView.getColumns().setAll(date, credentials, role, hoursPerWeek, grossSalary, netSalary);
        this.salaryHistoryTreeTableView.getColumns().forEach(c -> c.setContextMenu(null));
        this.salaryHistoryTreeTableView.setRoot(root);
        this.salaryHistoryTreeTableView.setShowRoot(false);
    }

    /**
     * Adds a Salary to the tree table in the history tab
     *
     * @param salary salary to add to the table
     */
    private void addClientToSalaryTable(Salary salary) {
        this.salaryHistoryTreeTableView.getRoot().getChildren().add(new TreeItem<>(salary.toUserSalaryTree()));
    }

    /**
     * Checks if a salary is selected in the table at the top of the window
     *
     * @return true if a salary is selected, else false
     */
    private boolean isSalarySelected() {
        return this.currentSalary != null;
    }

    /**
     * Checks if a user is selected in the table at the top of the window
     *
     * @return true if a user is selected, else false
     */
    private boolean isUserSelected() {
        return this.currentUser != null;
    }
}
