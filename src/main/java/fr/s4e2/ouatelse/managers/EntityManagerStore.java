package fr.s4e2.ouatelse.managers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.objects.Store;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * The type EntityManagerStore type
 */
public class EntityManagerStore {

    private final ConnectionSource connectionSource;
    private Dao<Store, String> instance;

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
            exception.printStackTrace();
            System.exit(0);
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
        }
    }

    /**
     * Gets all stores in the database
     *
     * @return all the stores in the database
     */
    public Dao<Store, String> getAll() {
        return this.instance;
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
     * Gets store if exists, else null
     *
     * @param id       the id of the store
     * @param password the password of the store in plain text
     * @return the store if exists, else null
     */
    public Store getStoreIfExists(String id, String password) {
        Store store = null;

        try {
            //noinspection UnstableApiUsage
            store = this.instance.query(this.instance.queryBuilder().where()
                    .eq("id", id)
                    .and().eq("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
                    ).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return store;
    }
}
