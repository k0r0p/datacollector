package com.hydronitrogen.datacollector.fundamentals;

import java.util.Date;

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

    public Date getStartDate() {
        return getPeriod().getStartDate();
    }

    public Date getEndDate() {
        return getPeriod().getEndDate();
    }
}
