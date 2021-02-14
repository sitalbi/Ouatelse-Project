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
import java.util.Date;

@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "user")
public class User extends Person {

    @DatabaseField(canBeNull = false)
    private String credentials;

    @DatabaseField(canBeNull = false)
    private String password;

    public void setPassword(String password) {
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public boolean isPassword(String password) {
        return this.password.equals(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
    }

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Role role;

    @DatabaseField(canBeNull = false)
    private Date hiringDate;

    @DatabaseField(canBeNull = false)
    private int hoursPerWeek;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Salary> salarySheets;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store workingStore;
}
