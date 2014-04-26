package com.hydronitrogen.datacollector;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.joda.time.DateTime;

import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.Context.Period;


/**
 * Useful constants for testing.
 * @author hkothari
 */
public final class TestConstants {

    public static final String FILING_COMPANY = "1 800 FLOWERS COM INC";
    public static final String FILING_FORM = "10-K";
    public static final String FILING_CIK = "1084869";
    public static final DateTime  FILING_DATE = new DateTime(2012, 9, 14, 0, 0);
    public static final String FILING_FILENAME = "edgar/data/1084869/0001047469-12-008848.txt";
    public static final Filing TEST_FILING = new Filing(FILING_COMPANY, FILING_FORM, FILING_CIK, FILING_DATE,
            FILING_FILENAME);

    public static final String DURATION_CONTEXT_ID = "D2012";
    public static final DateTime DURATION_CONTEXT_START = new DateTime(2011, 7, 4, 0, 0);
    public static final DateTime DURATION_CONTEXT_END = new DateTime(2012, 7, 1, 0, 0);
    public static final Context DURATION_CONTEXT = new Context(DURATION_CONTEXT_ID, new Period(false,
            DURATION_CONTEXT_START, DURATION_CONTEXT_END));

    public static final String INSTANT_CONTEXT_ID = "I2011";
    public static final DateTime INSTANT_CONTEXT_START = new DateTime(2011, 7, 3, 0, 0);
    public static final Context INSTANT_CONTEXT = new Context(INSTANT_CONTEXT_ID, new Period(true,
            INSTANT_CONTEXT_START, null));

    public static final Path FTP_COMPANY_ZIP_PATH = Paths.get("edgar/full-index/2012/QTR3/company.zip");
    public static final String FTP_XBRL_FILENAME = "flws-20120701.xml";
    public static final String FTP_XSD_FILENAME = "flws-20120701.xsd";
    public static final Path FTP_XBRL_PATH = Paths.get("edgar/data/1084869/000104746912008848/").resolve(
            FTP_XBRL_FILENAME);

    public static final String LOCAL_COMPANY_ZIP_FILE = "src/test/resources/company.idx.zip";
    public static final String LOCAL_XBRL_FILE = "src/test/resources/flws-20120701.xml";

    private TestConstants() {
        // Utility class -- do not instantiate.
    }
}
