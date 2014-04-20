package com.hydronitrogen.datacollector.importer;

/**
 * @author hkothari
 *
 */
public interface FilingFilter {

    /**
     * A filter method to determine whether the filing should be included.
     * @param filing the filing we want to test.
     * @return true if the filing should be included, false otherwise.
     */
    boolean accept(Filing filing);

}
