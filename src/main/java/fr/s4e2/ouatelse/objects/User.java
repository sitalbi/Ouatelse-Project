package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String password;

    //@DatabaseField(canBeNull = false)
    //private RoleType role;

    @DatabaseField(canBeNull = false)
    private Date dateHired;

    @DatabaseField(canBeNull = false)
    private int hoursPerWeek;
}

