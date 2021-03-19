package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * The ScheduledOrder table has an identifier, a product and a date
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "scheduled_orders")
public class ScheduledOrder {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Product product;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Store store;

    @DatabaseField(canBeNull = false)
    private Date scheduledOrderDate;

    @DatabaseField(canBeNull = false)
    private int quantity;

    public ScheduledOrder(Product product, Store store, Date scheduledOrderDate, int quantity) {
        this.product = product;
        this.store = store;
        this.scheduledOrderDate = scheduledOrderDate;
        this.quantity = quantity;
    }

    public ScheduledOrder.ScheduledOrderInfoTree toScheduledOrderInfoTree() {
        return new ScheduledOrder.ScheduledOrderInfoTree(
                this.getProduct().getReference(),
                this.getQuantity(),
                this.scheduledOrderDate.toString()
        );
    }

    @Getter
    public static class ScheduledOrderInfoTree extends RecursiveTreeObject<ScheduledOrderInfoTree> {
        private final LongProperty productReference;
        private final IntegerProperty quantity;
        private final StringProperty scheduledOrderDate;

        public ScheduledOrderInfoTree(Long productReference, Integer quantity, String scheduledOrderDate) {
            this.productReference = new SimpleLongProperty(productReference);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.scheduledOrderDate = new SimpleStringProperty(scheduledOrderDate);
        }
    }
}
