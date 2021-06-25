/**
 * Copyright French Prime minister Office/DINSIC/Vitam Program (2015-2019)
 * <p>
 * contact.vitam@programmevitam.fr
 * <p>
 * This software is developed as a validation helper tool, for constructing Submission Information Packages (archives
 * sets) in the Vitam program whose purpose is to implement a digital archiving back-office system managing high
 * volumetry securely and efficiently.
 * <p>
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA archiveDeliveryRequestReply the following URL "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 * <p>
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 * <p>
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */
package fr.gouv.vitam.tools.sedalib.metadata.content;

import fr.gouv.vitam.tools.sedalib.metadata.namedtype.NamedTypeMetadata;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLEventReader;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLStreamWriter;

import javax.xml.stream.XMLStreamException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class Rule.
 * <p>
 * For Rule/StartDate couple in SEDA management rules.
 * <p>
 * WARNING: this class is unusual as it doesn't represent one XML element but one or two concatanated XML elements
 */
public class Rule extends NamedTypeMetadata {

    /**
     * The rule id.
     */
    private String ruleID;

    /**
     * The start date.
     */
    private LocalDate startDate;

    /**
     *  Other Metdadata
     */
    private Map<String, Object> otherMetadata;

    /**
     * Instantiates a new rule element.
     */
    public Rule() {
        this(null, null, new TreeMap<>());
    }

    /**
     * Instantiates a new rule element.
     *
     * @param ruleID    the rule id
     * @param startDate the start date
     */
    public Rule(String ruleID, LocalDate startDate) {
        this(ruleID, startDate, new TreeMap<>());
    }

    /**
     * Instantiates a new rule element.
     *
     * @param ruleID    the rule id
     * @param startDate the start date
     * @param otherMetadata the other metadata
     */
    public Rule(String ruleID, LocalDate startDate, Map<String, Object> otherMetadata) {
        super("Rule");
        this.ruleID = ruleID;
        this.startDate = startDate;
        this.otherMetadata = otherMetadata;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.gouv.vitam.tools.sedalib.metadata.SEDAMetadata#toSedaXml(fr.gouv.vitam.
     * tools.sedalib.xml.SEDAXMLStreamWriter)
     */
    public void toSedaXml(SEDAXMLStreamWriter xmlWriter) throws SEDALibException {
        try {
            xmlWriter.writeElementValue("Rule", ruleID);
            if (startDate != null)
                xmlWriter.writeElementValue("StartDate", SEDAXMLStreamWriter.getStringFromDate(startDate));
            for(Map.Entry<String,Object> entry : otherMetadata.entrySet()) {
               if(entry.getValue() instanceof LocalDate) {
                   xmlWriter.writeElementValue(entry.getKey(), SEDAXMLStreamWriter.getStringFromDate((LocalDate) entry.getValue()));
               }else  {
                   xmlWriter.writeElementValue(entry.getKey(), entry.getValue().toString());
               }
            }
        } catch (XMLStreamException e) {
            throw new SEDALibException("Erreur d'écriture XML dans un élément de règle dans RuleType", e);
        }
    }

    @Override
    public LinkedHashMap<String, String> toCsvList() throws SEDALibException {
        throw new SEDALibException("Méthode d'écriture des Csv inadaptée pour un RuleElement");
    }

    /**
     * To csv list linked hash map, for a specific rule position.
     *
     * @param i the rule position
     * @return the linked hash map
     * @throws SEDALibException the seda lib exception
     */
    public LinkedHashMap<String, String> toCsvList(int i) throws SEDALibException {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        result.put("Rule." + i, ruleID);
        if (startDate != null)
            result.put("StartDate." + i, SEDAXMLStreamWriter.getStringFromDate(startDate));
        return result;
    }

    /**
     * Import the metadata content in XML expected form from the SEDA Manifest.
     *
     * @param xmlReader the SEDAXMLEventReader reading the SEDA manifest
     * @return true, if it finds something convenient, false if not
     * @throws SEDALibException if the XML can't be read or the SEDA scheme is not respected, for example
     */
    public boolean fillFromSedaXml(SEDAXMLEventReader xmlReader) throws SEDALibException {
        String tmp, tmpDate;
        LocalDate startDate;
        try {
            tmp = xmlReader.nextValueIfNamed("Rule");
            if (tmp != null) {
                tmpDate = xmlReader.nextValueIfNamed("StartDate");
                if (tmpDate == null)
                    startDate = null;
                else try {
                    startDate = SEDAXMLEventReader.getDateFromString(tmpDate);
                } catch (DateTimeParseException e) {
                    throw new SEDALibException("La date est mal formatée", e);
                }
                this.ruleID = tmp;
                this.startDate = startDate;
            } else
                return false;
        } catch (XMLStreamException | IllegalArgumentException | SEDALibException e) {
            throw new SEDALibException("Erreur de lecture XML dans un élément de règle dans RuleType", e);
        }
        return true;
    }

    /**
     * Gets rule id.
     *
     * @return the rule id
     */
    public String getRuleID() {
        return ruleID;
    }

    /**
     * Sets rule id.
     *
     * @param ruleID the rule id
     */
    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets other metadata.
     *
     * @return the other metadata
     */
    public Map<String, Object> getOtherMetadata() {
        return otherMetadata;
    }

    /**
     * Sets other metadata.
     *
     * @param otherMetadata the other metadata
     */
    public void setOtherMetadata(Map<String, Object> otherMetadata) {
        this.otherMetadata = otherMetadata;
    }

    public void addOtherMetadata(String metadata, Object value) {
        this.otherMetadata.put(metadata, value);
    }
}
