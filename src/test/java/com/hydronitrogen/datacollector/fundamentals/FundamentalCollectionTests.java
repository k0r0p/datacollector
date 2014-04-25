package com.hydronitrogen.datacollector.fundamentals;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.hydronitrogen.datacollector.caching.SecFileCacheService;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.SecImportServiceImpl;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 *
 * @author hkothari
 */
public final class FundamentalCollectionTests {

    private static final String TEST_COMPANY = "1 800 FLOWERS COM INC";
    private static final String TEST_FORM = "10-K";
    private static final String TEST_CIK = "1084869";
    private static final DateTime TEST_DATE = new DateTime(2012, 9, 14, 0, 0);
    private static final String TEST_FILENAME = "edgar/data/1084869/0001047469-12-008848.txt";
    private static final Filing TEST_FILING = new Filing(TEST_COMPANY, TEST_FORM, TEST_CIK, TEST_DATE, TEST_FILENAME);

    private static final String TEST_MOCK_FILE = "src/test/resources/flws-20120701.xml";

    private SecFileCacheService secFileCacheService;
    private SecImportServiceImpl secImportService;
    private XbrlParser testFilingXbrl;

    private ObjectMapper mapper;

    @Before
    public void setup() throws FileNotFoundException {
        mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());

        secFileCacheService = mock(SecFileCacheService.class);
        when(secFileCacheService.getFile(any(Path.class), any(Integer.class))).thenReturn(
                new FileInputStream(TEST_MOCK_FILE));

        secImportService = new SecImportServiceImpl(secFileCacheService);
        testFilingXbrl = secImportService.getXbrlForFiling(TEST_FILING);
    }

    @Test
    public void testSerializeFundamentalCollection() throws JsonProcessingException {
        FundamentalCollection collection = FundamentalCollection.fromFilingAndXbrl(TEST_FILING, testFilingXbrl);
        String json = mapper.writeValueAsString(collection);
        // TODO: (hkothari) this is lazy use org.skyscreamer or something.
        assertTrue(json.contains("\"earningsPerShareBasic\":0.27"));
    }
}
