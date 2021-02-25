package fr.s4e2.ouatelse.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    /*
        Use cases :
            - String is null = null
            - String contains a number = number
            - String does not contain a number = null
            - String contains not only a number = null
            - String contains multiple numbers = null
     */
    @Test
    void getNumber() {
        Integer result;

        result = Utils.getNumber(null);
        assertNull(result);

        result = Utils.getNumber("1337");
        assertEquals(result, 1337);

        result = Utils.getNumber("This String does not contain any number");
        assertNull(result);

        result = Utils.getNumber("This String does not contain only 4096");
        assertNull(result);

        result = Utils.getNumber("1337 4096");
        assertNull(result);
    }

    /*
        Use cases :
            - date to convert is null
            - date to convert is valid
     */
    @Test
    void dateToLocalDate() {
        assertThrows(NullPointerException.class, () -> Utils.dateToLocalDate(null));

        LocalDate localDate = Utils.dateToLocalDate(new Date(15));
        assertEquals(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "01/01/1970");
    }

    /*
        Use cases :
            - local date to convert is not valid
            - local date to convert is valid
     */
    @Test
    void localDateToDate() {
        Date date = new Date(15);
        LocalDate localDate = Utils.dateToLocalDate(date);

        assertThrows(NullPointerException.class, () -> Utils.localDateToDate(null));

        date = Utils.localDateToDate(localDate);
        assertEquals(date.toString(), "Thu Jan 01 00:00:00 CET 1970");
    }
}