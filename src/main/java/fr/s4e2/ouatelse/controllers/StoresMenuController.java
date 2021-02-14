package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Store;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StoresMenuController implements Initializable {
    private static final String TEXT_FIELD_HINT = "Veuillez saisir un nom";
    private static final String STORE_ALREADY_EXISTS = "Ce magasin existe déjà!";

    public ListView<Store> storesListView;
    public TextField newStoreNameField;
    private Dao<Store, String> storeDao;
    private Store currentStore;

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
        try {
            this.storeDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Store.class);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.newStoreNameField.setPromptText(TEXT_FIELD_HINT);
        this.storesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Store>() {
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
            public void changed(ObservableValue<? extends Store> observable, Store oldValue, Store newValue) {
                if (currentStore != null) {
                    currentStore = newValue;

                    loadStoreInformations(newValue);
                } else {
                    currentStore = null;

                    clearStoreInformations();
                }
            }
        });

        this.loadStoresList();
    }

    /**
     * Creates and stores a new role with empty permissions
     *
     * @param mouseEvent The mouse click event
     */
    public void onAddButtonClick(MouseEvent mouseEvent) {
        if (newStoreNameField.getText().trim().isEmpty()) {
            this.newStoreNameField.setPromptText(TEXT_FIELD_HINT);
            this.newStoreNameField.getParent().requestFocus();
            return;
        }
        for (Store store : this.storeDao) {
            if (store.getId().equals(this.newStoreNameField.getText().trim())) {
                this.newStoreNameField.clear();
                this.newStoreNameField.setPromptText(STORE_ALREADY_EXISTS);
                this.newStoreNameField.getParent().requestFocus();
                return;
            }
        }

        Store newStore = null;
        try {
            newStore = new Store(newStoreNameField.getText());
            this.storeDao.create(newStore);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.loadStoresList();
        this.newStoreNameField.setText("");
        this.storesListView.getSelectionModel().select(newStore);
    }


    /**
     * Load the selected store's informations into the editable fields
     *
     * @param store The store to view / edit the informations from
     */
    private void loadStoreInformations(Store store) {

    }

    /**
     * Clears the editable fields from a store's informations
     */
    private void clearStoreInformations() {

    }

    /**
     * Loads the stores into the ListView storesListView
     */
    private void loadStoresList() {
        this.storesListView.getItems().clear();
        this.storeDao.forEach(store -> this.storesListView.getItems().add(store));
    }
}
