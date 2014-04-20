package com.hydronitrogen.datacollector.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility methods for formatting text.
 * @author hkothari
 */
public final class FormatUtils {

    private FormatUtils() {
        // Utility class -- do not instantiate.
    }

    // DateFormats are inherently unsafe for multithreaded use.
    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * Parses a date from a string and returns it in a thread safe manner.
     * @param dateString the date in "yyyy-MM-dd" form.
     * @return a java.util.Date
     * @throws ParseException if the date could not be parsed.
     */
    public static Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.get().parse(dateString);
    }

    /**
     * Converts a date to a string.
     * @param date the date to print.
     * @return a string in the form "yyyy-MM-dd"
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.get().format(date);
    }
}
