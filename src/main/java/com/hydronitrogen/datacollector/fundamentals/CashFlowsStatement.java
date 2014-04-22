package com.hydronitrogen.datacollector.fundamentals;

import org.joda.time.DateTime;

import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class CashFlowsStatement extends XbrlBased {

    public CashFlowsStatement(Filing filing, XbrlParser source, Context context) {
        super(filing, source, context);
        // Assert context is duration
    }

    public DateTime getStartDate() {
        return getPeriod().getStartDate();
    }

    public DateTime getEndDate() {
        return getPeriod().getEndDate();
    }
}
