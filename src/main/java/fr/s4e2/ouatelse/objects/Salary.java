package fr.s4e2.ouatelse.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Salary table contains an identifier, a month, a gross salary, employer and employee charges, and a imposable state
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "salary")
public class Salary {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private Date date;

    @DatabaseField(canBeNull = false)
    private double grossSalary;

    @DatabaseField(canBeNull = false)
    private double netSalary;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;

    /**
     * Converts this object into a tree table object representing it's information (used for the salary management screen)
     *
     * @return A tree table object representing this object's information
     */
    public SalaryTree toUserSalaryTree() {
        return new SalaryTree(
                this.getId(),
                this.getDate(),
                this.getUser().getCredentials(),
                this.getUser().getRole(),
                this.getUser().getHoursPerWeek(),
                this.getGrossSalary(),
                this.getNetSalary()
        );
    }

    /**
     * Recursive Salary Tree
     */
    @Getter
    public static class SalaryTree extends RecursiveTreeObject<SalaryTree> {
        private final LongProperty id;
        private final StringProperty date;
        private final StringProperty credentials;
        private final StringProperty role;
        private final IntegerProperty hoursPerWeek;
        private final DoubleProperty grossSalary;
        private final DoubleProperty netSalary;

        /**
         * Constructor
         *
         * @param date         the date
         * @param credentials  the user's credentials
         * @param role         the user's role
         * @param hoursPerWeek the user's hours per week
         * @param grossSalary  the gross salary
         * @param netSalary    the net salary
         */
        public SalaryTree(long id, Date date, String credentials, Role role, Integer hoursPerWeek, Double grossSalary, Double netSalary) {
            this.id = new SimpleLongProperty(id);
            this.date = new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd").format(date));
            this.credentials = new SimpleStringProperty(credentials);
            this.role = new SimpleStringProperty(role != null ? role.getName() : "");
            this.hoursPerWeek = new SimpleIntegerProperty(hoursPerWeek);
            this.grossSalary = new SimpleDoubleProperty(grossSalary);
            this.netSalary = new SimpleDoubleProperty(netSalary);
        }
    }
}
