package com.hydronitrogen.datacollector;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.hydronitrogen.datacollector.fundamentals.FundamentalCollection;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.FilingFilter;
import com.hydronitrogen.datacollector.importer.Filings;


/**
 *
 * @author hkothari
 */
public final class FundamentalCollector implements Callable<FundamentalCollection> {

    private static final Set<String> RELEVANT_FORMS = ImmutableSet.of("10-K", "10-Q");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ExecutorService jobExecutor = Executors.newFixedThreadPool(10);

    private final Filing filing;

    public FundamentalCollector(Filing filing) {
        this.filing = filing;
    }

    /**
     * Collects the fundamentals of the provided tickers/ciks and outputs them
     * to JSON.
     * @param args
     */
    public static void main(String[] args) {
        final Map<String, String> tickersToCik;
        try {
            TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
            tickersToCik = mapper.readValue(System.in, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Collect the list of filings
        Set<Filing> filings = Filings.getFilingsSince(2000);
        filings = Filings.filterFilingList(filings, new FilingFilter() {

            @Override
            public boolean accept(Filing filing) {
                return tickersToCik.keySet().contains(filing.getCik()) && RELEVANT_FORMS.contains(filing.getForm());
            }

        });
        // Process them in parallel
        List<Future<FundamentalCollection>> fundamentalCollections = Lists.newArrayList();
        for (Filing filing : filings) {
            Future<FundamentalCollection> fundamental = jobExecutor.submit(new FundamentalCollector(filing));
            fundamentalCollections.add(fundamental);
        }
        // TODO: Write the fundamental collections to JSON
    }

    @Override
    public FundamentalCollection call() {
        return FundamentalCollection.fromFiling(filing);
    }
}
