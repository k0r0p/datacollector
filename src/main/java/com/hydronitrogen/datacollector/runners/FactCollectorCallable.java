package com.hydronitrogen.datacollector.runners;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.hydronitrogen.datacollector.importer.Filing;
import com.hydronitrogen.datacollector.importer.SecImportServiceImpl;
import com.hydronitrogen.datacollector.utils.XbrlUtils;
import com.hydronitrogen.datacollector.xbrl.Context;
import com.hydronitrogen.datacollector.xbrl.XbrlParser;

/**
 * Given a SecImporterService and a Filing get the FundamentalCollection.
 * @author hkothari
 */
public final class FactCollectorCallable implements Callable<Map<String, Object>> {

    private final SecImportServiceImpl secImportService;
    private final Map<String, List<String>> fields;
    private final Filing filing;

    public FactCollectorCallable(SecImportServiceImpl secImporterService, Map<String, List<String>> fields,
            Filing filing) {
        this.secImportService = secImporterService;
        this.fields = fields;
        this.filing = filing;
    }

    @Override
    public Map<String, Object> call() throws Exception {
        XbrlParser xbrl = secImportService.getXbrlForFiling(filing);
        // HACKHACK: THIS WILL FAIL AT SOME POINT GUARANTEED
        Context lastInstant = XbrlUtils.getLastInstantContextFromXbrl(xbrl);
        Context lastDuration = XbrlUtils.getLastDurationContextFromXbrl(xbrl);

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        String filingDate = filing.getDate().toString("yyyy-MM-dd");
        builder.put("filingDate", filingDate);
        for (Map.Entry<String, List<String>> field : fields.entrySet()) {
            Optional<Double> instantValue = xbrl.getOneOfDoubleFactValue(field.getValue(), lastInstant);
            if (instantValue.isPresent()) {
                builder.put(field.getKey(), instantValue.get());
            } else {
                Optional<Double> durationValue = xbrl.getOneOfDoubleFactValue(field.getValue(), lastDuration);
                if (durationValue.isPresent()) {
                    builder.put(field.getKey(), instantValue.get());
                }
            }
        }
        return builder.build();
    }

}
