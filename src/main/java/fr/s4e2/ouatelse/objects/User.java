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
@DatabaseTable(tableName = "users")
public class User extends Person {

    @DatabaseField(canBeNull = false)
    private String credentials;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField(canBeNull = false)
    private Job job;

    @DatabaseField(canBeNull = false)
    private Date hiringDate;

    @DatabaseField(canBeNull = false)
    private int hoursPerWeek;
}
