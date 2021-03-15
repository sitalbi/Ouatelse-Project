package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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
    private ProductStock productStock;

    @DatabaseField(canBeNull = false)
    private Date scheduledOrderDate;
}
