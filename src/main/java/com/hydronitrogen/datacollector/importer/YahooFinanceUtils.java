package com.hydronitrogen.datacollector.importer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

/**
 * @author hkothari
 *
 */
public final class YahooFinanceUtils {

    private YahooFinanceUtils() {
        // Utility class -- do not instantiate.
    }

    // s = TICKER, a = start month 2 digits, b = start day 2 digits, c = start year
    // d = end month 2 digits, e = end day 2 digits, f = end year
    private static final String CSV_DOWNLOAD_URL = "http://ichart.finance.yahoo.com/table.csv?"
            + "s=%s&a=%02d&b=%02d&c=%d&d=%02d&e=%02d&f=%d&g=d&ignore=.csv";

    /**
     * Gets the historical price data from Yahoo Finance.
     * @param ticker the stock ticker to look up
     * @param startDate the date from which to begin prices.
     * @param endDate the last date of prices to include.
     * @return a String of a CSV of historical prices.
     */
    public static String getHistoricalPrices(String ticker, DateTime startDate, DateTime endDate) {
        String downloadUrl = String.format(CSV_DOWNLOAD_URL, ticker, startDate.getMonthOfYear(),
                startDate.getDayOfMonth(), startDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth(),
                endDate.getYear());
        URL url;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            return IOUtils.toString(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
