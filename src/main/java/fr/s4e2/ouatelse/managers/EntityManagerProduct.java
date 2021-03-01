package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.objects.Product;

import java.sql.SQLException;
import java.util.List;

public class EntityManagerProduct {
    private final ConnectionSource connectionSource;
    private Dao<Product, Long> instance;

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
            exception.printStackTrace();
            System.exit(0);
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
            return false;
        }
    }
}


