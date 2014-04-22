package com.hydronitrogen.datacollector.fundamentals;

import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.utils.XbrlUtils;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * A class which gathers all the important fundamentals form
 * an individual filing.
 * @author hkothari
 */
public final class FundamentalCollection {

    private final BalanceSheet balanceSheet;
    private final IncomeStatement incomeStatement;
    private final CashFlowsStatement cashFlowsStatement;

    private FundamentalCollection(BalanceSheet balanceSheet, IncomeStatement incomeStatement,
            CashFlowsStatement cashFlowsStatement) {
        this.balanceSheet = balanceSheet;
        this.incomeStatement = incomeStatement;
        this.cashFlowsStatement = cashFlowsStatement;
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

    /**
     * Constructs a FilingCollection object from the provided filing.
     * @param filing the filing from which we are to gather the information.
     * @return
     */
    public static FundamentalCollection fromFilingAndXbrl(Filing filing, XbrlParser xbrl) {
        Context lastInstant = XbrlUtils.getLastInstantContextFromXbrl(xbrl);
        Context lastDuration = XbrlUtils.getLastDurationContextFromXbrl(xbrl);
        BalanceSheet balanceSheet = new BalanceSheet(filing, xbrl, lastInstant);
        IncomeStatement incomeStatement = new IncomeStatement(filing, xbrl, lastDuration);
        CashFlowsStatement cashFlowsStatement = new CashFlowsStatement(filing, xbrl, lastDuration);
        return new FundamentalCollection(balanceSheet, incomeStatement, cashFlowsStatement);
    }
}
