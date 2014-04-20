package com.hydronitrogen.datacollector.fundamentals;

import java.util.Date;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class BalanceSheet extends XbrlBased {

    private static final String ASSETS_FIELD = "us-gaap:Assets";
    private static final String CURRENT_ASSETS_FIELD = "us-gaap:AssetsCurrent";
    // FIX: Use non-current or assets - current.
    private static final String NONCURRENT_ASSETS_FIELD = "us-gaap:AssetsNoncurrent";
    private static final Set<String> LIABILITIES_AND_EQUITY_FIELDS = ImmutableSet.of(
            "us-gaap:LiabilitiesAndStockholdersEquity", "us-gaap:LiabilitiesAndPartnersCapital");
    private static final String LIABILITIES_FIELD = "us-gaap:LiabilitiesAndPartnersCapital";
    private static final String CURRENT_LIABILITIES_FIELD = "us-gaap:LiabilitiesCurrent";
    // FIX: Use non-current or liabilities - current.
    private static final String NONCURRENT_LIABLITIES_FIELD = "us-gaap:LiabilitiesNoncurrent";
    private static final String COMMITMENTS_CONTINGENCIES_FIELD = "us-gaap:CommitmentsAndContingencies";
    private static final Set<String> TEMPORARY_EQUITY_FIELDS = ImmutableSet.of("us-gaap:TemporaryEquityRedemptionValue",
            "us-gaap:RedeemablePreferredStockCarryingAmount", "us-gaap:TemporaryEquityCarryingAmount",
            "us-gaap:TemporaryEquityValueExcludingAdditionalPaidInCapital",
            "us-gaap:TemporaryEquityCarryingAmountAttributableToParent",
            "us-gaap:RedeemableNoncontrollingInterestEquityFairValue");
    // FIX: Add to temporary equity
    private static final Set<String> REDEEMABLE_NONCONTROLLING_INTEREST_FIELDS = ImmutableSet.of(
            "us-gaap:RedeemableNoncontrollingInterestEquityCarryingAmount",
            "us-gaap:RedeemableNoncontrollingInterestEquityCommonCarryingAmount");
    private static final Set<String> EQUITY_FIELDS = ImmutableSet.of(
            "us-gaap:StockholdersEquityIncludingPortionAttributableToNoncontrollingInterest",
            "us-gaap:StockholdersEquity", "us-gaap:PartnersCapitalIncludingPortionAttributableToNoncontrollingInterest",
            "us-gaap:PartnersCapital", "us-gaap:CommonStockholdersEquity", "us-gaap:MemberEquity", "us-gaap:AssetsNet");
    private  static final Set<String> NONCONTROLLING_INTEREST_FIELDS = ImmutableSet.of("us-gaap:MinorityInterest",
            "us-gaap:PartnersCapitalAttributableToNoncontrollingInterest");
    private static final Set<String> EQUITY_ATTRIBUTABLE_TO_PARENT = ImmutableSet.of("us-gaap:StockholdersEquity",
            "us-gaap:LiabilitiesAndPartnersCapital");

    public BalanceSheet(XbrlParser source, Context context) {
        super(source, context);
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
        // TODO: apply fix
        return getOneOfFacts(EQUITY_FIELDS);
    }

    public Date getDate() {
        return getPeriod().getStartDate();
    }


}
