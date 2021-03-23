package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Client table extends Person and contains a home phone number, a work phone number, a fax and a detail field
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "client")
public class Client extends Person {

    @DatabaseField
    private String homePhoneNumber;

    @DatabaseField
    private String workPhoneNumber;

    @DatabaseField
    private String fax;

    @DatabaseField
    private String details;

    /**
     * Converts this object into a tree table object representing it's information
     *
     * @return A tree table object representing this object's information
     */
    public ClientTree toClientTree() {
        return new ClientTree(
                this.getId(),
                this.getName(),
                this.getSurname(),
                this.getEmail()
        );
    }

    /**
     * Recursive Client Tree
     */
    @Getter
    public static class ClientTree extends RecursiveTreeObject<ClientTree> {
        private final LongProperty id;
        private final StringProperty name;
        private final StringProperty surname;
        private final StringProperty email;

        /**
         * Constructor
         *
         * @param id      client's id
         * @param name    client's first name
         * @param surname client's last name
         * @param email   client's email
         */
        public ClientTree(Long id, String name, String surname, String email) {
            this.id = new SimpleLongProperty(id);
            this.name = new SimpleStringProperty(name);
            this.surname = new SimpleStringProperty(surname);
            this.email = new SimpleStringProperty(email);
        }
    }

}
