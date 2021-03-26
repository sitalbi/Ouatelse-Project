package fr.s4e2.ouatelse.managers;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.Salary;
import fr.s4e2.ouatelse.objects.User;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerSalary
 */
public class EntityManagerSalary {
    private static final String SALARY_MANAGER_NOT_INITIALIZED = "EntityManagerSalarycould not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<Salary, Long> instance;

    /**
     * Instantiates a new EntityManagerSalary
     *
     * @param connectionSource the connection source
     */
    public EntityManagerSalary(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, Salary.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, SALARY_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(SALARY_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a salary in the database
     *
     * @param salary the client to be inserted
     */
    public void create(Salary salary) {
        try {
            this.instance.create(salary);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a salary from the database
     *
     * @param salary the client to be deleted
     */
    public void delete(Salary salary) {
        try {
            this.instance.delete(salary);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a salary in the database
     *
     * @param salary the salary to be updated
     */
    public void update(Salary salary) {
        try {
            this.instance.update(salary);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the salary in the database
     *
     * @return an iterator over all the salary in the database
     */
    public CloseableIterator<Salary> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<Salary> executeQuery(PreparedQuery<Salary> query) {
        List<Salary> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return results;
    }

    /**
     * Gets all the salary
     *
     * @return all the clients that are in the database
     */
    public List<Salary> getQueryForAll() {
        List<Salary> results = null;

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
     * @return the query builder for the salary
     */
    public QueryBuilder<Salary, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets a corresponding salary if exists, else null
     *
     * @param id the if of the salary
     * @param user the employee who has this salary
     * @return the the salary if exists, else null
     */
    public Salary getSalaryIfExists(long id, User user) {
        Salary salary = null;

        try {
            salary = this.instance.query(this.instance.queryBuilder().where().eq("id", id)
                    .and().eq("user_id", user.getId()).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return salary;
    }

    /**
     * Check if a salary exists in the database
     *
     * @param salary the salary to be checked
     * @return true if it exists, else false
     */
    public boolean exists(Salary salary) {
        if (salary == null) return false;

        try {
            return this.instance.queryForId(salary.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
