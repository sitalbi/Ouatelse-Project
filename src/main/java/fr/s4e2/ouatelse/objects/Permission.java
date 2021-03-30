package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import org.apache.commons.lang3.text.WordUtils;

/**
 * The Permission enumeration lists all possible permissions that can be granted to a user
 */
public enum Permission {
    @DatabaseField(dataType = DataType.ENUM_STRING)
    ROLE_MANAGEMENT,
    STORE_MANAGEMENT,
    USER_MANAGEMENT,
    MONITORING,
    SALARY_MANAGEMENT,
    STATISTICS,
    PRODUCTS_MANAGEMENT,
    STOCKS_MANAGEMENT,
    SALES_MANAGEMENT,
    CLIENTS_MANAGEMENT,
    PLANNING_MANAGEMENT,
    VENDORS_MANAGEMENT;

    /**
     * Allows to capitalize the Permission and replace "_" by blanks
     *
     * @return the Converted Name
     */
    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().replace("_", " "));
    }
}
