package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.ClientStock;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerClientStock {
    private static final String CLIENT_STOCK_MANAGER_NOT_INITIALIZED = "EntityManagerClientStock could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<ClientStock, Long> instance;

    /**
     * Instantiates a new EntityManagerClientStock
     *
     * @param connectionSource the connection source
     */
    public EntityManagerClientStock(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, ClientStock.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, CLIENT_STOCK_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(CLIENT_STOCK_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a client stock in the database
     *
     * @param clientStock the client stock to be inserted
     */
    public void create(ClientStock clientStock) {
        try {
            this.instance.create(clientStock);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a client stock from the database
     *
     * @param clientStock the client stock to be deleted
     */
    public void delete(ClientStock clientStock) {
        try {
            this.instance.delete(clientStock);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a client stock in the database
     *
     * @param clientStock the client stock to be updated
     */
    public void update(ClientStock clientStock) {
        try {
            this.instance.update(clientStock);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the client stocks in the database
     *
     * @return an iterator over all the client stocks in the database
     */
    public CloseableIterator<ClientStock> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<ClientStock> executeQuery(PreparedQuery<ClientStock> query) {
        List<ClientStock> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return results;
    }

    /**
     * Gets all the client stocks
     *
     * @return all the client stocks that are in the database
     */
    public List<ClientStock> getQueryForAll() {
        List<ClientStock> results = null;

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
     * @return the query builder for the clients
     */
    public QueryBuilder<ClientStock, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if a client stock exists in the database
     *
     * @param clientStock the client stock to be checked
     * @return true if it exists, else false
     */
    public boolean exists(ClientStock clientStock) {
        if (clientStock == null) return false;

        try {
            return this.instance.queryForId(clientStock.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
