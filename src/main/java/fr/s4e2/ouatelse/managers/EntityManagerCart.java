package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Cart;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerCart {
    private static final String CART_MANAGER_NOT_INITIALIZED = "EntityManagerCart could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Cart, Long> instance;

    /**
     * Instantiates a new EntityManagerCart
     *
     * @param connectionSource the connection source
     */
    public EntityManagerCart(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Cart.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, CART_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(CART_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a cart in the database
     *
     * @param cart the cart to be inserted
     */
    public void create(Cart cart) {
        try {
            this.instance.create(cart);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a cart from the database
     *
     * @param cart the cart to be deleted
     */
    public void delete(Cart cart) {
        try {
            this.instance.delete(cart);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a cart in the database
     *
     * @param cart the cart to be updated
     */
    public void update(Cart cart) {
        try {
            this.instance.update(cart);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the carts in the database
     *
     * @return an iterator over all the carts in the database
     */
    public CloseableIterator<Cart> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Cart> executeQuery(PreparedQuery<Cart> query) {
        List<Cart> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return results;
    }

    /**
     * Gets all the carts
     *
     * @return all the carts that are in the database
     */
    public List<Cart> getQueryForAll() {
        List<Cart> results = null;

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
    public QueryBuilder<Cart, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if a cart exists in the database
     *
     * @param cart the cart to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Cart cart) {
        if (cart == null) return false;

        try {
            return this.instance.queryForId(cart.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
