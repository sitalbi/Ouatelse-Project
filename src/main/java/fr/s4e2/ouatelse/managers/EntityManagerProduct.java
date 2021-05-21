package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerProduct {
    private static final String PRODUCT_MANAGER_NOT_INITIALIZED = "EntityManagerProduct could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Product, Long> instance;

    /**
     * Instantiates a new EntityManagerProduct
     *
     * @param connectionSource the connection source
     */
    public EntityManagerProduct(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Product.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, PRODUCT_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(PRODUCT_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a product in the database
     *
     * @param product the product
     */
    public void create(Product product) {
        try {
            this.instance.create(product);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a product from the database
     *
     * @param product the product to be deleted
     */
    public void delete(Product product) {
        try {
            this.instance.delete(product);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a product in the database
     *
     * @param product the product to be updated
     */
    public void update(Product product) {
        try {
            this.instance.update(product);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the products in the database
     *
     * @return the all the products in the database
     */
    public CloseableIterator<Product> getAll() {
        return this.instance.iterator();
    }

    /**
     * Executes a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Product> executeQuery(PreparedQuery<Product> query) {
        List<Product> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the products
     *
     * @return all the products that are in the database
     */
    public List<Product> getQueryForAll() {
        List<Product> results = null;

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
     * @return the query builder for the roles
     */
    public QueryBuilder<Product, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets a corresponding product if exists, else null
     *
     * @param reference the reference of the product
     * @return the product if exists, else null
     */
    public Product getProductIfExists(long reference) {
        Product product = null;

        try {
            product = this.instance.query(this.instance.queryBuilder().where().eq("reference", reference).prepare())
                    .stream().findFirst().orElse(null);

        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return product;
    }

    /**
     * Check if a product exists in the database
     *
     * @param product the product to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Product product) {
        if (product == null) return false;

        try {
            return this.instance.queryForId(product.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}


