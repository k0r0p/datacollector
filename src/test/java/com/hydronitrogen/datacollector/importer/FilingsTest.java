package com.hydronitrogen.datacollector.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.junit.Test;

import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.FilingFilter;
import com.hydronitrogen.datacollector.importer.Filings;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;
import com.hydronitrogen.datacollector.xbrl.Context.Period;

/**
 * @author hkothari
 *
 */
public final class FilingsTest {

    private static final String TEST_COMPANY = "1 800 FLOWERS COM INC";
    private static final String TEST_FORM = "10-K";
    private static final String TEST_CIK = "1084869";
    private static final Date TEST_DATE = new GregorianCalendar(2012, Calendar.SEPTEMBER, 14).getTime();
    private static final String TEST_FILENAME = "edgar/data/1084869/0001047469-12-008848.txt";
    private static final Filing TEST_FILING = new Filing(TEST_COMPANY, TEST_FORM, TEST_CIK, TEST_DATE, TEST_FILENAME);

    private static final String TEST_CONTEXT_ID = "D2012";
    private static final Date TEST_CONTEXT_START = new GregorianCalendar(2011, Calendar.JULY, 4).getTime();
    private static final Date TEST_CONTEXT_END = new GregorianCalendar(2012, Calendar.JULY, 1).getTime();
    private static final Context TEST_CONTEXT = new Context(TEST_CONTEXT_ID, new Period(false, TEST_CONTEXT_START,
            TEST_CONTEXT_END));

    @Test
    public void testSimpleGetFilings() {
        Set<Filing> filings = Filings.getFilingList(2012, 3);
        assertEquals(203652, filings.size());
    }

    @Test
    public void testFilterFilings() {
        FilingFilter flowerFilter = new FilingFilter() {

            @Override
            public boolean accept(Filing filing) {
                return filing.getCik().equals(TEST_CIK);
            }
        };
        Set<Filing> filings = Filings.getFilingList(2012, 3);
        Set<Filing> flowersFilings = Filings.filterFilingList(filings, flowerFilter);
        assertEquals(12, flowersFilings.size());
        assertTrue(flowersFilings.contains(TEST_FILING));
    }

    @Test
    public void testGetXbrl() {
        XbrlParser parser = Filings.getXbrlForFiling(TEST_FILING);
        assertEquals(0.27, parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic", TEST_CONTEXT).get(), 0.000001);
    }
}
