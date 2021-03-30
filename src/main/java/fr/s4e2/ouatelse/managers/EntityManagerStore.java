package fr.s4e2.ouatelse.managers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Store;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerStore type
 */
public class EntityManagerStore {
    private static final String STORE_MANAGER_NOT_INITIALIZED = "EntityManagerStore could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Store, String> instance;

    /**
     * Instantiates a new EntityManagerStore
     *
     * @param connectionSource the connection source
     */
    public EntityManagerStore(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Store.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, STORE_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(STORE_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a store in the database
     *
     * @param store the store to insert
     */
    public void create(Store store) {
        try {
            this.instance.create(store);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a store from the database
     *
     * @param store the store to delete
     */
    public void delete(Store store) {
        try {
            this.instance.delete(store);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Update a store from the database
     *
     * @param store the store to update
     */
    public void update(Store store) {
        try {
            this.instance.update(store);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all stores in the database
     *
     * @return an iterator over all the stores in the database
     */
    public CloseableIterator<Store> getAll() {
        return this.instance.iterator();
    }

    /**
     * Executes a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Store> executeQuery(PreparedQuery<Store> query) {
        List<Store> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the stores
     *
     * @return all the stores that are in the database
     */
    public List<Store> getQueryForAll() {
        List<Store> results = null;

        try {
            results = this.instance.queryForAll();
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets a query builder
     *
     * @return the query builder for the stores
     */
    public QueryBuilder<Store, String> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets store if exists from ID and password, else null
     *
     * @param id       the id of the store
     * @param password the password of the store in plain text
     * @return the store if exists, else null
     */
    public Store authGetStoreIfExists(String id, String password) {
        Store store = null;

        try {
            //noinspection UnstableApiUsage
            store = this.instance.query(this.instance.queryBuilder().where()
                    .eq("id", id)
                    .and().eq("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
                    ).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return store;
    }

    /**
     * Gets store if exists from ID only, else null
     *
     * @param id The store's ID
     * @return the store object, else null
     */
    public Store getStoreIfExist(String id) {
        Store store = null;

        try {
            store = this.instance.query(this.instance.queryBuilder().where()
                    .eq("id", id)
                    .prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return store;
    }

    /**
     * Check if an address exists in the database
     *
     * @param store the store to check
     * @return true if it exists, false if not
     */
    public boolean exists(Store store) {
        try {
            return this.instance.queryForId(store.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
