package fr.s4e2.ouatelse.objects;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * The Store table contains a username, an encrypted password, an address, a manager and a list of products
 */
@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "store")
public class Store {

    @DatabaseField(id = true, canBeNull = false)
    private String id;

    @DatabaseField(canBeNull = false)
    private String password;

    /**
     * Creates an encrypted password from a String
     *
     * @param password the Password before encryption
     */
    public void setPassword(String password) {
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    /**
     * Checks if the provided String is a Password
     *
     * @param password the provided Password
     * @return True or False
     */
    public boolean isPassword(String password) {
        return this.password.equals(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
    }

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Address address;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private User manager;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Product> products;

    /**
     * Constructor
     *
     * @param id the ID
     */
    public Store(String id) {
        this.id = id;
    }

    /**
     * Gets the ID
     *
     * @return the ID
     */
    @Override
    public String toString() {
        return this.getId();
    }
}
