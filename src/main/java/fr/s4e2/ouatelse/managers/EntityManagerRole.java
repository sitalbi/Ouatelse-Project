package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerRole
 */
public class EntityManagerRole {

    private static final String ROLE_MANAGER_NOT_INITIALIZED = "EntityManagerRole could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Role, Long> instance;

    /**
     * Instantiates a new EntityManagerRole
     *
     * @param connectionSource the connection source
     */
    public EntityManagerRole(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Role.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, ROLE_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(ROLE_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a role in the database
     *
     * @param roleName the name of the role to be inserted
     * @return the initialized role
     */
    public Role create(String roleName) {
        Role newRole = null;

        try {
            newRole = new Role(roleName);
            this.instance.create(newRole);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return newRole;
    }

    /**
     * Deletes a role from the database
     *
     * @param role the role to be deleted
     */
    public void delete(Role role) {
        try {
            this.instance.delete(role);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a role in the database
     *
     * @param role the role to be updated
     */
    public void update(Role role) {
        try {
            this.instance.update(role);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the roles in the database
     *
     * @return the all the roles in the database
     */
    public CloseableIterator<Role> getAll() {
        return this.instance.iterator();
    }

    /**
     * Executes a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Role> executeQuery(PreparedQuery<Role> query) {
        List<Role> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the roles
     *
     * @return all the roles that are in the database
     */
    public List<Role> getQueryForAll() {
        List<Role> results = null;

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
    public QueryBuilder<Role, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Check if a role exists in the database
     *
     * @param role the role to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Role role) {
        if (role == null) return false;

        try {
            return this.instance.queryForId(role.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
