package com.hydronitrogen.datacollector.importer;

import java.util.Date;
import java.util.Objects;

/**
 * @author hkothari
 *
 */
public final class Filing {
    private final String company;
    private final String form;
    private final String cik;
    private final Date date;
    private final String filename;

    public Filing(String company, String form, String cik, Date date, String filename) {
        this.company = company;
        this.form = form;
        this.cik = cik;
        this.date = date;
        this.filename = filename;
    }

    public String getForm() {
        return form;
    }

    public String getCik() {
        return cik;
    }

    public Date getDate() {
        return date;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, form, cik, date, filename);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof Filing) {
            Filing otherFiling = (Filing) other;
            boolean sameCompany = company.equals(otherFiling.company);
            boolean sameForm = form.equals(otherFiling.form);
            boolean sameCik = cik.equals(otherFiling.cik);
            boolean sameDate = date.equals(otherFiling.date);
            boolean sameFilename = filename.equals(otherFiling.filename);
            return sameCompany && sameForm && sameCik && sameDate && sameFilename;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s(%s) - %s filed on %s at %s", company, cik, form, date, filename);
    }
}
