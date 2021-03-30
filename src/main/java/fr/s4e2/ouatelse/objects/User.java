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

/**
 * The User table contains credentials, an encrypted password, a role, a hiring date, the
 * number of hours per week the user works, a salary list, and the store where the user works
 */
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
    private int hoursPerWeek = 0;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Salary> salarySheets;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store workingStore;

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

    /**
     * Converts this object into a tree table object representing it's information
     *
     * @return A tree table object representing this object's information
     */
    public UserTree toUserTree() {
        return new UserTree(
                this.getCredentials(),
                this.getSurname(),
                this.getName(),
                this.getRole(),
                this.getWorkingStore(),
                this.getStatus()
        );
    }

    /**
     * Converts this object into a tree table object representing it's information (used for the salary management screen)
     *
     * @return A tree table object representing this object's information
     */
    public UserSalaryTree toUserSalaryTree() {
        return new UserSalaryTree(
                this.getCredentials(),
                this.getSurname(),
                this.getName(),
                this.getRole()
        );
    }

    /**
     * Recursive User Tree
     */
    @Getter
    public static class UserTree extends RecursiveTreeObject<UserTree> {
        private final StringProperty id;
        private final StringProperty lastName;
        private final StringProperty firstName;
        private final StringProperty role;
        private final StringProperty storeName;
        private final StringProperty status;

        /**
         * Constructor
         *
         * @param id        the ID
         * @param lastName  the Last Name
         * @param firstName the First Name
         * @param role      the Role
         * @param storeName the Name of the Store
         * @param status    the User Status
         */
        public UserTree(String id, String lastName, String firstName, Role role, Store storeName, PersonState status) {
            this.id = new SimpleStringProperty(id);
            this.lastName = new SimpleStringProperty(lastName);
            this.firstName = new SimpleStringProperty(firstName);
            this.role = new SimpleStringProperty(role != null ? role.getName() : "");
            this.storeName = new SimpleStringProperty(storeName != null ? storeName.getId() : "");
            this.status = new SimpleStringProperty(status != null ? status.toString() : "");
        }
    }

    /**
     * Recursive User Salary Tree
     */
    @Getter
    public static class UserSalaryTree extends RecursiveTreeObject<UserSalaryTree> {
        private final StringProperty id;
        private final StringProperty lastName;
        private final StringProperty firstName;
        private final StringProperty role;

        public UserSalaryTree(String id, String lastName, String firstName, Role role) {
            this.id = new SimpleStringProperty(id);
            this.lastName = new SimpleStringProperty(lastName);
            this.firstName = new SimpleStringProperty(firstName);
            this.role = new SimpleStringProperty(role != null ? role.getName() : "");
        }
    }
}
