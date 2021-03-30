package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Address table contains an identifier, a zip code, a city, address
 */
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

    @DatabaseField(canBeNull = false)
    private String streetNameAndNumber;

    /**
     * Constructor
     *
     * @param zipCode the Zip Code
     * @param city    the City
     * @param streetNameAndNumber the street name and its number
     */
    public Address(int zipCode, String city, String streetNameAndNumber) {
        this.zipCode = zipCode;
        this.city = city;
        this.streetNameAndNumber = streetNameAndNumber;
    }
}
