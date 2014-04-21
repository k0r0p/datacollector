package com.hydronitrogen.datacollector.utils;

import java.util.Set;

import org.joda.time.DateTime;

import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 *
 * @author hkothari
 */
public final class XbrlUtils {

    private XbrlUtils() {
        // Utility class -- do not instantiate.
    }

    /**
     * Returns whether the context
     * @param context
     * @return
     */
    public static boolean isSimpleContext(Context context) {
        return context.getId().matches("[DI]\\d{4}(Q[1-4])?(YTD)?");
    }

    /**
     * Gets the last Instant Context which is of the form [DI]\d{4}
     * @param xbrl
     * @return
     */
    public static Context getLastInstantContextFromXbrl(XbrlParser xbrl) {
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

    public static Context getLastDurationContextFromXbrl(XbrlParser xbrl) {
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

}
