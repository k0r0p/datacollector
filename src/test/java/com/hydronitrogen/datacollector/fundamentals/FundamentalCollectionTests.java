package com.hydronitrogen.datacollector.fundamentals;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.hydronitrogen.datacollector.importer.Filing;
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
    private static final XbrlParser TEST_FILING_XBRL = null;

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new GuavaModule());
    }

    @Test
    public void testSerializeFundamentalCollection() throws JsonProcessingException {
        FundamentalCollection collection = FundamentalCollection.fromFilingAndXbrl(TEST_FILING, TEST_FILING_XBRL);
        String json = mapper.writeValueAsString(collection);
        // TODO: (hkothari) this is lazy use org.skyscreamer or something.
        assertTrue(json.contains("\"earningsPerShareBasic\":0.27"));
    }
}
