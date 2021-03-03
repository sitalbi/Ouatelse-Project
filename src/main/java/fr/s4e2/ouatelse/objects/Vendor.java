package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "vendor")
public class Vendor {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Address address;

    @DatabaseField(canBeNull = false, unique = true)
    private String email;

    @DatabaseField(canBeNull = false)
    private boolean contractState;

    @DatabaseField(canBeNull = false)
    private String phoneNumber;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Product> products;

    @Getter
    public static class VendorTree extends RecursiveTreeObject<Vendor.VendorTree> {
        private final StringProperty name;
        private final StringProperty email;
        private final StringProperty city;
        private final StringProperty contractState;

        public VendorTree(String name, String city, String email, boolean contractState) {
            this.name = new SimpleStringProperty(name);
            this.city = new SimpleStringProperty(city);
            this.email = new SimpleStringProperty(email);
            this.contractState = new SimpleStringProperty(contractState ? "\u2713" : "\u274c");
        }
    }
}
