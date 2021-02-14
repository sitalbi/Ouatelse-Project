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

    public void setPassword(String password) {
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public boolean isPassword(String password) {
        return this.password.equals(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
    }

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Address address;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Product> products;

    public Store(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
