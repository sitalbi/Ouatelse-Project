package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * The Availability table contains an identifier, a start time, an end time, and a user state
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "availability")
public class Availability {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private Date beginHour;

    @DatabaseField(canBeNull = false)
    private Date endHour;

    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    private UserState userState;
}
