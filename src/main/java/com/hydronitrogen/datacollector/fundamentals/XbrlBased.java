package com.hydronitrogen.datacollector.fundamentals;

import java.util.Set;

import com.google.common.base.Optional;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;
import com.hydronitrogen.datacollector.xbrl.Context.Period;

/**
 * @author hkothari
 *
 */
public abstract class XbrlBased {

    private final XbrlParser source;
    private final Context context;

    protected XbrlBased(XbrlParser source, Context context) {
        this.source = source;
        this.context = context;
    }

    protected Period getPeriod() {
        return context.getPeriod();
    }

    protected Optional<Double> getOneOfFacts(Set<String> factNames) {
        Optional<Double> factValue = Optional.absent();
        for (String factName : factNames) {
            factValue = getDoubleFactValue(factName);
            if (factValue.isPresent()) {
                return factValue;
            }
        }
        return factValue;
    }

    protected Optional<Double> getDoubleFactValue(String factName) {
        return source.getDoubleFactValue(factName, context);
    }
}
