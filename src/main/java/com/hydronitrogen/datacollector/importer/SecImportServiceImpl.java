package com.hydronitrogen.datacollector.importer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.net.ftp.FTP;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.hydronitrogen.datacollector.caching.SecFtpService;
import com.hydronitrogen.datacollector.utils.FormatUtils;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 *
 * @author hkothari
 */
public final class SecImportServiceImpl {

    private static final String FILING_LIST_PATH = "edgar/full-index/%d/QTR%d/company.zip";
    private static final String COMPANY_IDX_FILENAME = "company.idx";

    private final SecFtpService secFtpService;

    public SecImportServiceImpl(SecFtpService secFtpService) {
        this.secFtpService = secFtpService;
    }

    /**
     * Gets a list of Filing objects from the provided year and 1 indexed quarter.
     * @param year the year to gather filings from.
     * @param quarter the quarter to gather from [1,2,3,4]
     * @return a Set of Filing objects.
     */
    public Set<Filing> getFilingList(int year, int quarter) {
        Path filingPath = Paths.get(String.format(FILING_LIST_PATH, year, quarter));
        InputStream indexFileStream = getIndexFileContents(filingPath);
        return splitIndexFile(indexFileStream);
    }

    /**
     * Gets all the filings since the provided year
     * @param year
     * @return
     */
    public Set<Filing> getFilingsSince(int year) {
        ImmutableSet.Builder<Filing> builder = ImmutableSet.builder();
        // TODO: (hkothari) fix this
        for (int currentYear = year; currentYear <= 2014; currentYear++) {
            for(int currentQuarter = 1; currentQuarter <= 4; currentQuarter++) {
                builder.addAll(getFilingList(currentYear, currentQuarter));
            }
        }
        return builder.build();
    }

    /**
     * Gets the corresponding XBRL file for a given filing and prepares
     * it for inspection.
     * @param filing the filing document which we would like to read from.
     * @return an XbrlParser corresponding to the given filing.
     * @throws NullPointerException if the filing was not found.
     */
    public XbrlParser getXbrlForFiling(Filing filing) throws NullPointerException {
        Path filename = Paths.get(filing.getFilename());
        String filenameNoTxt = Files.getNameWithoutExtension(filename.getFileName().toString());
        String dirname = filenameNoTxt.replace("-", "");
        Path fullDir = filename.getParent().resolve(dirname);

        // Find the xsd file
        List<String> files = secFtpService.getDirectory(fullDir);
        String xbrlName = null;
        for (String file : files) {
            if (file.endsWith(".xsd")) {
                xbrlName = Files.getNameWithoutExtension(file) + ".xml";
            }
        }

        // Parse and return XBRL
        Preconditions.checkNotNull(xbrlName, "No XBRL xml file was found for this filing");
        InputStream is = secFtpService.getFile(fullDir.resolve(xbrlName), FTP.ASCII_FILE_TYPE);
        try {
            Document xbrlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            return new XbrlParser(xbrlDocument);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getIndexFileContents(Path filingPath) {
        InputStream is = secFtpService.getFile(filingPath);
        try {
            ZipInputStream zip = new ZipInputStream(is);
            ZipEntry next = zip.getNextEntry();
            while (next != null) {
                if (next.getName().equals(COMPANY_IDX_FILENAME)) {
                    return zip;
                }
            }
            throw new RuntimeException("The provided filing path did not contain an index file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DateTime parseIndexDate(String date) {
        try {
            return FormatUtils.parseDate(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }



    private static Set<Filing> splitIndexFile(InputStream indexFileStream) {
        Scanner lineReader = new Scanner(indexFileStream);
        // Skip the first 10 lines because they don't have useful entries
        for (int i = 0; i < 10; i++) {
            lineReader.nextLine();
        }
        Set<Filing> filings = Sets.newHashSet();
        while (lineReader.hasNextLine()) {
            String entry = lineReader.nextLine();
            // HACKHACK: FIXME: SOME JANKY SHIT HAPPENS HERE
            if (!entry.trim().isEmpty()) {
                String company = entry.substring(0, 62).trim();
                String form = entry.substring(62, 74).trim();
                String cik = entry.substring(74, 86).trim();
                DateTime date = parseIndexDate(entry.substring(86, 98).trim());
                String filename = entry.substring(98).trim();
                filings.add(new Filing(company, form, cik, date, filename));
            }
        }
        lineReader.close();
        return filings;
    }
}
