package com.hydronitrogen.datacollector.fundamentals;

import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class IncomeStatement extends XbrlBased {

    private static final Set<String> REVENUE_FIELDS = ImmutableSet.of("us-gaap:Revenues",
            "us-gaap:SalesRevenueNet", "us-gaap:SalesRevenueServicesNet",
            "us-gaap:RevenuesNetOfInterestExpense", "us-gaap:RegulatedAndUnregulatedOperatingRevenue",
            "us-gaap:HealthCareOrganizationRevenue", "us-gaap:InterestAndDividendIncomeOperating",
            "us-gaap:RealEstateRevenueNet", "us-gaap:RevenueMineralSales", "us-gaap:OilAndGasRevenue",
            "us-gaap:FinancialServicesRevenue", "us-gaap:RegulatedAndUnregulatedOperatingRevenue");
    private static final String GROSS_PROFIT_FIELD = "us-gaap:GrossProfit";
    private static final String EARNINGS_PER_SHARE_BASIC_FIELD = "us-gaap:EarningsPerShareBasic";

    public IncomeStatement(XbrlParser source, Context context) {
        super(source, context);
        assert !context.getPeriod().isInstant();
    }

    public Optional<Double> getRevenue() {
        return getOneOfFacts(REVENUE_FIELDS);
    }

    public Optional<Double> getGrossProfit() {
        return getDoubleFactValue(GROSS_PROFIT_FIELD);
    }

    public Optional<Double> getEarningsPerShareBasic() {
        return getDoubleFactValue(EARNINGS_PER_SHARE_BASIC_FIELD);
    }

    public DateTime getStartDate() {
        return getPeriod().getStartDate();
    }

    public DateTime getEndDate() {
        return getPeriod().getEndDate();
    }
}
