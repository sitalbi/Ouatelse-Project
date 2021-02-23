package fr.s4e2.ouatelse.databaseInterface;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Store;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class databaseStoreInterface {

    private static volatile Dao<Store, Long> instance;

    private databaseStoreInterface() {
    }

    public static Store getStoreIfExists(String id, String password) {
        Store store = null;

        try {
            //noinspection UnstableApiUsage
            store = getInstance().query(getInstance().queryBuilder().where()
                    .eq("id", id)
                    .and().eq("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
                    ).prepare()).stream().findFirst().orElse(null);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return store;
    }

    public static Dao<Store, Long> getInstance() {
        if (instance == null) {
            synchronized (databaseStoreInterface.class) {
                if (instance == null) {
                    try {
                        instance = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Store.class);
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }
}
