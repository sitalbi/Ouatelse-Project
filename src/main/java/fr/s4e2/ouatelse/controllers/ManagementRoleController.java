package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.databaseInterface.databaseRoleInterface;
import fr.s4e2.ouatelse.objects.Permission;
import fr.s4e2.ouatelse.objects.Role;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ManagementRoleController extends BaseController {

    private static final String TEXT_FIELD_HINT = "Veuillez saisir un nom";
    private static final String ROLE_ALREADY_EXISTS = "Ce rôle existe déjà!";
    public ListView<Permission> permissionsRoleHas;
    public ListView<Permission> permissionsRoleHasnt;
    public TextField newRoleNameField;
    public Button addPermissionButton;
    public Button deletePermissionButton;
    @FXML
    private ListView<Role> rolesListView;
    private Role currentRole = null;

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

        this.addPermissionButton.setDisable(true);
        this.deletePermissionButton.setDisable(true);
        this.permissionsRoleHas.setDisable(true);
        this.permissionsRoleHasnt.setDisable(true);

        this.newRoleNameField.setPromptText(TEXT_FIELD_HINT);

        // Enables or disables button on select and unselect
        this.rolesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Role>() {
            /**
             * This method needs to be provided by an implementation of
             * {@code ChangeListener}. It is called if the value of an
             * {@link ObservableValue} changes.
             * <p>
             * In general is is considered bad practice to modify the observed value in
             * this method.
             *
             * @param observable The {@code ObservableValue} which value changed
             * @param oldValue   The old value
             * @param newValue   The new value
             */
            @Override
            public void changed(ObservableValue<? extends Role> observable, Role oldValue, Role newValue) {
                if (newValue != null) {
                    currentRole = newValue;

                    permissionsRoleHas.setDisable(false);
                    permissionsRoleHasnt.setDisable(false);

                    loadPermissionLists(newValue);
                } else {
                    currentRole = null;

                    permissionsRoleHas.setDisable(true);
                    permissionsRoleHasnt.setDisable(true);
                }
            }
        });

        this.rolesListView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            Role role = rolesListView.getSelectionModel().getSelectedItem();
            if (role == null) return;
            rolesListView.getSelectionModel().clearSelection();
            currentRole = null;

            permissionsRoleHas.getItems().clear();
            permissionsRoleHasnt.getItems().clear();

            permissionsRoleHas.setDisable(true);
            permissionsRoleHasnt.setDisable(true);
        });
        this.permissionsRoleHas.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            Permission permission = permissionsRoleHas.getSelectionModel().getSelectedItem();
            if (permission == null) return;
            permissionsRoleHas.getSelectionModel().clearSelection();
        });
        this.permissionsRoleHasnt.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            Permission permission = permissionsRoleHasnt.getSelectionModel().getSelectedItem();
            if (permission == null) return;
            permissionsRoleHasnt.getSelectionModel().clearSelection();
        });

        ChangeListener<Permission> changeListener = new ChangeListener<Permission>() {
            /**
             * This method needs to be provided by an implementation of
             * {@code ChangeListener}. It is called if the value of an
             * {@link ObservableValue} changes.
             * <p>
             * In general is is considered bad practice to modify the observed value in
             * this method.
             *
             * @param observable The {@code ObservableValue} which value changed
             * @param oldValue   The old value
             * @param newValue   The new value
             */
            @Override
            public void changed(ObservableValue<? extends Permission> observable, Permission oldValue, Permission newValue) {
                if (newValue != null) {
                    addPermissionButton.setDisable(false);
                    deletePermissionButton.setDisable(false);
                } else {
                    addPermissionButton.setDisable(true);
                    deletePermissionButton.setDisable(true);
                }
            }
        };

        this.permissionsRoleHas.getSelectionModel().selectedItemProperty().addListener(changeListener);
        this.permissionsRoleHasnt.getSelectionModel().selectedItemProperty().addListener(changeListener);

        this.loadRoleList();
    }

    /**
     * Creates and stores a new role with empty permissions
     *
     * @param mouseEvent The mouse click event
     */
    @FXML
    private void onAddButtonClick(MouseEvent mouseEvent) {
        // Displays a hint if the conditions aren't met
        if (newRoleNameField.getText().trim().isEmpty()) {
            this.newRoleNameField.setPromptText(TEXT_FIELD_HINT);
            this.newRoleNameField.getParent().requestFocus();
            return;
        }
        for (Role role : databaseRoleInterface.getInstance()) {
            if (role.getName().equals(newRoleNameField.getText().trim())) {
                this.newRoleNameField.clear();
                this.newRoleNameField.setPromptText(ROLE_ALREADY_EXISTS);
                this.newRoleNameField.getParent().requestFocus();
                return;
            }
        }

        Role newRole = databaseRoleInterface.createNewRole(newRoleNameField.getText().trim());

        this.newRoleNameField.setText("");
        this.newRoleNameField.setPromptText("Veuillez saisir un nom");

        this.rolesListView.getItems().add(newRole);
        this.rolesListView.getSelectionModel().select(newRole);
        this.newRoleNameField.getParent().requestFocus();
    }

    /**
     * Add a permission to a role and saves the modifications in the database
     *
     * @param mouseEvent The mouse click event
     */
    public void onAddPermissionButtonClick(MouseEvent mouseEvent) {
        if (currentRole == null) return;

        Permission selectedPermission = permissionsRoleHasnt.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;
        this.currentRole.getPermissions().add(selectedPermission);

        // Saves role state and disables buttons
        loadPermissionLists(currentRole);
        this.saveRole(currentRole);
        this.deletePermissionButton.setDisable(true);
        this.addPermissionButton.setDisable(true);
    }

    /**
     * Remove a permission to a role and saves the modifications in the database
     *
     * @param mouseEvent The mouse click event
     */
    public void onDeletePermissionButtonClick(MouseEvent mouseEvent) {
        if (currentRole == null) return;

        Permission selectedPermission = permissionsRoleHas.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;
        this.currentRole.getPermissions().remove(selectedPermission);

        // Saves role state and disables buttons
        loadPermissionLists(currentRole);
        this.saveRole(currentRole);
        this.deletePermissionButton.setDisable(true);
        this.addPermissionButton.setDisable(true);
    }

    /**
     * Deletes a role
     *
     * @param mouseEvent The mouse click event
     */
    public void onDeleteButtonClick(MouseEvent mouseEvent) {
        databaseRoleInterface.deleteRole(rolesListView.getSelectionModel().getSelectedItem());

        this.loadRoleList();
        this.clearPermissionLists();
    }

    /**
     * Load the roles into the ListView rolesListView
     */
    private void loadRoleList() {
        this.rolesListView.getItems().clear();
        databaseRoleInterface.getInstance().forEach(role -> rolesListView.getItems().add(role));
    }

    /**
     * Loads the permissions the role has in the ListView permissionsRoleHas and the permissions the role hasn't in
     * the ListView permissionsRoleHasnt
     *
     * @param role The selected role
     */
    private void loadPermissionLists(Role role) {
        this.clearPermissionLists();
        role.getPermissions().forEach(permission -> permissionsRoleHas.getItems().add(permission));
        Stream.of(Permission.values()).forEachOrdered(permission -> {
            if (!role.getPermissions().contains(permission)) {
                this.permissionsRoleHasnt.getItems().add(permission);
            }
        });
    }

    /**
     * Clears the ListView permissionsRoleHas and permissionsRoleHasnt
     */
    private void clearPermissionLists() {
        this.permissionsRoleHas.getItems().clear();
        this.permissionsRoleHasnt.getItems().clear();
    }

    /**
     * Saves changes on the role entity
     *
     * @param role a chosen role
     */
    private void saveRole(Role role) {
        databaseRoleInterface.updateRole(role);
    }
}