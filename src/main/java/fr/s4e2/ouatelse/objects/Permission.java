package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public enum Permission {
    @DatabaseField(dataType = DataType.ENUM_STRING)
    ADMIN,
    ROLE_ADD,
    ROLE_DELETE,
    PERMISSION_ADD,
    PERMISSION_REMOVE
}
