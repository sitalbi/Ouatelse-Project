package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "client")
public class Client extends Person {

    @DatabaseField(canBeNull = false)
    private String homePhoneNumber;

    @DatabaseField(canBeNull = false)
    private String workPhoneNumber;

    @DatabaseField
    private String fax;

    @DatabaseField()
    private String details;

    @Getter
    public static class ClientTree extends RecursiveTreeObject<ClientTree> {
        private final LongProperty id;
        private final StringProperty name;
        private final StringProperty surname;
        private final StringProperty workPhoneNumber;
        private final StringProperty city;
        private final IntegerProperty zipCode;

        public ClientTree(Long id, String name, String surname, String workPhoneNumber, String city, Integer zipCode) {
            this.id = new SimpleLongProperty(id);
            this.name = new SimpleStringProperty(name);
            this.surname = new SimpleStringProperty(surname);
            this.workPhoneNumber = new SimpleStringProperty(workPhoneNumber);
            this.city = new SimpleStringProperty(city);
            this.zipCode = new SimpleIntegerProperty(zipCode);
        }
    }

}
