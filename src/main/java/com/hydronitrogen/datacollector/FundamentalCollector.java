package com.hydronitrogen.datacollector;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.hydronitrogen.datacollector.caching.SecFileCacheService;
import com.hydronitrogen.datacollector.caching.SecFileCacheServiceImpl;
import com.hydronitrogen.datacollector.fundamentals.BalanceSheet;
import com.hydronitrogen.datacollector.fundamentals.CashFlowsStatement;
import com.hydronitrogen.datacollector.fundamentals.FundamentalCollection;
import com.hydronitrogen.datacollector.fundamentals.IncomeStatement;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.FilingFilter;
import com.hydronitrogen.datacollector.importer.Filings;
import com.hydronitrogen.datacollector.importer.SecImportServiceImpl;
import com.hydronitrogen.datacollector.runners.FundamentalCollectorCallable;


/**
 *
 * @author hkothari
 */
public final class FundamentalCollector {

    private static final Set<String> RELEVANT_FORMS = ImmutableSet.of("10-K", "10-Q");
    private static final String BALANCE_SHEET_OPT = "b";
    private static final String INCOME_STATEMENT_OPT = "i";
    private static final String CASH_FLOWS_STATEMENT_OPT = "o";
    private static final String START_YEAR_OPT = "y";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SecFileCacheService secFileCacheService = new SecFileCacheServiceImpl();
    private static final SecImportServiceImpl secImportService = new SecImportServiceImpl(secFileCacheService);

    private FundamentalCollector() {
        // Main class -- do not instantiate.
    }

    /**
     * Collects the fundamentals of the provided tickers/ciks and outputs them
     * to JSON.
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cmd = getCommandLine(args);
        String balanceSheetName = cmd.getOptionValue(BALANCE_SHEET_OPT);
        String incomeStatementName = cmd.getOptionValue(INCOME_STATEMENT_OPT);
        String cashFlowsStatementName = cmd.getOptionValue(CASH_FLOWS_STATEMENT_OPT);
        int startYear = Integer.parseInt(cmd.getOptionValue(START_YEAR_OPT));

        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
        final Map<String, String> tickersToCik;
        try {
            tickersToCik = mapper.readValue(System.in, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<Filing> filings = getFilings(tickersToCik, startYear);
        List<FundamentalCollection> fundamentalCollections = getFundamentalCollections(filings);
        try {
            outputBalanceSheets(fundamentalCollections, new FileOutputStream(balanceSheetName));
            outputIncomeStatements(fundamentalCollections, new FileOutputStream(incomeStatementName));
            outputCashFlowsStatements(fundamentalCollections, new FileOutputStream(cashFlowsStatementName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandLine getCommandLine(String args[]) {
        Options options = new Options();
        options.addOption(BALANCE_SHEET_OPT, true, "balance sheet file");
        options.addOption(INCOME_STATEMENT_OPT, true, "income statement file");
        options.addOption(CASH_FLOWS_STATEMENT_OPT, true, "cash flows statement file");
        options.addOption(START_YEAR_OPT, true, "year to begin collecting filings from");

        CommandLineParser parser = new BasicParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    private static List<FundamentalCollection> getFundamentalCollections(Set<Filing> filings) {
        ExecutorService jobExecutor = Executors.newFixedThreadPool(10);
        // Process them in parallel
        List<Future<FundamentalCollection>> futureFundamentalCollections = Lists.newArrayList();
        for (Filing filing : filings) {
            Future<FundamentalCollection> fundamental = jobExecutor.submit(new FundamentalCollectorCallable(
                    secImportService, filing));
            futureFundamentalCollections.add(fundamental);
        }
        // Wait for them to finish
        List<FundamentalCollection> fundamentalCollections = Lists.newArrayList();
        for (Future<FundamentalCollection> future : futureFundamentalCollections) {
            try {
                fundamentalCollections.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return fundamentalCollections;
    }

    private static void outputBalanceSheets(List<FundamentalCollection> fundamentalCollections,
            OutputStream outputStream) {
        List<BalanceSheet> balanceSheets = Lists.newArrayList();
        for (FundamentalCollection collection : fundamentalCollections) {
            balanceSheets.add(collection.getBalanceSheet());
        }
        try {
            mapper.writeValue(outputStream, balanceSheets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void outputIncomeStatements(List<FundamentalCollection> fundamentalCollections,
            OutputStream outputStream) {
        List<IncomeStatement> incomeStatements = Lists.newArrayList();
        for (FundamentalCollection collection : fundamentalCollections) {
            incomeStatements.add(collection.getIncomeStatement());
        }
        try {
            mapper.writeValue(outputStream, incomeStatements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void outputCashFlowsStatements(List<FundamentalCollection> fundamentalCollections,
            OutputStream outputStream) {
        List<CashFlowsStatement> cashFlowsStatements = Lists.newArrayList();
        for (FundamentalCollection collection : fundamentalCollections) {
            cashFlowsStatements.add(collection.getCashFlowsStatement());
        }
        try {
            mapper.writeValue(outputStream, cashFlowsStatements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
