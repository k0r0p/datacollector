package com.hydronitrogen.datacollector.xbrl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hydronitrogen.datacollector.TestConstants;

/**
 * @author hkothari
 *
 */
public final class XbrlParserTest {

    private Document xbrlDocument;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException {
        File flwsData = new File(TestConstants.LOCAL_XBRL_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        xbrlDocument = dBuilder.parse(flwsData);
    }

    @Test
    public void testGetContexts() {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        Set<Context> contexts = parser.getContexts();
        assertEquals(238, contexts.size());
    }

    @Test
    public void testGetFactValue() throws ParseException {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        String factValue = parser.getFactValue("us-gaap:Assets",
                TestConstants.INSTANT_CONTEXT).get();
        assertEquals("259075000", factValue);
    }

    @Test
    public void testGetDoubleFactValue() throws ParseException {
        XbrlParser parser = new XbrlParser(xbrlDocument);
        double factValue = parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic",
                TestConstants.DURATION_CONTEXT).get();
        assertEquals(0.27, factValue, 0.000001);
    }
}
