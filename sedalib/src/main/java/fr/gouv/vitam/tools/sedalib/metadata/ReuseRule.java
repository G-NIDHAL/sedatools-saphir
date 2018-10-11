/**
 * Copyright French Prime minister Office/DINSIC/Vitam Program (2015-2019)
 *
 * contact.vitam@programmevitam.fr
 * 
 * This software is developed as a validation helper tool, for constructing Submission Information Packages (archives 
 * sets) in the Vitam program whose purpose is to implement a digital archiving back-office system managing high 
 * volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA archiveDeliveryRequestReply the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */

package fr.gouv.vitam.tools.sedalib.metadata;

import java.util.List;

import fr.gouv.vitam.tools.sedalib.metadata.namedtype.RuleType;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLEventReader;

/**
 * The Class ReuseRule.
 * <p>
 * Class for SEDA element ReuseRule
 * <p>
 * A management ArchiveUnit metadata.
 * <p>
 * Standard quote: "Gestion de la réutilisation"
 */
public class ReuseRule extends RuleType {

	/**
	 * Instantiates a new reuse rule.
	 */
	public ReuseRule() {
		super("ReuseRule");
	}

	/**
	 * Instantiates a new reuse rule, with one rule and a date.
	 *
	 * @param rule        the rule
	 * @param startDate   the start date
	 */
	public ReuseRule(String rule, String startDate) {
		super("ReuseRule", rule, startDate);
	}

	/**
	 * Instantiates a new reuse rule form args.
	 *
	 * @param elementName the XML element name (here "ReuseRule")
	 * @param args        the generic args for metadata construction
	 * @throws SEDALibException if args are not suitable for constructor
	 */
	public ReuseRule(String elementName, Object[] args) throws SEDALibException {
		super("ReuseRule", args);
	}

	/**
	 * Import the AccessRule in XML expected form for the SEDA Manifest.
	 *
	 * @param xmlReader the SEDAXMLEventReader reading the SEDA manifest
	 * @return the read Content
	 * @throws SEDALibException if the XML can't be read or the SEDA scheme is not
	 *                          respected
	 */
	public static ReuseRule fromSedaXml(SEDAXMLEventReader xmlReader) throws SEDALibException {
		ReuseRule reuseRule=new ReuseRule();
		reuseRule=(ReuseRule) fromSedaXmlInObject(xmlReader,reuseRule);
		return reuseRule;
	}

	@Override
	public List<String> getFinalActionList() {
		return null;
	}

}
