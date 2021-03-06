package fr.s4e2.ouatelse.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Multitude of reusable and useful functions
 */
public class Utils {

    /**
     * Empty Constructor
     */
    private Utils() {
    }

    /**
     * Returns an integer from a string
     *
     * @param s the String to be changed
     * @return the integer value or null in the case of failure
     */
    public static Integer getNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns a double from a string
     *
     * @param s the String to be changed
     * @return the double value or null in the case of failure
     */
    public static Double getDouble(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Allows you to convert a Date to a LocalDate
     *
     * @param dateToConvert the Date to be converted
     * @return the LocalDate
     */
    public static LocalDate dateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Allows you to convert a LocalDate to a Date
     *
     * @param dateToConvert the LocalDate to be converted
     * @return the Date
     */
    public static Date localDateToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
