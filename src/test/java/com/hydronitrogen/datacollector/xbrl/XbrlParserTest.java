package com.hydronitrogen.datacollector.xbrl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author hkothari
 *
 */
public final class XbrlParserTest {

    private Document xbrlDocument;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException {
        File lnkdData = new File("src/test/resources/lnkd-20130930.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        xbrlDocument = dBuilder.parse(lnkdData);
    }

    @Test
    public void testGetContexts() {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        Set<Context> contexts = parser.getContexts();
        assertEquals(205, contexts.size());
    }

    @Test
    public void testGetFactValue() throws ParseException {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        DateTime contextDate = new DateTime(2012, 12, 31, 0, 0);
        Context testContext = new Context("I2012Q4", new Context.Period(true, contextDate, null));
        String factValue = parser.getFactValue("us-gaap:AssetsFairValueDisclosure", testContext).get();
        assertEquals("646159000", factValue);
    }

    @Test
    public void testGetDoubleFactValue() throws ParseException {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        DateTime startDate = new DateTime(2012, 7, 1, 0, 0);
        DateTime endDate = new DateTime(2012, 9, 30, 0, 0);
        Context testContext = new Context("D2012Q3", new Context.Period(true, startDate, endDate));
        double factValue = parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic", testContext).get();
        assertEquals(0.02, factValue, 0.000001);
    }
}
