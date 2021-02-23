package fr.s4e2.ouatelse.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilsTest {


    /*
        Use cases :
            - String is null = null
            - String contains a number = number
            - String does not contain a number = null
            - String contains not only a number = null
            - String contains multiple numbers = null
     */
    @org.junit.jupiter.api.Test
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
            - date to convert is not valid
            - date to convert is valid
     */
    @org.junit.jupiter.api.Test
    void dateToLocalDate() {
        Date date = new Date();
        LocalDate localDate;

        localDate = Utils.dateToLocalDate(date);
        assertEquals(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "23/02/2021");
    }

    /*
        Use cases :
            - local date to convert is not valid
            - local date to convert is valid
     */
    @org.junit.jupiter.api.Test
    void localDateToDate() {
        Date date = new Date();
        LocalDate localDate;
        localDate = Utils.dateToLocalDate(date);

        date = Utils.localDateToDate(localDate);
        assertEquals(date.toString(), "Tue Feb 23 00:00:00 CET 2021");
    }
}