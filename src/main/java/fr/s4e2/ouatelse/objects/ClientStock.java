package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The ClientStock table contains an identifier, a product, the quantity and the associated client
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "client_stock")
public class ClientStock {

    @DatabaseField(canBeNull = false)
    int quantity = 0;
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Product product;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Client client;

    /**
     * Converts this object into a tree table object representing its information
     *
     * @return A tree table object representing this object's information
     */
    public ClientStockTree toClientStockTree() {
        return new ClientStockTree(
                this.getProduct().getReference(),
                this.product.getName(),
                this.getQuantity()
        );
    }

    /**
     * Recursive Client Stock Info Tree
     */
    @Getter
    public static class ClientStockTree extends RecursiveTreeObject<ClientStockTree> {
        private final LongProperty reference;
        private final StringProperty productName;
        private final IntegerProperty stockQuantity;

        /**
         * Constructor
         *
         * @param reference     the Reference of the Product
         * @param productName   the Product's name
         * @param stockQuantity the Quantity of Stock remaining
         */
        public ClientStockTree(Long reference, String productName, Integer stockQuantity) {
            this.reference = new SimpleLongProperty(reference);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
            this.productName = new SimpleStringProperty(productName);
        }
    }
}