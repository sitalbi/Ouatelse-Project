package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Salary table contains an identifier, a month, a gross salary, employer and employee charges, and a imposable state
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "salary")
public class Salary {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private byte month;

    @DatabaseField(canBeNull = false)
    private double grossSalary;

    @DatabaseField(canBeNull = false)
    private double employerCharges;

    @DatabaseField(canBeNull = false)
    private double employeeCharges;

    @DatabaseField(canBeNull = false)
    private boolean imposable;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;
}
