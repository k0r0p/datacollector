package com.hydronitrogen.datacollector.xbrl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.hydronitrogen.datacollector.utils.XbrlUtils;

/**
 *
 * @author hkothari
 */
public final class XbrlUtilsTest {

    @Test
    public void testIsSimpleContext() {
        Context simpleYear = new Context("D2012", null);
        assertTrue(XbrlUtils.isSimpleContext(simpleYear));

        Context simpleQuarter = new Context("D2012Q3", null);
        assertTrue(XbrlUtils.isSimpleContext(simpleQuarter));

        Context simpleYTDQuarter = new Context("D2012Q3YTD", null);
        assertTrue(XbrlUtils.isSimpleContext(simpleYTDQuarter));

        Context notSimpleYear = new Context("I2012_SOMEFIELD_WHY_IS_THIS_HERE", null);
        assertTrue(!XbrlUtils.isSimpleContext(notSimpleYear));
    }

}
