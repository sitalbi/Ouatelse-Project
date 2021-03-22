package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Cart table contains an identifier, a date, a product and a client
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "cart")
public class Cart {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private Date date;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Product> products;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Client client;

    /**
     * Converts this object into a tree table object representing it's information
     *
     * @return A tree table object representing this object's information
     */
    public CartTree toCartTree() {
        return new CartTree(
                this.getId(),
                this.getDate(),
                this.getProducts(),
                this.getClient()
        );
    }

    /**
     * Recursive User Tree
     */
    @Getter
    public static class CartTree extends RecursiveTreeObject<CartTree> {
        private final LongProperty id;
        private final StringProperty date;
        private final StringProperty hour;
        private final StringProperty productName;
        private final StringProperty clientEmail;

        /**
         * Constructor
         *
         * @param id      the ID
         * @param date    the date
         * @param product the First product
         * @param client  the client
         */
        public CartTree(long id, Date date, ForeignCollection<Product> products, Client client) {
            StringBuilder productListStringBuilder = new StringBuilder();
            products.forEach(product -> productListStringBuilder.append(product.getName()).append("\n"));

            this.id = new SimpleLongProperty(id);
            this.date = new SimpleStringProperty(date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "");
            this.hour = new SimpleStringProperty(date != null ? new SimpleDateFormat("hh:mm:ss").format(date) : "");
            this.productName = new SimpleStringProperty(productListStringBuilder.toString().trim());
            this.clientEmail = new SimpleStringProperty(client != null ? client.getEmail() : "");
        }
    }
}
