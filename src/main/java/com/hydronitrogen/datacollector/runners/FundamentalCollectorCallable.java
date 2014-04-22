package com.hydronitrogen.datacollector.runners;

import java.util.concurrent.Callable;

import com.hydronitrogen.datacollector.fundamentals.FundamentalCollection;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.SecImportServiceImpl;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * Given a SecImporterService and a Filing get the FundamentalCollection.
 * @author hkothari
 */
public final class FundamentalCollectorCallable implements Callable<FundamentalCollection> {

    private final SecImportServiceImpl secImportService;
    private final Filing filing;

    public FundamentalCollectorCallable(SecImportServiceImpl secImporterService, Filing filing) {
        this.secImportService = secImporterService;
        this.filing = filing;
    }

    @Override
    public FundamentalCollection call() throws Exception {
        XbrlParser xbrl = secImportService.getXbrlForFiling(filing);
        return FundamentalCollection.fromFilingAndXbrl(filing, xbrl);
    }

}
