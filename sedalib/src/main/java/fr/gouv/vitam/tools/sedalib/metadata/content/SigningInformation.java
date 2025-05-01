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
 * circulated by CEA, CNRS and INRIA archiveTransfer the following URL "http://www.cecill.info".
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

import fr.gouv.vitam.tools.sedalib.metadata.namedtype.*;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Class SigningInformation.
 * <p>
 * Class for SEDA element SigningInformation.
 * <p>
 * An ArchiveUnit metadata.
 * <p>
 * Standard quote: "Contient toutes les informations relatives à la signature"
 */
public class SigningInformation extends ComplexListType {

    /**
     * Init metadata map.
     */
    @ComplexListMetadataMap(seda2Version = {3})
    public static final Map<String, ComplexListMetadataKind> metadataMap;

    static {
        metadataMap = new LinkedHashMap<>();
        metadataMap.put("SigningRole", new ComplexListMetadataKind(EnumType.class, true));
        metadataMap.put("DetachedSigningRole", new ComplexListMetadataKind(EnumType.class, true));
        metadataMap.put("SignedDocumentReferenceId", new ComplexListMetadataKind(SIPInternalIDType.class, true));
        metadataMap.put("SignatureDescription", new ComplexListMetadataKind(SignatureDescription.class, true));
        metadataMap.put("TimestampingInformation", new ComplexListMetadataKind(TimestampingInformation.class, true));
        metadataMap.put("AdditionalProof", new ComplexListMetadataKind(AdditionalProof.class, false));
        metadataMap.put("Extended", new ComplexListMetadataKind(AnyXMLListType.class, false));
    }

    // Constructors

    /**
     * Instantiates a new file info.
     */
    public SigningInformation() {
        super("SigningInformation");
    }

    /**
     * Instantiates a new signing information block with metadata.
     *
     * @param signingRole      the role of the signer
     * @param sipInternalID    the unique reference ID of the signed document
     * @param signerFirstName  the first name of the signer
     * @param signerBirthName  the birth name of the signer
     * @param signingTime      the date and time of signing
     * @param signerIdentifier the unique identifier of the signer
     * @param signingType      the type of the signature
     * @throws SEDALibException if sub-elements construction fails
     */
    public SigningInformation(String signingRole, String sipInternalID, String signerFirstName, String signerBirthName, LocalDateTime signingTime, String signerIdentifier,
                              String signingType) throws SEDALibException {
        super("SigningInformation");


        if (signingRole != null) addNewMetadata("SigningRole", signingRole);
        if (sipInternalID != null) addNewMetadata("SignedDocumentReferenceId", sipInternalID);
        if (signerFirstName != null ||
                signerBirthName != null || signingTime != null || signerIdentifier != null ||
                signingType != null) {
            addNewMetadata("SignatureDescription", signerFirstName, signerBirthName, signingTime, signerIdentifier, null, null, signingType);
        }
        if (signingTime != null) addNewMetadata("TimestampingInformation", signingTime, null);
    }
}
