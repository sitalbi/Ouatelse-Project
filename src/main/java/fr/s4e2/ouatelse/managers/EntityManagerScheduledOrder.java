package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.ScheduledOrder;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerScheduledOrder {
    private static final String SCHEDULED_ORDER_MANAGER_NOT_INITIALIZED = "EntityManagerScheduledOrder could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<ScheduledOrder, Long> instance;

    /**
     * Instantiates a new EntityManagerStore
     *
     * @param connectionSource the connection source
     */
    public EntityManagerScheduledOrder(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, ScheduledOrder.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, SCHEDULED_ORDER_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(SCHEDULED_ORDER_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts an order in the database
     *
     * @param scheduledOrder the order to insert
     */
    public void create(ScheduledOrder scheduledOrder) {
        try {
            this.instance.create(scheduledOrder);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes an order from the database
     *
     * @param scheduledOrder the order to delete
     */
    public void delete(ScheduledOrder scheduledOrder) {
        try {
            this.instance.delete(scheduledOrder);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Update an order from the database
     *
     * @param scheduledOrder the order to update
     */
    public void update(ScheduledOrder scheduledOrder) {
        try {
            this.instance.update(scheduledOrder);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all orders in the database
     *
     * @return an iterator over all the orders in the database
     */
    public CloseableIterator<ScheduledOrder> getAll() {
        return this.instance.iterator();
    }

    /**
     * Executes a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<ScheduledOrder> executeQuery(PreparedQuery<ScheduledOrder> query) {
        List<ScheduledOrder> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the orders
     *
     * @return all the orders that are in the database
     */
    public List<ScheduledOrder> getQueryForAll() {
        List<ScheduledOrder> results = null;

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
    public QueryBuilder<ScheduledOrder, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if an orders exists in the database
     *
     * @param scheduledOrder the order to check
     * @return true if it exists, false if not
     */
    public boolean exists(ScheduledOrder scheduledOrder) {
        if (scheduledOrder == null) return false;
        try {
            return this.instance.queryForId(scheduledOrder.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
