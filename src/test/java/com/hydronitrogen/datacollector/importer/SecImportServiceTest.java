package com.hydronitrogen.datacollector.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.net.ftp.FTP;
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

    private static final Path TEST_COMPANY_ZIP_PATH = Paths.get("edgar/full-index/2012/QTR3/company.zip");
    private static final String TEST_COMPANY_ZIP_FILE = "src/test/resources/company.idx.zip";
    private static final Path TEST_XBRL_PATH = Paths.get("edgar/data/1084869/000104746912008848/flws-20120701.xml");
    private static final String TEST_XBRL_FILE = "src/test/resources/flws-20120701.xml";

    private SecFileCacheService secFileCacheService;
    private SecImportServiceImpl secImportService;

    @Before
    public void setUp() throws FileNotFoundException {
        secFileCacheService = mock(SecFileCacheService.class);
        when(secFileCacheService.getFile(TEST_COMPANY_ZIP_PATH)).thenReturn(new FileInputStream(TEST_COMPANY_ZIP_FILE));
        when(secFileCacheService.getFile(TEST_XBRL_PATH, FTP.ASCII_FILE_TYPE)).thenReturn(
                new FileInputStream(TEST_XBRL_FILE));
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
