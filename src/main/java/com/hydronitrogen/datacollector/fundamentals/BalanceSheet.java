package com.hydronitrogen.datacollector.fundamentals;

import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class BalanceSheet extends XbrlBased {

    private static final String ASSETS_FIELD = "us-gaap:Assets";
    private static final String LIABILITIES_FIELD = "us-gaap:LiabilitiesAndPartnersCapital";
    private static final Set<String> EQUITY_FIELDS = ImmutableSet.of(
            "us-gaap:StockholdersEquityIncludingPortionAttributableToNoncontrollingInterest",
            "us-gaap:StockholdersEquity", "us-gaap:PartnersCapitalIncludingPortionAttributableToNoncontrollingInterest",
            "us-gaap:PartnersCapital", "us-gaap:CommonStockholdersEquity", "us-gaap:MemberEquity", "us-gaap:AssetsNet");

    public BalanceSheet(Filing filing, XbrlParser source, Context context) {
        super(filing, source, context);
        // Assert context is instant
        assert context.getPeriod().isInstant();
    }

    public Optional<Double> getAssets() {
        return getDoubleFactValue(ASSETS_FIELD);
    }

    public Optional<Double> getLiabilities() {
        return getDoubleFactValue(LIABILITIES_FIELD);
    }

    public Optional<Double> getEquity() {
        return getOneOfFacts(EQUITY_FIELDS);
    }

    public DateTime getDate() {
        return getPeriod().getStartDate();
    }

}
