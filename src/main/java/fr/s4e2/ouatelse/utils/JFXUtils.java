package fr.s4e2.ouatelse.utils;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JFXUtils {

    /**
     * Empty Constructor
     */
    private JFXUtils() {
    }

    /**
     * Returns a StringConverter for a double
     *
     * @return StringConverter for a Double
     */
    public static StringConverter<Double> getDoubleConverter() {
        return new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.toString();
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException ignored) {
                    return 0.0D;
                }
            }
        };
    }

    /**
     * Returns a StringConverter for a local date
     *
     * @return StringConverter for a LocalDate
     */
    public static StringConverter<LocalDate> getDateConverter() {
        return new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        };
    }
}
