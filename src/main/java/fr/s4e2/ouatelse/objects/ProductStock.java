package fr.s4e2.ouatelse.objects;

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

    @Getter
    public static class ProductStockTreeTop extends RecursiveTreeObject<ProductStockTreeTop> {
        private final LongProperty reference;
        private final IntegerProperty stockQuantity;
        private final StringProperty order;
        private final StringProperty shippingDate;

        public ProductStockTreeTop(Long reference, Integer stockQuantity, String order, String shippingDate) {
            this.reference = new SimpleLongProperty(reference);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
            this.order = new SimpleStringProperty(order);
            this.shippingDate = new SimpleStringProperty(shippingDate);
        }
    }

    @Getter
    public static class ProductStockTreeBottom extends RecursiveTreeObject<ProductStockTreeBottom> {
        private final DoubleProperty buyingPrice;
        private final DoubleProperty margin;
        private final DoubleProperty priceWithoutTaxes;
        private final DoubleProperty taxes;
        private final DoubleProperty priceWithTaxes;

        public ProductStockTreeBottom(Double buyingPrice, Double margin, Double taxes, Double sellingPrice) {
            this.buyingPrice = new SimpleDoubleProperty(buyingPrice);
            this.margin = new SimpleDoubleProperty(margin);
            this.taxes = new SimpleDoubleProperty(taxes);
            this.priceWithTaxes = new SimpleDoubleProperty(sellingPrice);
            this.priceWithoutTaxes = new SimpleDoubleProperty();

            this.priceWithoutTaxes.bind(this.priceWithTaxes.subtract(this.priceWithTaxes.multiply(this.taxes)));
        }
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
}
