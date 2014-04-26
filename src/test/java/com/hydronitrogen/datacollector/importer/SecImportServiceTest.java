package com.hydronitrogen.datacollector.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import org.apache.commons.net.ftp.FTP;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hydronitrogen.datacollector.TestConstants;
import com.hydronitrogen.datacollector.caching.SecFtpService;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class SecImportServiceTest {

    private SecFtpService secFtpService;
    private SecImportServiceImpl secImportService;

    @Before
    public void setUp() throws FileNotFoundException {
        secFtpService = mock(SecFtpService.class);
        when(secFtpService.getFile(TestConstants.FTP_COMPANY_ZIP_PATH)).thenReturn(
                new FileInputStream(TestConstants.LOCAL_COMPANY_ZIP_FILE));
        when(secFtpService.getFile(TestConstants.FTP_XBRL_PATH, FTP.ASCII_FILE_TYPE)).thenReturn(
                new FileInputStream(TestConstants.LOCAL_XBRL_FILE));
        when(secFtpService.getDirectory(TestConstants.FTP_XBRL_PATH.getParent())).thenReturn(
                Lists.newArrayList(TestConstants.FTP_XBRL_FILENAME, TestConstants.FTP_XSD_FILENAME));
        secImportService = new SecImportServiceImpl(secFtpService);
    }

    @Test
    public void testSimpleGetFilings() {
        Set<Filing> filings = secImportService.getFilingList(2012, 3);
        assertTrue(filings.contains(TestConstants.TEST_FILING));
    }

    @Test
    public void testFilterFilings() {
        FilingFilter flowerFilter = new FilingFilter() {

            @Override
            public boolean accept(Filing filing) {
                return filing.getCik().equals(TestConstants.FILING_CIK);
            }
        };
        Set<Filing> filings = secImportService.getFilingList(2012, 3);
        Set<Filing> flowersFilings = Filings.filterFilingList(filings, flowerFilter);
        assertEquals(12, flowersFilings.size());
        assertTrue(flowersFilings.contains(TestConstants.TEST_FILING));
    }

    @Test
    public void testGetXbrl() {
        XbrlParser parser = secImportService.getXbrlForFiling(TestConstants.TEST_FILING);
        assertEquals(0.27, parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic",
                TestConstants.DURATION_CONTEXT).get(), 0.000001);
    }
}
