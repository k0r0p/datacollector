package com.hydronitrogen.datacollector.utils;

import java.text.ParseException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility methods for formatting text.
 * @author hkothari
 */
public final class FormatUtils {

    private FormatUtils() {
        // Utility class -- do not instantiate.
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * Parses a date from a string and returns it in a thread safe manner.
     * @param dateString the date in "yyyy-MM-dd" form.
     * @return a java.util.Date
     * @throws ParseException if the date could not be parsed.
     */
    public static DateTime parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parseDateTime(dateString);
    }

    /**
     * Converts a date to a string.
     * @param date the date to print.
     * @return a string in the form "yyyy-MM-dd"
     */
    public static String formatDate(DateTime date) {
        return DATE_FORMAT.print(date);
    }
}
