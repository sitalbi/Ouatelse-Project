package fr.s4e2.ouatelse.objects;

import com.google.common.hash.Hashing;
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

import java.nio.charset.StandardCharsets;
import java.util.Date;

@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "user")
public class User extends Person {

    @DatabaseField(canBeNull = false, unique = true)
    private String credentials;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private Role role;

    @DatabaseField(canBeNull = false)
    private Date hiringDate;

    @DatabaseField(canBeNull = false)
    private int hoursPerWeek;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Salary> salarySheets;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store workingStore;

    public void setPassword(String password) {
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public boolean isPassword(String password) {
        return this.password.equals(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
    }

    @Getter
    public static class UserTree extends RecursiveTreeObject<UserTree> {
        private final StringProperty id;
        private final StringProperty lastName;
        private final StringProperty firstName;
        private final StringProperty role;
        private final StringProperty storeName;
        private final StringProperty status;

        public UserTree(String id, String lastName, String firstName, Role role, Store storeName, PersonState status) {
            this.id = new SimpleStringProperty(id);
            this.lastName = new SimpleStringProperty(lastName);
            this.firstName = new SimpleStringProperty(firstName);
            this.role = new SimpleStringProperty(role != null ? role.getName() : "");
            this.storeName = new SimpleStringProperty(storeName != null ? storeName.getId() : "");
            this.status = new SimpleStringProperty(status != null ? status.toString() : "");
        }
    }
}
