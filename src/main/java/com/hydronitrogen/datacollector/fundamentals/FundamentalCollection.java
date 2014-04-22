package com.hydronitrogen.datacollector.fundamentals;

import org.joda.time.DateTime;

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

    /**
     * Constructs a FilingCollection object from the provided filing.
     * @param filing the filing from which we are to gather the information.
     * @return
     */
    public static FundamentalCollection fromFilingAndXbrl(Filing filing, XbrlParser xbrl) {
        Context lastInstant = XbrlUtils.getLastInstantContextFromXbrl(xbrl);
        Context lastDuration = XbrlUtils.getLastDurationContextFromXbrl(xbrl);
        BalanceSheet balanceSheet = new BalanceSheet(xbrl, lastInstant);
        IncomeStatement incomeStatement = new IncomeStatement(xbrl, lastDuration);
        CashFlowsStatement cashFlowsStatement = new CashFlowsStatement(xbrl, lastDuration);
        return new FundamentalCollection(filing, balanceSheet, incomeStatement, cashFlowsStatement);
    }
}
