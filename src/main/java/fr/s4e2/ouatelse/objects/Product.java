package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
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

    @DatabaseField(unique = true, canBeNull = false)
    private long reference;

    @DatabaseField(canBeNull = false)
    private String barCode;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private double purchasePrice;

    @DatabaseField(canBeNull = false)
    private String brand;

    @DatabaseField(canBeNull = false)
    private double margin;

    @DatabaseField(canBeNull = false)
    private double taxes;

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

    /**
     * Allows you to get the selling price of a product
     *
     * @return the Selling Price
     */
    public double getSellingPrice() {
        double priceWithMargin = (purchasePrice + (margin * purchasePrice));
        return priceWithMargin + (taxes * priceWithMargin);
    }

    /**
     * Allows you to get the tax-free price of a product
     *
     * @return the Tax-free Price
     */
    public double getPriceWithoutTaxes() {
        return purchasePrice + (margin * purchasePrice);
    }

    /**
     * Recursive Product Tree
     */
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
        private final DoubleProperty margin;
        private final DoubleProperty taxes;

        /**
         * Constructor
         *
         * @param reference     the Reference
         * @param name          the Name
         * @param margin        the Margin
         * @param purchasePrice the Purchase Price
         * @param brand         the Brand
         * @param state         the State
         * @param category      the Category
         * @param soldByName    the Name
         * @param taxes         the Taxes
         */
        public ProductTree(Long reference, String name, Double margin, Double purchasePrice, String brand, String state, String category, String soldByName, Double taxes) {
            this.reference = new SimpleLongProperty(reference);
            this.name = new SimpleStringProperty(name);
            this.margin = new SimpleDoubleProperty(margin);
            this.purchasePrice = new SimpleDoubleProperty(purchasePrice);
            this.brand = new SimpleStringProperty(brand);
            this.state = new SimpleStringProperty(state);
            this.category = new SimpleStringProperty(category);
            this.soldByName = new SimpleStringProperty(soldByName != null ? soldByName : "");
            this.taxes = new SimpleDoubleProperty(taxes);

            this.sellingPrice = new SimpleDoubleProperty();

            this.sellingPrice.bind(this.purchasePrice
                    .add(this.purchasePrice.multiply(this.margin))
                    .add(this.purchasePrice.add(this.purchasePrice.multiply(this.margin)).multiply(this.taxes))
            );
        }
    }

    /**
     * Converts this object into a tree table object representing it's information
     *
     * @return A tree table object representing this object's information
     */
    public ProductTree toProductTree() {
        return new ProductTree(
                this.getReference(),
                this.getName(),
                this.getMargin(),
                this.getPurchasePrice(),
                this.getBrand(),
                this.getState().toString(),
                this.getCategory(),
                (this.getSoldBy() != null) ? this.getSoldBy().getName() : "",
                this.getTaxes()
        );
    }

    /**
     * Recursive Product Prices Tree
     */
    @Getter
    public static class ProductPricesTree extends RecursiveTreeObject<ProductPricesTree> {
        private DoubleProperty buyingPrice;
        private DoubleProperty margin;
        private final DoubleProperty priceWithoutTaxes;
        private DoubleProperty taxes;
        private final DoubleProperty priceWithTaxes;

        /**
         * Constructor
         *
         * @param buyingPrice the Buying Price
         * @param margin      the Margin
         * @param taxes       the Taxes
         */
        public ProductPricesTree(Double buyingPrice, Double margin, Double taxes) {
            this.buyingPrice = new SimpleDoubleProperty(buyingPrice);
            this.margin = new SimpleDoubleProperty(margin);
            this.priceWithoutTaxes = new SimpleDoubleProperty();
            this.taxes = new SimpleDoubleProperty(taxes);
            this.priceWithTaxes = new SimpleDoubleProperty();

            this.loadValues();
        }

        /**
         * Sets the buying price
         *
         * @param buyingPrice the Buying Price
         */
        public void setBuyingPrice(double buyingPrice) {
            this.buyingPrice = new SimpleDoubleProperty(buyingPrice);
            this.loadValues();
        }

        /**
         * Sets the margin
         *
         * @param margin the Margin
         */
        public void setMargin(double margin) {
            this.margin = new SimpleDoubleProperty(margin);
            this.loadValues();
        }

        /**
         * Sets the tax
         *
         * @param taxes the Taxes
         */
        public void setTaxes(double taxes) {
            this.taxes = new SimpleDoubleProperty(taxes);
            this.loadValues();
        }

        private void loadValues() {
            this.priceWithTaxes.bind(this.buyingPrice
                    .add(this.buyingPrice.multiply(this.margin))
                    .add(this.buyingPrice.add(this.buyingPrice.multiply(this.margin)).multiply(this.taxes))
            );
            this.priceWithoutTaxes.bind(this.priceWithTaxes.subtract(this.priceWithTaxes.multiply(this.taxes)));
        }
    }

    /**
     * Converts this object into a tree table object representing it's prices
     *
     * @return A tree table object representing this object's prices
     */
    public ProductPricesTree toProductPricesTree() {
        return new ProductPricesTree(this.getPurchasePrice(), this.getMargin(), this.getTaxes());
    }
}
