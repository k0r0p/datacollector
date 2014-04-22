package com.hydronitrogen.datacollector.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.hydronitrogen.datacollector.caching.SecFileCacheService;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.Context.Period;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class SecImportServiceTest {

    private static final String TEST_COMPANY = "1 800 FLOWERS COM INC";
    private static final String TEST_FORM = "10-K";
    private static final String TEST_CIK = "1084869";
    private static final DateTime TEST_DATE = new DateTime(2012, 9, 14, 0, 0);
    private static final String TEST_FILENAME = "edgar/data/1084869/0001047469-12-008848.txt";
    private static final Filing TEST_FILING = new Filing(TEST_COMPANY, TEST_FORM, TEST_CIK, TEST_DATE, TEST_FILENAME);

    private static final String TEST_CONTEXT_ID = "D2012";
    private static final DateTime TEST_CONTEXT_START = new DateTime(2011, 7, 4, 0, 0);
    private static final DateTime TEST_CONTEXT_END = new DateTime(2012, 7, 1, 0, 0);
    private static final Context TEST_CONTEXT = new Context(TEST_CONTEXT_ID, new Period(false, TEST_CONTEXT_START,
            TEST_CONTEXT_END));

    private final SecFileCacheService secFileCacheService = new MockSecFileCacheService();
    private SecImportServiceImpl secImportService;

    @Before
    public void setUp() {
        secImportService = new SecImportServiceImpl(secFileCacheService);
    }

    @Test
    public void testSimpleGetFilings() {
        Set<Filing> filings = secImportService.getFilingList(2012, 3);
        assertTrue(filings.contains(TEST_FILING));
    }

    @Test
    public void testFilterFilings() {
        FilingFilter flowerFilter = new FilingFilter() {

            @Override
            public boolean accept(Filing filing) {
                return filing.getCik().equals(TEST_CIK);
            }
        };
        Set<Filing> filings = secImportService.getFilingList(2012, 3);
        Set<Filing> flowersFilings = Filings.filterFilingList(filings, flowerFilter);
        assertEquals(12, flowersFilings.size());
        assertTrue(flowersFilings.contains(TEST_FILING));
    }

    @Test
    public void testGetXbrl() {
        XbrlParser parser = secImportService.getXbrlForFiling(TEST_FILING);
        assertEquals(0.27, parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic", TEST_CONTEXT).get(), 0.000001);
    }
}
