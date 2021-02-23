package fr.s4e2.ouatelse.databaseInterface;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Role;

import java.sql.SQLException;

public class databaseRoleInterface {

    private static volatile Dao<Role, Long> instance;

    private databaseRoleInterface() {
    }

    public static Role createNewRole(String roleName) {
        Role newRole = null;
        try {
            newRole = new Role();
            getInstance().create(newRole);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return newRole;
    }

    public static void deleteRole(Role role) {
        try {
            getInstance().delete(role);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void updateRole(Role role) {
        try {
            getInstance().update(role);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static Dao<Role, Long> getInstance() {
        if (instance == null) {
            synchronized (databaseStoreInterface.class) {
                if (instance == null) {
                    try {
                        instance = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Role.class);
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }
}
