package com.hydronitrogen.datacollector.importer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.hydronitrogen.datacollector.utils.FormatUtils;
import com.hydronitrogen.datacollector.utils.SecFtpUtils;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class Filings {

    private static final String FILING_LIST_PATH = "edgar/full-index/%d/QTR%d/company.zip";
    private static final String COMPANY_IDX_FILENAME = "company.idx";

    private Filings() {
        // Util class do not instantiate
    }

    /**
     * Gets a list of Filing objects from the provided year and 1 indexed quarter.
     * @param year the year to gather filings from.
     * @param quarter the quarter to gather from [1,2,3,4]
     * @return a Set of Filing objects.
     */
    public static Set<Filing> getFilingList(int year, int quarter) {
        Path filingPath = Paths.get(String.format(FILING_LIST_PATH, year, quarter));
        String indexFile = getIndexFileContents(filingPath);
        return splitIndexFile(indexFile);
    }

    /**
     * Gets all the filings since the provided year
     * @param year
     * @return
     */
    public static Set<Filing> getFilingsSince(int year) {
        ImmutableSet.Builder<Filing> builder = ImmutableSet.builder();
        // TODO: (hkothari) fix this
        for (int currentYear = year; year <= 2014; currentYear++) {
            for(int currentQuarter = 1; currentQuarter <= 4; currentQuarter++) {
                builder.addAll(getFilingList(currentYear, currentQuarter));
            }
        }
        return builder.build();
    }

    /**
     * Filters the provided set of filings down to ones which are acceptable
     * according to the provided filing filter.
     * @param filings the set of filings to be filtered.
     * @param filter the filter which determines the filings to be removed/included.
     * @return a new set of filings which have been appropriately filtered.
     */
    public static Set<Filing> filterFilingList(Set<Filing> filings, FilingFilter filter) {
        Set<Filing> filtered = Sets.newHashSet();
        for (Filing filing : filings) {
            if (filter.accept(filing)) {
                filtered.add(filing);
            }
        }
        return filtered;
    }

    /**
     * Gets the corresponding XBRL file for a given filing and prepares
     * it for inspection.
     * @param filing the filing document which we would like to read from.
     * @return an XbrlParser corresponding to the given filing.
     * @throws NullPointerException if the filing was not found.
     */
    public static XbrlParser getXbrlForFiling(Filing filing) throws NullPointerException {
        Path filename = Paths.get(filing.getFilename());
        String filenameNoTxt = Files.getNameWithoutExtension(filename.getFileName().toString());
        String dirname = filenameNoTxt.replace("-", "");
        Path fullDir = filename.getParent().resolve(dirname);
        FTPClient client = SecFtpUtils.getSecConnection();
        FTPFile[] files = SecFtpUtils.listFilesInDirectory(client, fullDir);
        // Find the xsd file
        String xbrlName = null;
        for (FTPFile file : files) {
            if (file.getName().endsWith(".xsd")) {
                xbrlName = Files.getNameWithoutExtension(file.getName()) + ".xml";
            }
        }

        // Parse and return XBRL
        Preconditions.checkNotNull(xbrlName, "No XBRL xml file was found for this filing");
        InputStream is = SecFtpUtils.readFileFromSec(client, fullDir.resolve(xbrlName), FTP.ASCII_FILE_TYPE);
        try {
            Document xbrlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            return new XbrlParser(xbrlDocument);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getIndexFileContents(Path filingPath) {
        FTPClient client = SecFtpUtils.getSecConnection();
        InputStream is = SecFtpUtils.readFileFromSec(client, filingPath, FTP.BINARY_FILE_TYPE);
        ZipInputStream zip = null;
        try {
            zip = new ZipInputStream(is);
            ZipEntry next = zip.getNextEntry();
            while (next != null) {
                if (next.getName().equals(COMPANY_IDX_FILENAME)) {
                    return IOUtils.toString(zip);
                }
            }
            throw new RuntimeException("The provided filing path did not contain an index file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(zip);
        }
    }

    private static String removeNonAscii(String string) {
        return string.replaceAll("[^\\x00-\\x7F]", "");
    }

    private static Date parseIndexDate(String date) {
        try {
            return FormatUtils.parseDate(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<Filing> splitIndexFile(String indexFileContents) {
        Iterable<String> lines = Splitter.on('\n').split(removeNonAscii(indexFileContents));
        // Skip the first 10 lines because they don't have useful entries
        Iterable<String> actualEntries = Iterables.skip(lines, 10);
        Set<Filing> filings = Sets.newHashSet();
        for (String entry : actualEntries) {
            // HACKHACK: FIXME: SOME JANKY SHIT HAPPENS HERE
            if (!entry.trim().isEmpty()) {
                String company = entry.substring(0, 62).trim();
                String form = entry.substring(62, 74).trim();
                String cik = entry.substring(74, 86).trim();
                Date date = parseIndexDate(entry.substring(86, 98).trim());
                String filename = entry.substring(98).trim();
                filings.add(new Filing(company, form, cik, date, filename));
            }
        }
        return filings;
    }
}
