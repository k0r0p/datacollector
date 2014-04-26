package com.hydronitrogen.datacollector;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.hydronitrogen.datacollector.caching.SecFileCacheService;
import com.hydronitrogen.datacollector.caching.SecFileCacheServiceImpl;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.FilingFilter;
import com.hydronitrogen.datacollector.importer.Filings;
import com.hydronitrogen.datacollector.importer.SecImportServiceImpl;
import com.hydronitrogen.datacollector.runners.FactCollectorCallable;


/**
 *
 * @author hkothari
 */
public final class FactCollector {

    private static final Set<String> RELEVANT_FORMS = ImmutableSet.of("10-K", "10-Q");
    private static final String TEMPLATE_OPT = "t";
    private static final String OUTPUT_FILE_OPT = "o";
    private static final String START_YEAR_OPT = "y";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SecFileCacheService secFileCacheService = new SecFileCacheServiceImpl();
    private static final SecImportServiceImpl secImportService = new SecImportServiceImpl(secFileCacheService);

    private FactCollector() {
        // Main class -- do not instantiate.
    }

    /**
     * Collects the fundamentals of the provided tickers/ciks and outputs them
     * to JSON.
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cmd = getCommandLine(args);
        String templateFile = cmd.getOptionValue(TEMPLATE_OPT);
        String outputFile = cmd.getOptionValue(OUTPUT_FILE_OPT);
        int startYear = Integer.parseInt(cmd.getOptionValue(START_YEAR_OPT));

        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
        final Map<String, String> tickersToCik;
        try {
            tickersToCik = mapper.readValue(System.in, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, List<String>> fields;
        try {
            fields = getFields(new FileInputStream(templateFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Set<Filing> filings = getFilings(tickersToCik, startYear);
        List<Map<String, Object>> factCollections = getFactCollections(fields, filings);
        try {
            outputFacts(fields.keySet(), factCollections, new FileOutputStream(outputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandLine getCommandLine(String args[]) {
        Options options = new Options();
        options.addOption(TEMPLATE_OPT, true, "template file");
        options.addOption(OUTPUT_FILE_OPT, true, "output file");
        options.addOption(START_YEAR_OPT, true, "year to begin collecting filings from");

        CommandLineParser parser = new BasicParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, List<String>> getFields(InputStream inputStream) {
        // TODO: (hkothari) read into a class instead
        ImmutableMap.Builder<String, List<String>> fieldsMap = ImmutableMap.builder();
        JsonNode top;
        try {
            top = mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Iterator<Entry<String, JsonNode>> fields = top.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if (value.isArray()) {
                Iterator<JsonNode> valueElements = value.elements();
                ImmutableList.Builder<String> valuesBuilder = ImmutableList.builder();
                while(valueElements.hasNext()) {
                    valuesBuilder.add(valueElements.next().asText());
                }
                fieldsMap.put(key, valuesBuilder.build());
            } else if (value.isValueNode()) {
                fieldsMap.put(key, ImmutableList.of(value.asText()));
            }
        }
        return fieldsMap.build();
    }

    private static Set<Filing> getFilings(final Map<String, String> tickersToCik, int startYear) {
        Set<Filing> filings = secImportService.getFilingsSince(startYear);
        filings = Filings.filterFilingList(filings, new FilingFilter() {

            @Override
            public boolean accept(Filing filing) {
                return tickersToCik.keySet().contains(filing.getCik()) && RELEVANT_FORMS.contains(filing.getForm());
            }

        });
        return filings;
    }

    private static List<Map<String, Object>> getFactCollections(Map<String, List<String>> fields, Set<Filing> filings) {
        ExecutorService jobExecutor = Executors.newFixedThreadPool(10);
        // Process them in parallel
        List<Future<Map<String, Object>>> futureFactCollections = Lists.newArrayList();
        for (Filing filing : filings) {
            Future<Map<String, Object>> future = jobExecutor.submit(new FactCollectorCallable(
                    secImportService, fields, filing));
            futureFactCollections.add(future);
        }
        // Wait for them to finish
        List<Map<String, Object>> factCollections = Lists.newArrayList();
        for (Future<Map<String, Object>> future : futureFactCollections) {
            try {
                factCollections.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return factCollections;
    }

    private static void outputFacts(Set<String> columnNames, List<Map<String, Object>> factCollections,
            OutputStream outputStream) {
        // Build our CSV schema
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        for (String name : columnNames) {
            schemaBuilder.addColumn(name);
        }
        // Write the CSV.
        try {
            mapper.writer(schemaBuilder.build()).writeValue(outputStream, factCollections);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
