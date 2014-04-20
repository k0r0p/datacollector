package com.hydronitrogen.datacollector.fundamentals;

import java.util.Set;

import org.joda.time.DateTime;

import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.Filings;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * A class which gathers all the important fundamentals form
 * an individual filing and puts them in a form to be output by
 * JSON.
 * @author hkothari
 */
public final class FundamentalCollection {

    private final Filing filing;
    private final BalanceSheet balanceSheet;
    private final IncomeStatement incomeStatement;
    private final CashFlowsStatement cashFlowsStatement;

    private FundamentalCollection(Filing filing, BalanceSheet balanceSheet, IncomeStatement incomeStatement,
            CashFlowsStatement cashFlowsStatement) {
        this.filing = filing;
        this.balanceSheet = balanceSheet;
        this.incomeStatement = incomeStatement;
        this.cashFlowsStatement = cashFlowsStatement;
    }

    public DateTime getDateFiled() {
        return filing.getDate();
    }

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }

    public IncomeStatement getIncomeStatement() {
        return incomeStatement;
    }

    public CashFlowsStatement getCashFlowsStatement() {
        return cashFlowsStatement;
    }

    private static boolean isSimpleContext(Context context) {
        return context.getId().matches("[DI]\\d{4}");
    }

    private static Context getLastInstantContextFromXbrl(XbrlParser xbrl) {
        Set<Context> contexts = xbrl.getContexts();
        Context newestContext = null;
        for (Context context : contexts) {
            if (context.getPeriod().isInstant() && isSimpleContext(context)) {
                DateTime newStart = context.getPeriod().getStartDate();
                if (newestContext == null || newStart.isAfter(newestContext.getPeriod().getStartDate())) {
                    newestContext = context;
                }
            }
        }
        return newestContext;
    }

    private static Context getLastDurationContextFromXbrl(XbrlParser xbrl) {
        Set<Context> contexts = xbrl.getContexts();
        Context newestContext = null;
        for (Context context : contexts) {
            if (!context.getPeriod().isInstant() && isSimpleContext(context)) {
                DateTime newEnd = context.getPeriod().getEndDate();
                if (newestContext == null || newEnd.isAfter(newestContext.getPeriod().getEndDate())) {
                    newestContext = context;
                }
            }
        }
        return newestContext;
    }

    /**
     * Constructs a FilingCollection object from the provided filing.
     * @param filing the filing from which we are to gather the information.
     * @return
     */
    public static FundamentalCollection fromFiling(Filing filing) {
        XbrlParser xbrl = Filings.getXbrlForFiling(filing);
        Context lastInstant = getLastInstantContextFromXbrl(xbrl);
        Context lastDuration = getLastDurationContextFromXbrl(xbrl);
        BalanceSheet balanceSheet = new BalanceSheet(xbrl, lastInstant);
        IncomeStatement incomeStatement = new IncomeStatement(xbrl, lastDuration);
        CashFlowsStatement cashFlowsStatement = new CashFlowsStatement(xbrl, lastDuration);
        return new FundamentalCollection(filing, balanceSheet, incomeStatement, cashFlowsStatement);
    }
}
