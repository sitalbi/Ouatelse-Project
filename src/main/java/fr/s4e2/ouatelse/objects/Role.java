package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "role")
public class Role {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Permission> permissions;

    public Role(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
