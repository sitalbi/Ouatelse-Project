package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "address")
public class Address {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private int zipCode;

    @DatabaseField(canBeNull = false)
    private String city;

    @DatabaseField()
    private String details;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;
}
