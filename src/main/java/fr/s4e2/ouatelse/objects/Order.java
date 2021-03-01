package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "order")
public class Order {

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<Product, Integer> products;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Store store;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Vendor vendor;
}
