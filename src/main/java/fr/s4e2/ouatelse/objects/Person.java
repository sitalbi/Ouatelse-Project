package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * The Person table contains an ID, last name, first name, phone number, email, date of birth, title, status and address
 */
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String surname;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String mobilePhoneNumber;

    @DatabaseField(canBeNull = false, unique = true)
    private String email;

    @DatabaseField(canBeNull = false)
    private Date birthDate;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_INTEGER)
    private Civility civility;

    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    private PersonState status;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Address address;
}

