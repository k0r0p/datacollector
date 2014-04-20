package com.hydronitrogen.secscraper.xbrl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

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
		Date contextDate = new SimpleDateFormat("yyyy-MM-dd").parse("2012-12-31");
		Context testContext = new Context("I2012Q4", new Context.Period(true, contextDate, null));
		String factValue = parser.getFactValue("us-gaap:AssetsFairValueDisclosure", testContext).get();
		assertEquals("646159000", factValue);
	}

	@Test
	public void testGetDoubleFactValue() throws ParseException {
		XbrlParser parser = new XbrlParser(xbrlDocument);
		Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-01");
		Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2012-09-30");
		Context testContext = new Context("D2012Q3", new Context.Period(true, startDate, endDate));
		double factValue = parser.getDoubleFactValue("us-gaap:EarningsPerShareBasic", testContext).get();
		assertEquals(0.02, factValue, 0.000001);
	}
}
