package com.hydronitrogen.datacollector.fundamentals;

import org.joda.time.DateTime;

import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * @author hkothari
 *
 */
public final class CashFlowsStatement extends XbrlBased {

    public CashFlowsStatement(XbrlParser source, Context context) {
        super(source, context);
        // Assert context is duration
    }

    public DateTime getStartDate() {
        return getPeriod().getStartDate();
    }

    public DateTime getEndDate() {
        return getPeriod().getEndDate();
    }
}
