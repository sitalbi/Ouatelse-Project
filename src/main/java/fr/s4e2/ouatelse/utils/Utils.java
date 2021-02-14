package fr.s4e2.ouatelse.utils;

public class Utils {

    public static Integer getNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
