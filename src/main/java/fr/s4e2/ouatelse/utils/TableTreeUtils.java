package fr.s4e2.ouatelse.utils;

import javafx.util.StringConverter;

public class TableTreeUtils {

    private TableTreeUtils() {
    }

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
}
