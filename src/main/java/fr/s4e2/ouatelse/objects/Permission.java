package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public enum Permission {
    @DatabaseField(dataType = DataType.ENUM_STRING)
    ROLE_MANAGEMENT,
    STORE_MANAGEMENT,
    USER_MANAGEMENT,
    MONITORING,
    EMPLOYEES_MANAGEMENT,
    SALARY_MANAGEMENT,
    STATISTICS,
    PRODUCTS_MANAGEMENT,
    STOCKS_MANAGEMENT,
    SALES_MANAGEMENT,
    CLIENTS_MANAGEMENT,
    PLANNING_MANAGEMENT
}
