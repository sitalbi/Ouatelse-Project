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
    private Date date = new Date();

    @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    private ForeignCollection<ClientStock> clientStocks;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Client client;

    @DatabaseField(canBeNull = false)
    private boolean closed = false;

    /**
     * Converts this object into a tree table object representing it's information
     *
     * @return A tree table object representing this object's information
     */
    public CartTree toCartTree() {
        return new CartTree(
                this.getId(),
                this.getDate(),
                this.isClosed()
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
        private final StringProperty closed;

        /**
         * Constructor
         *
         * @param id     the ID
         * @param date   the date
         * @param closed the car being closed
         */
        public CartTree(long id, Date date, boolean closed) {
            this.id = new SimpleLongProperty(id);
            this.date = new SimpleStringProperty(date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "");
            this.hour = new SimpleStringProperty(date != null ? new SimpleDateFormat("hh:mm:ss").format(date) : "");
            this.closed = new SimpleStringProperty(closed ? "\u2713" : "\u274c");
        }
    }
}
