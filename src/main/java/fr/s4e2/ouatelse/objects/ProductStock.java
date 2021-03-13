package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "product_stock")
public class ProductStock {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Product product;

    @DatabaseField(canBeNull = false)
    int quantity = 0;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store store;

    @DatabaseField
    private Date date;

    @Getter
    public static class ProductStockInfoTree extends RecursiveTreeObject<ProductStockInfoTree> {
        private final LongProperty reference;
        private final IntegerProperty stockQuantity;
        private final StringProperty order;
        private final StringProperty shippingDate;

        public ProductStockInfoTree(Long reference, Integer stockQuantity, String order, Date shippingDate) {
            this.reference = new SimpleLongProperty(reference);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
            this.order = new SimpleStringProperty(order);
            this.shippingDate = new SimpleStringProperty(new SimpleDateFormat("").format(shippingDate));
        }
    }

    /**
     * Converts this object into a tree table object representing its information
     *
     * @return a tree table object representing this object's information
     */
    public ProductStockInfoTree toProductStockInfoTree() {
        return new ProductStockInfoTree(
                this.getProduct().getReference(),
                this.getQuantity(),
                String.valueOf(this.getId()),
                this.getDate()
        );
    }

    @Getter
    public static class ProductStockTree extends RecursiveTreeObject<ProductStockTree> {
        private final LongProperty id;
        private final LongProperty reference;
        private final StringProperty article;
        private final DoubleProperty unitValue;
        private final IntegerProperty stockQuantity;
        private final StringProperty productState;

        public ProductStockTree(Long id, Long reference, String article, Double unitValue, Integer stockQuantity, ProductState productState) {
            this.id = new SimpleLongProperty(id);
            this.reference = new SimpleLongProperty(reference);
            this.article = new SimpleStringProperty(article);
            this.unitValue = new SimpleDoubleProperty(unitValue);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
            this.productState = new SimpleStringProperty(productState.toString());
        }
    }

    /**
     * Converts this object into a tree table object fully representing its information
     *
     * @return a tree table object fully representing this object's information
     */
    public ProductStockTree toProductStockTree() {
        return new ProductStockTree(
                this.getId(),
                this.getProduct().getReference(),
                this.getProduct().getName(),
                this.getProduct().getPurchasePrice(),
                this.getQuantity(),
                this.getProduct().getState()
        );
    }
}
