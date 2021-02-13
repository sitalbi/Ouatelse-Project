package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Permission;
import fr.s4e2.ouatelse.objects.Role;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class RolesMenuController implements Initializable {

    private final static String NAME_ERROR_MESSAGE = "Error : Enter a name";
    public ListView<Permission> permissionsRoleHas;
    public ListView<Permission> permissionsRoleHasnt;
    public TextField newRoleNameField;
    public Button addPermissionButton;
    public Button deletePermissionButton;
    @FXML
    private ListView<Role> rolesListView;
    private Dao<Role, Long> roleDao;
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
        this.addPermissionButton.setDisable(true);
        this.deletePermissionButton.setDisable(true);
        this.permissionsRoleHas.setDisable(true);
        this.permissionsRoleHasnt.setDisable(true);

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
             * @param newValue
             */
            @Override
            public void changed(ObservableValue<? extends Role> observable, Role oldValue, Role newValue) {
                if (newValue != null) {
                    currentRole = newValue;

                    addPermissionButton.setDisable(false);
                    deletePermissionButton.setDisable(false);
                    permissionsRoleHas.setDisable(false);
                    permissionsRoleHasnt.setDisable(false);

                    loadPermissionLists(newValue);
                } else {
                    currentRole = null;

                    addPermissionButton.setDisable(true);
                    deletePermissionButton.setDisable(true);
                    permissionsRoleHas.setDisable(true);
                    permissionsRoleHasnt.setDisable(true);
                }
            }
        });
        this.loadRoleList();
    }

    @FXML
    private void onAddButtonClick(MouseEvent mouseEvent) {
        if (newRoleNameField.getText().isEmpty() && !newRoleNameField.getText().equals(NAME_ERROR_MESSAGE)) {
            newRoleNameField.setText(NAME_ERROR_MESSAGE);
            return;
        }

        Role newRole = null;
        try {
            newRole = new Role(newRoleNameField.getText());
            this.roleDao.create(newRole);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.loadRoleList();
        this.rolesListView.getSelectionModel().select(newRole);
    }

    public void onDoneButtonClick(MouseEvent mouseEvent) {
        Stage stage = (Stage) this.deletePermissionButton.getScene().getWindow();
        stage.close();
    }

    public void onAddPermissionButtonClick(MouseEvent mouseEvent) {
        if (currentRole == null) return;

        Permission selectedPermission = this.permissionsRoleHasnt.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;
        currentRole.getPermissions().add(selectedPermission);

        loadPermissionLists(currentRole);
        this.saveRole(currentRole);
    }

    public void onDeletePermissionButtonClick(MouseEvent mouseEvent) {
        if (currentRole == null) return;

        Permission selectedPermission = this.permissionsRoleHas.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;
        currentRole.getPermissions().remove(selectedPermission);

        loadPermissionLists(currentRole);
        this.saveRole(currentRole);
    }

    public void onDeleteButtonClick(MouseEvent mouseEvent) {
        try {
            Role selectedRole = this.rolesListView.getSelectionModel().getSelectedItem();
            this.roleDao.delete(selectedRole);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        this.loadRoleList();
        this.clearPermissionLists();
    }

    private void loadRoleList() {
        this.rolesListView.getItems().clear();
        try {
            this.roleDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Role.class);
            this.roleDao.forEach(role -> rolesListView.getItems().add(role));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void loadPermissionLists(Role role) {
        this.clearPermissionLists();
        role.getPermissions().forEach(permission -> this.permissionsRoleHas.getItems().add(permission));
        Stream.of(Permission.values()).forEachOrdered(permission -> {
            if (!role.getPermissions().contains(permission)) {
                this.permissionsRoleHasnt.getItems().add(permission);
            }
        });
    }

    private void clearPermissionLists() {
        this.permissionsRoleHas.getItems().clear();
        this.permissionsRoleHasnt.getItems().clear();
    }

    private void saveRole(Role role) {
        try {
            this.roleDao.update(role);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
