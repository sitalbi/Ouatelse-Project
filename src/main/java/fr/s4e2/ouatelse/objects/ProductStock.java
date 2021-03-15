package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The ProductStock table contains an identifier, a product, the quantity and the associated store
 */
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

    /**
     * Converts this object into a tree table object representing its information
     *
     * @return A tree table object representing this object's information
     */
    public ProductStockInfoTree toProductStockInfoTree() {
        return new ProductStockInfoTree(
                this.getProduct().getReference(),
                this.getQuantity(),
                String.valueOf(this.getId())
        );
    }

    /**
     * Recursive Product Stock Info Tree
     */
    @Getter
    public static class ProductStockInfoTree extends RecursiveTreeObject<ProductStockInfoTree> {
        private final LongProperty reference;
        private final IntegerProperty stockQuantity;
        private final StringProperty order;

        /**
         * Constructor
         *
         * @param reference     the Reference of the Product
         * @param stockQuantity the Quantity of Stock remaining
         * @param order         the Order
         */
        public ProductStockInfoTree(Long reference, Integer stockQuantity, String order) {
            this.reference = new SimpleLongProperty(reference);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
            this.order = new SimpleStringProperty(order);
        }
    }

    /**
     * Recursive Product Stock Tree
     */
    @Getter
    public static class ProductStockTree extends RecursiveTreeObject<ProductStockTree> {
        private final LongProperty id;
        private final LongProperty reference;
        private final StringProperty article;
        private final DoubleProperty unitValue;
        private final IntegerProperty stockQuantity;
        private final StringProperty productState;

        /**
         * Constructor
         *
         * @param id            the ID
         * @param reference     the Reference of the Product
         * @param article       the Name of the Article
         * @param unitValue     the Unit Price
         * @param stockQuantity the Quantity of Stock remaining
         * @param productState  the state of the Product
         */
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
     * @return A tree table object fully representing this object's information
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
