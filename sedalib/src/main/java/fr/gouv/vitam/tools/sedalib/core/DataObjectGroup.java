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
package fr.gouv.vitam.tools.sedalib.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.gouv.vitam.tools.sedalib.metadata.SEDAMetadata;
import fr.gouv.vitam.tools.sedalib.metadata.management.LogBook;
import fr.gouv.vitam.tools.sedalib.metadata.namedtype.StringType;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibProgressLogger;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLEventReader;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLStreamWriter;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class DataObjectGroup.
 * <p>
 * Class for SEDA element DataObjectGroup. It contains logbook metadata and
 * links to contained DataObjects binary or physical.
 */
public class DataObjectGroup extends DataObjectPackageIdElement implements DataObject {

    // SEDA Elements
    /**
     * The BinaryDataObject list.
     */
    private List<BinaryDataObject> binaryDataObjectList;

    /**
     * The PhysicalDataObject list.
     */
    private List<PhysicalDataObject> physicalDataObjectList;

    /**
     * The LogBook xml element in String form.
     */
    public LogBook logBook;

    // Constructors

    /**
     * Instantiates a new DataObjectGroup.
     */
    public DataObjectGroup() {
        this(null, null);
    }

    /**
     * Instantiates a new DataObjectGroup.
     * <p>
     * If DataObjectPackage is defined the new ArchiveUnit is added with a generated
     * uniqID in the structure.
     *
     * @param dataObjectPackage the DataObjectPackage containing the DataObjectGroup
     * @param path              the path defining the ArchiveUnit onDisk
     *                          representation or null
     */
    public DataObjectGroup(DataObjectPackage dataObjectPackage, Path path) {
        super(dataObjectPackage);
        this.binaryDataObjectList = new ArrayList<>();
        this.physicalDataObjectList = new ArrayList<>();
        this.logBook = null;
        if (path == null)
            this.onDiskPath = null;
        else
            this.onDiskPath = path.toAbsolutePath().normalize();
        if (dataObjectPackage != null)
            try {
                dataObjectPackage.addDataObjectGroup(this);
            } catch (SEDALibException ignored) {
                // impossible as the uniqID is generated by the called function.
            }
    }

    // Methods

    /**
     * Adds a DataObject of BinaryDataObject or PhysicalDataObject nature.
     *
     * @param dataObject the DataObject
     */
    public void addDataObject(DataObject dataObject) {
        if (dataObject instanceof BinaryDataObject) {
            binaryDataObjectList.add((BinaryDataObject) dataObject);
            ((BinaryDataObject) dataObject).setDataObjectGroup(this);
        } else if (dataObject instanceof PhysicalDataObject) {
            physicalDataObjectList.add((PhysicalDataObject) dataObject);
            ((PhysicalDataObject) dataObject).setDataObjectGroup(this);
        }
    }

    /**
     * Remove the DataObject.
     *
     * @param zdo the DataObject
     * @return true, if successful
     */
    public boolean removeDataObject(DataObject zdo) {
        boolean result = false;
        if (zdo instanceof BinaryDataObject) {
            result = binaryDataObjectList.remove(zdo);
            if (result) getDataObjectPackage().getBdoInDataObjectPackageIdMap().remove(zdo.getInDataObjectPackageId());
        } else if (zdo instanceof PhysicalDataObject) {
            result = physicalDataObjectList.remove(zdo);
            if (result) getDataObjectPackage().getBdoInDataObjectPackageIdMap().remove(zdo.getInDataObjectPackageId());
        }
        return result;
    }

    /**
     * Find a contained DataObject by dataObjectVersion.
     *
     * @param dataObjectVersion the dataObjectVersion searched for
     * @return the DataObject or null if not found
     */
    public DataObject findDataObjectByDataObjectVersion(String dataObjectVersion) {
        for (BinaryDataObject bdo : binaryDataObjectList) {
            StringType bdoDataObjectVersion = bdo.getMetadataDataObjectVersion();
            if ((bdoDataObjectVersion != null) && (bdoDataObjectVersion.getValue().equals(dataObjectVersion)))
                return bdo;
        }
        for (PhysicalDataObject pdo : physicalDataObjectList) {
            StringType pdoDataObjectVersion = pdo.getMetadataDataObjectVersion();
            if ((pdoDataObjectVersion != null) && (pdoDataObjectVersion.getValue().equals(dataObjectVersion)))
                return pdo;
        }
        return null;
    }

