package com.hydronitrogen.datacollector.importer;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author hkothari
 *
 */
public final class Filings {


    private Filings() {
        // Util class do not instantiate
    }

    /**
     * Filters the provided set of filings down to ones which are acceptable
     * according to the provided filing filter.
     * @param filings the set of filings to be filtered.
     * @param filter the filter which determines the filings to be removed/included.
     * @return a new set of filings which have been appropriately filtered.
     */
    public static Set<Filing> filterFilingList(Set<Filing> filings, FilingFilter filter) {
        Set<Filing> filtered = Sets.newHashSet();
        for (Filing filing : filings) {
            if (filter.accept(filing)) {
                filtered.add(filing);
            }
        }
        return filtered;
    }





}
