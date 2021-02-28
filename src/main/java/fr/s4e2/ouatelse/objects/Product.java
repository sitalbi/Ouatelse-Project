package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "product")
public class Product {

    @DatabaseField(unique = true, canBeNull = false)
    private long reference;

    @DatabaseField(canBeNull = false)
    private String barCode;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private double sellingPrice;

    @DatabaseField(canBeNull = false)
    private double purchasePrice;

    @DatabaseField(canBeNull = false)
    private String brand;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING)
    private ProductState state;

    @DatabaseField(canBeNull = false)
    private long quantity = 0;

    @DatabaseField(canBeNull = false)
    private String category;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Vendor soldBy;
}
