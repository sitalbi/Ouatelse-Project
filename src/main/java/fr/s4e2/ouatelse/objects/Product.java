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

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String barCode;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private double sellingPrice;

    @DatabaseField(canBeNull = false)
    private double sellingPriceWithTaxes;

    @DatabaseField(canBeNull = false)
    private double purchasePrice;

    @DatabaseField(canBeNull = false)
    private double purchasePriceWithTaxes;

    @DatabaseField(canBeNull = false)
    private int productReference;

    @DatabaseField(canBeNull = false)
    private String color;

    @DatabaseField(canBeNull = false)
    private String brand;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_INTEGER)
    private ProductState state;

    @DatabaseField(canBeNull = false)
    private String category;

    //@DatabaseField(foreign = true, foreignAutoRefresh = true)
    //private ProductType productType; ce serait mieux que ce soit
    // un attribut de la classe produit plutôt qu'une classe productType

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Provider selledBy;
}
