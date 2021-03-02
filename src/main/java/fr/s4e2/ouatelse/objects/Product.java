package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Product table contains an identifier, a barcode, a name, a selling price, a purchase price,
 * a reference, a brand, a product status, a category, a vendor and a store
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "product")
public class Product {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String barCode;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private double sellingPrice;

    @DatabaseField(canBeNull = false)
    private double purchasePrice;

    @DatabaseField(canBeNull = false)
    private int reference;

    @DatabaseField(canBeNull = false)
    private String brand;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_INTEGER)
    private ProductState state;

    @DatabaseField(canBeNull = false)
    private String category;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Vendor soldBy;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store store;
}
