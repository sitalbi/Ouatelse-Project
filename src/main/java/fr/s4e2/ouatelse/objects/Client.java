package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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

    @DatabaseField(canBeNull = true)
    private String fax;

    @DatabaseField()
    private String details;
}
