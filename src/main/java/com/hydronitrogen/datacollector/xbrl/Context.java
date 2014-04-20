package com.hydronitrogen.datacollector.xbrl;

import java.util.Date;

import com.hydronitrogen.datacollector.utils.FormatUtils;

/**
 * Represents a period of time in a XBRL statement
 * identified with a unique ID.
 * @author hkothari
 */
public final class Context {

	private final String id;
	private final Period period;

	public Context(String id, Period period) {
		this.id = id;
		this.period = period;
	}

	/**
	 * A representation of a time period, either instant or contained
	 * which this Context covers.
	 * @author hkothari
	 */
	public static class Period {

		private final boolean instant;
		private final Date startDate;
		private final Date endDate;

		public Period(boolean instant, Date startDate, Date endDate) {
			this.instant = instant;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		@Override
        public String toString() {
		    String representation = instant ? "I" : "D";
		    representation += "(" + FormatUtils.formatDate(startDate);
		    if (endDate != null) {
		        representation += "," + FormatUtils.formatDate(endDate);
		    }
		    representation += ")";
		    return representation;
		}

		public boolean isInstant() {
		    return instant;
		}

		public Date getStartDate() {
		    return startDate;
		}

		public Date getEndDate() {
		    return endDate;
		}
	}

    public String getId() {
        return id;
    }

    public Period getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return id + " " + period.toString();
    }
}
