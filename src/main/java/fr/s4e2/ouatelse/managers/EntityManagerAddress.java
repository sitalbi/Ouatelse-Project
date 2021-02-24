package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.objects.Address;

import java.sql.SQLException;
import java.util.List;

/**
 * The type EntityManagerAddress
 */
public class EntityManagerAddress {
    private final ConnectionSource connectionSource;
    private Dao<Address, Long> instance;

    /**
     * Instantiates a new EntityManagerAddress
     *
     * @param connectionSource the connection source
     */
    public EntityManagerAddress(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Address.class);
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Inserts a new address in the database
     *
     * @param address the address to be inserted
     */
    public void create(Address address) {
        try {
            this.instance.create(address);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Deletes an address from the database
     *
     * @param address the address to be deleted
     */
    public void delete(Address address) {
        try {
            this.instance.delete(address);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Updates an address from the database
     *
     * @param address the address to be updated
     */
    public void update(Address address) {
        try {
            this.instance.update(address);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Gets all the addresses that are in the database
     *
     * @return all the addresses that are in the database
     */
    public CloseableIterator<Address> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the result list
     */
    public List<Address> executeQuery(PreparedQuery<Address> query) {
        List<Address> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return results;
    }

    /**
     * Gets all the addresses
     *
     * @return all the addresses that are in the database
     */
    public List<Address> getQueryForAll() {
        List<Address> results = null;

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
     * @return the query builder for the addresses
     */
    public QueryBuilder<Address, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if an address exists in the database
     *
     * @param address the address to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Address address) {
        try {
            return this.instance.queryForId(address.getId()) != null;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
