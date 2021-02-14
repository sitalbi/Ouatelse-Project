package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "store")
public class Store {

    @DatabaseField(id = true, canBeNull = false)
    private String id;

    @DatabaseField
    private String password;

    @DatabaseField
    private String address;

    @DatabaseField
    private String city;

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
