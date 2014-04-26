package com.hydronitrogen.datacollector.xbrl;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Optional;
import com.hydronitrogen.datacollector.utils.FormatUtils;

/**
 * @author hkothari
 */
public final class XbrlParser {

    private final Document xbrlDocument;

    public XbrlParser(Document xbrlDocument) {
        this.xbrlDocument = xbrlDocument;
    }

    private Context.Period parsePeriodFromContext(Element contextElement) throws DOMException, ParseException {
        Element startDateElement = (Element) contextElement.getElementsByTagName("xbrli:startDate").item(0);
        Element endDateElement = (Element) contextElement.getElementsByTagName("xbrli:endDate").item(0);
        Element instantElement = (Element) contextElement.getElementsByTagName("xbrli:instant").item(0);

        if (instantElement != null) {
            DateTime instantDate = FormatUtils.parseDate(instantElement.getTextContent());
            return new Context.Period(true, instantDate, null);
        } else {
            DateTime startDate = FormatUtils.parseDate(startDateElement.getTextContent());
            DateTime endDate = FormatUtils.parseDate(endDateElement.getTextContent());
            return new Context.Period(false, startDate, endDate);
        }

    }

    /**
     * Gets a set of all the contexts within the document.
     * @return a Set of Context objects, which may be empty if there are no contexts.
     */
    public Set<Context> getContexts() {
        NodeList contexts = xbrlDocument.getElementsByTagName("xbrli:context");
        Set<Context> parsed = new HashSet<Context>();
        for (int i = 0; i < contexts.getLength(); i++) {
            Element contextElement = (Element) contexts.item(i);
            String id = contextElement.getAttribute("id");

            Context context;
            try {
                context = new Context(id, parsePeriodFromContext(contextElement));
            } catch (DOMException | ParseException e) {
                throw new RuntimeException(e);
            }
            parsed.add(context);
        }
        return parsed;
    }

    /**
     * Returns the an optional of the given fact's value or absent if it's
     * not present within the provided context.
     * @param factName the fact to search for, eg. us-gaap:ShareholdersEquity
     * @param context the context of which to return the value for.
     * @return the value of the fact if present or Optional.absent()
     */
    public Optional<String> getFactValue(String factName, Context context) {
        NodeList facts = xbrlDocument.getElementsByTagName(factName);
        for (int i = 0; i < facts.getLength(); i++) {
            Element fact = (Element) facts.item(i);
            if (context.getId().equals(fact.getAttribute("contextRef"))) {
                return Optional.of(fact.getTextContent());
            }
        }
        return Optional.absent();
    }

    /**
     * Gets the value of the given fact as a double if present and double.
     * @param factName the name of the fact to search for, eg. us-gaap:ShareholdersEquity
     * @param context the context of which to return the value for.
     * @return the Double value of the fact or Optional.absent()
     * @throws NumberFormatException if the found value isn't a double.
     */
    public Optional<Double> getDoubleFactValue(String factName, Context context) throws NumberFormatException {
        Optional<String> factValue = getFactValue(factName, context);
        if (factValue.isPresent()) {
            return Optional.of(Double.parseDouble(factValue.get()));
        } else {
            return Optional.absent();
        }
    }

    /**
     * Gets the value of one of the provided facts (whichever is first)
     * as a double if one is present and a double.
     * @param factNames the collection of the names to search from.
     * @param context the context of which to return the value for.
     * @return the Double value of one of the facts or Optional.absent()
     * @throws NumberFormatException if a value is found but not a double.
     */
    public Optional<Double> getOneOfDoubleFactValue(Collection<String> factNames, Context context) {
        Optional<Double> factValue = Optional.absent();
        for (String factName : factNames) {
            factValue = getDoubleFactValue(factName, context);
            if (factValue.isPresent()) {
                return factValue;
            }
        }
        return factValue;
    }
}