    /**
     * Transfer all DataObjects from the given DataObjectGroup in this
     * DataObjectGroup, and merge LogBook elements.
     *
     * @param dataObjectGroup the DataObjectGroup
     * @throws SEDALibException when the LogBook content can't be merged
     */
    public void mergeDataObjectGroup(DataObjectGroup dataObjectGroup) throws SEDALibException {
        getDataObjectPackage().getDogInDataObjectPackageIdMap().remove(dataObjectGroup.getInDataObjectPackageId());
        for (BinaryDataObject bdo : dataObjectGroup.getBinaryDataObjectList()) {
            addDataObject(bdo);
        }
        for (PhysicalDataObject pdo : dataObjectGroup.getPhysicalDataObjectList()) {
            addDataObject(pdo);
        }
        // merge logbooks
        if (logBook == null)
            logBook = dataObjectGroup.logBook;
        else if (dataObjectGroup.logBook != null) {
            logBook = (LogBook) SEDAMetadata.fromString(logBook.toString().replace("</LogBook>", "") +
                    dataObjectGroup.logBook.toString().replace("<LogBook>", ""), LogBook.class);
        }
    }

    // SEDA XML exporter

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.gouv.vitam.tools.sedalib.core.DataObject#toSedaXml(fr.gouv.vitam.tools.
     * sedalib.xml.SEDAXMLStreamWriter)
     */
    public void toSedaXml(SEDAXMLStreamWriter xmlWriter, SEDALibProgressLogger sedaLibProgressLogger)
            throws SEDALibException, InterruptedException {
        try {
            xmlWriter.writeStartElement("DataObjectGroup");
            xmlWriter.writeAttribute("id", inDataPackageObjectId);
            for (BinaryDataObject bo : binaryDataObjectList) {
                bo.toSedaXml(xmlWriter, sedaLibProgressLogger);
            }
            for (PhysicalDataObject bo : physicalDataObjectList) {
                bo.toSedaXml(xmlWriter, sedaLibProgressLogger);
            }
            if (logBook != null) logBook.toSedaXml(xmlWriter);
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new SEDALibException(
                    "Erreur d'écriture XML du DataObjectGroup [" + inDataPackageObjectId + "]", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.gouv.vitam.tools.sedalib.core.DataObject#toSedaXmlFragments()
     */
    @Override
    public String toSedaXmlFragments() {
        if (logBook == null)
            return "";
        return logBook.toString();
    }

    // SEDA XML importer

    /**
     * Import the DataObjectGroup in XML expected form from the SEDA Manifest in the
     * ArchiveTransfer and return it's id in ArchiveTransfer.
     *
     * @param xmlReader             the SEDAXMLEventReader reading the SEDA manifest
     * @param dataObjectPackage     the DataObjectPackage to be completed
     * @param rootDir               the directory where the BinaryDataObject files are
     *                              exported
     * @param sedaLibProgressLogger the progress logger
     * @return the inDataPackageObjectId of the read DataObjectGroup, or null if not a DataObjectGroup
     * @throws SEDALibException     if the XML can't be read or the SEDA scheme is
     *                              not respected
     * @throws InterruptedException if export process is interrupted
     */
    public static String idFromSedaXml(SEDAXMLEventReader xmlReader, DataObjectPackage dataObjectPackage,
                                       String rootDir, SEDALibProgressLogger sedaLibProgressLogger) throws SEDALibException, InterruptedException {
        DataObjectGroup dog = null;
        BinaryDataObject bdo;
        PhysicalDataObject pdo;
        String tmp;
        boolean loop;
        try {
            tmp = xmlReader.peekAttributeBlockIfNamed("DataObjectGroup", "id");
            if (tmp != null) {
                dog = new DataObjectGroup();
                dog.inDataPackageObjectId = tmp;
                dataObjectPackage.addDataObjectGroup(dog);
                xmlReader.nextUsefullEvent();
                loop = true;
                while (loop) {
                    tmp = xmlReader.peekName();
                    if (tmp == null)
                        break;
                    switch (tmp) {
                        case "BinaryDataObject":
                            bdo = BinaryDataObject.fromSedaXml(xmlReader, dataObjectPackage, sedaLibProgressLogger);
                            StringType bdoDataObjectGroupId = (StringType) bdo.getFirstNamedMetadata("DataObjectGroupId");
                            StringType bdoDataObjectGroupReferenceId = (StringType) bdo.getFirstNamedMetadata("DataObjectGroupReferenceId");
                            if ((bdoDataObjectGroupId != null) || (bdoDataObjectGroupReferenceId != null))
                                throw new SEDALibException("Le BinaryDataObject [" + bdo.inDataPackageObjectId
                                        + "] utilise un raccordement DataObjectGroup mode SEDA2.0 "
                                        + "dans un DataObjectGroup mode SEDA2." + SEDA2Version.getSeda2Version());
                            StringType bdoUri = (StringType) bdo.getFirstNamedMetadata("Uri");
                            bdo.setOnDiskPathFromString(rootDir + File.separator + bdoUri.getValue());
                            dog.addDataObject(bdo);
                            break;
                        case "PhysicalDataObject":
                            pdo = PhysicalDataObject.fromSedaXml(xmlReader, dataObjectPackage, sedaLibProgressLogger);
                            StringType dataObjectGroupId=(StringType) pdo.getFirstNamedMetadata("DataObjectGroupId");
                            StringType dataObjectGroupReferenceId=(StringType) pdo.getFirstNamedMetadata("DataObjectGroupReferenceId");
                            if ((dataObjectGroupId != null) || (dataObjectGroupReferenceId != null))
                                throw new SEDALibException("Le PhysicalDataObject [" + pdo.inDataPackageObjectId
                                        + "] utilise un raccordement DataObjectGroup mode SEDA2.0 "
                                        + "dans un DataObjectGroup mode SEDA2."+SEDA2Version.getSeda2Version());
                            dog.addDataObject(pdo);
                            break;
                        default:
                            loop = false;
                    }
                }
                String nextElementName = xmlReader.peekName();
                if ((nextElementName != null) && (nextElementName.equals("LogBook"))) {
                    dog.logBook = (LogBook) SEDAMetadata.fromSedaXml(xmlReader, LogBook.class);
                }
                xmlReader.endBlockNamed("DataObjectGroup");
            }
        } catch (XMLStreamException | SEDALibException e) {
            throw new SEDALibException("Erreur de lecture du DataObjectGroup"
                    + (dog != null ? " [" + dog.inDataPackageObjectId + "]" : ""), e);
        }
        //case not a DataObjectGroup
        if (dog == null)
            return null;

        return dog.inDataPackageObjectId;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.gouv.vitam.tools.sedalib.core.DataObject#fromSedaXmlFragments(java.lang.
     * String)
     */
    public void fromSedaXmlFragments(String fragments) throws SEDALibException {
        DataObjectGroup dog = new DataObjectGroup();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(fragments.getBytes(StandardCharsets.UTF_8));
             SEDAXMLEventReader xmlReader = new SEDAXMLEventReader(bais, true)) {
            // jump StartDocument
            xmlReader.nextUsefullEvent();
            String nextElementName = xmlReader.peekName();
            if ((nextElementName != null) && (nextElementName.equals("LogBook"))) {
                dog.logBook = (LogBook) SEDAMetadata.fromSedaXml(xmlReader, LogBook.class);
            }
        } catch (XMLStreamException | SEDALibException | IOException e) {
            throw new SEDALibException("Erreur de lecture du DataObjectGroup", e);
        }

        this.logBook = dog.logBook;
    }

    // Getters and setters

    /**
     * Gets the BinaryDataObject list.
     *
     * @return the BinaryDataObject list
     */
    public List<BinaryDataObject> getBinaryDataObjectList() {
        return this.binaryDataObjectList;
    }

    /**
     * Sets the BinaryDataObject list.
     *
     * @param binaryDataObjectList the new BinaryDataObject list
     */
    public void setBinaryDataObjectList(List<BinaryDataObject> binaryDataObjectList) {
        this.binaryDataObjectList = binaryDataObjectList;
    }

    /**
     * Gets the PhysicalDataObject list.
     *
     * @return the PhysicalDataObject list
     */
    public List<PhysicalDataObject> getPhysicalDataObjectList() {
        return physicalDataObjectList;
    }

    /**
     * Sets the PhysicalDataObject list.
     *
     * @param physicalDataObjectList the new PhysicalDataObject list
     */
    public void setPhysicalDataObjectList(List<PhysicalDataObject> physicalDataObjectList) {
        this.physicalDataObjectList = physicalDataObjectList;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.gouv.vitam.tools.sedalib.core.DataObject#getDataObjectGroup()
     */
    @Override
    @JsonIgnore
    public DataObjectGroup getDataObjectGroup() {
        return this;
    }

    /**
     * Gets log book.
     *
     * @return the log book
     */
    public LogBook getLogBook() {
        return logBook;
    }

    /**
     * Sets log book.
     *
     * @param logBook the log book
     */
    public void setLogBook(LogBook logBook) {
        this.logBook = logBook;
    }
}
