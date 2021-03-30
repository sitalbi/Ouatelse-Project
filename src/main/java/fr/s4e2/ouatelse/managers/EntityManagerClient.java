package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerUser
 */
public class EntityManagerClient {
    private static final String CLIENT_MANAGER_NOT_INITIALIZED = "EntityManagerClient could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Client, Long> instance;

    /**
     * Instantiates a new EntityManagerClient
     *
     * @param connectionSource the connection source
     */
    public EntityManagerClient(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Client.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, CLIENT_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(CLIENT_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a client in the database
     *
     * @param client the client to be inserted
     */
    public void create(Client client) {
        try {
            this.instance.create(client);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a client from the database
     *
     * @param client the client to be deleted
     */
    public void delete(Client client) {
        try {
            this.instance.delete(client);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a client in the database
     *
     * @param client the client to be updated
     */
    public void update(Client client) {
        try {
            this.instance.update(client);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the clients in the database
     *
     * @return an iterator over all the clients in the database
     */
    public CloseableIterator<Client> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Client> executeQuery(PreparedQuery<Client> query) {
        List<Client> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return results;
    }

    /**
     * Gets all the clients
     *
     * @return all the clients that are in the database
     */
    public List<Client> getQueryForAll() {
        List<Client> results = null;

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
    public QueryBuilder<Client, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets a corresponding client if exists, else null
     *
     * @param firstName the credentials of the client
     * @param lastName    the lastName of the client
     * @return the client if exists, else null
     */
    public Client getClientIfExists(long id, String firstName, String lastName) {
        Client client = null;

        try {
            //noinspection UnstableApiUsage
            client = this.instance.query(this.instance.queryBuilder().where().eq("id", id)
                    .and().eq("name", firstName).and().eq("surname", lastName).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return client;
    }

    /**
     * Check if a client exists in the database
     *
     * @param client the client to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Client client) {
        if (client == null) return false;

        try {
            return this.instance.queryForId(client.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
