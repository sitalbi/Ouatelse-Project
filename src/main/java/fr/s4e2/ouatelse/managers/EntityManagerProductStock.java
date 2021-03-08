package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.ProductStock;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerProductStock {
    private static final String PRODUCT_STOCK_MANAGER_NOT_INITIALIZED = "EntityManagerProductStock could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<ProductStock, Long> instance;

    /**
     * Instantiates a new EntityManagerProductStock
     *
     * @param connectionSource the connection source
     */
    public EntityManagerProductStock(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, ProductStock.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, PRODUCT_STOCK_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(PRODUCT_STOCK_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a stock in the database
     *
     * @param productStock the stock object
     */
    public void create(ProductStock productStock) {
        try {
            this.instance.create(productStock);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Deletes a stock from the database
     *
     * @param productStock the stock to be deleted
     */
    public void delete(ProductStock productStock) {
        try {
            this.instance.delete(productStock);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Updates a stock in the database
     *
     * @param productStock the stock to be updated
     */
    public void update(ProductStock productStock) {
        try {
            this.instance.update(productStock);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Gets all the stocks in the database
     *
     * @return the all the stocks in the database
     */
    public CloseableIterator<ProductStock> getAll() {
        return this.instance.iterator();
    }

    /**
     * Executes a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<ProductStock> executeQuery(PreparedQuery<ProductStock> query) {
        List<ProductStock> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return results;
    }

    /**
     * Gets all the stocks
     *
     * @return all the stocks that are in the database
     */
    public List<ProductStock> getQueryForAll() {
        List<ProductStock> results = null;

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
     * @return the query builder for the stocks
     */
    public QueryBuilder<ProductStock, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if a stock exists in the database
     *
     * @param productStock the stock to be checked
     * @return true if it exists, else false
     */
    public boolean exists(ProductStock productStock) {
        if (productStock == null) return false;

        try {
            return this.instance.queryForId(productStock.getId()) != null;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
