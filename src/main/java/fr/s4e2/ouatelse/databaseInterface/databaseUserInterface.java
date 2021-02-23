package fr.s4e2.ouatelse.databaseInterface;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.User;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class databaseUserInterface {

    private static volatile Dao<User, Long> instance;

    private databaseUserInterface() {}

    public static User getUserIfExists(String id, String password) {
        User user = null;

        try {
            user = getInstance().query(getInstance().queryBuilder().where().eq("id", id)
                    .and().eq("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8)
                    .toString()).prepare()).stream().findFirst().orElse(null);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return user;
    }

    public static Dao<User, Long> getInstance() {
        if(instance == null) {
            synchronized (databaseUserInterface.class) {
                if (instance == null) {
                    try {
                        instance = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), User.class);
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }
}
