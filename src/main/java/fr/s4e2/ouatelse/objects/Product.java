package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
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
    private String category;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Vendor soldBy;

    // Obligatory due to database mapping ########################
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store store;
    // ###########################################################

    @Getter
    public static class ProductTree extends RecursiveTreeObject<ProductTree> {
        private final LongProperty reference;
        private final StringProperty name;
        private final DoubleProperty sellingPrice;
        private final DoubleProperty purchasePrice;
        private final StringProperty brand;
        private final StringProperty state;
        private final StringProperty category;
        private final StringProperty soldByName;

        public ProductTree(Long reference, String name, Double sellingPrice, Double purchasePrice, String brand, String state, String category, String soldByName) {
            this.reference = new SimpleLongProperty(reference);
            this.name = new SimpleStringProperty(name);
            this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
            this.purchasePrice = new SimpleDoubleProperty(purchasePrice);
            this.brand = new SimpleStringProperty(brand);
            this.state = new SimpleStringProperty(state);
            this.category = new SimpleStringProperty(category);
            this.soldByName = new SimpleStringProperty(soldByName != null ? soldByName : "");
        }
    }
}
