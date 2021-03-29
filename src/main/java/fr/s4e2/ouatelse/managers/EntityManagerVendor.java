package fr.s4e2.ouatelse.managers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Vendor;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerVendor
 */
public class EntityManagerVendor {
    private static final String VENDOR_MANAGER_NOT_INITIALIZED = "EntityManagerVendor could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Vendor, Long> instance;

    /**
     * Instantiates a new EntityManagerVendor
     *
     * @param connectionSource the connection source
     */
    public EntityManagerVendor(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Vendor.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, VENDOR_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(VENDOR_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a vendor in the database
     *
     * @param vendor the vendor to be inserted
     */
    public void create(Vendor vendor) {
        try {
            this.instance.create(vendor);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a vendor from the database
     *
     * @param vendor the vendor to be deleted
     */
    public void delete(Vendor vendor) {
        try {
            this.instance.delete(vendor);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a vendor in the database
     *
     * @param vendor the vendor to be updated
     */
    public void update(Vendor vendor) {
        try {
            this.instance.update(vendor);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the vendors in the database
     *
     * @return na iterator over all the vendors in the database
     */
    public CloseableIterator<Vendor> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Vendor> executeQuery(PreparedQuery<Vendor> query) {
        List<Vendor> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the vendors
     *
     * @return all the vendors that are in the database
     */
    public List<Vendor> getQueryForAll() {
        List<Vendor> results = null;

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
     * @return the query builder for the vendors
     */
    public QueryBuilder<Vendor, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets a corresponding vendor if exists, else null
     *
     * @param name the name of the vendor
     * @return the vendor if exists, else null
     */
    public Vendor getVendorIfExists(String name) {
        Vendor vendor = null;

        try {
            vendor = this.instance.query(this.instance.queryBuilder().where().eq("name", name).prepare())
                    .stream().findFirst().orElse(null);

        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return vendor;
    }

    /**
     * Check if an vendor exists in the database
     *
     * @param vendor the vendor to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Vendor vendor) {
        if (vendor == null) return false;

        try {
            return this.instance.queryForId(vendor.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
