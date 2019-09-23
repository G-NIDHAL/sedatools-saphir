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
package fr.gouv.vitam.tools.resip.threads;

import fr.gouv.vitam.tools.resip.app.ResipGraphicApp;
import fr.gouv.vitam.tools.resip.data.Work;
import fr.gouv.vitam.tools.resip.frame.InOutDialog;
import fr.gouv.vitam.tools.resip.utils.ResipException;
import fr.gouv.vitam.tools.resip.utils.ResipLogger;
import fr.gouv.vitam.tools.sedalib.core.ArchiveTransfer;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibProgressLogger;

import javax.swing.*;

import static fr.gouv.vitam.tools.sedalib.utils.SEDALibProgressLogger.GLOBAL;
import static fr.gouv.vitam.tools.sedalib.utils.SEDALibProgressLogger.doProgressLogWithoutInterruption;

/**
 * The type Check profile thread.
 */
public class CheckProfileThread extends SwingWorker<String, InOutDialog> {
    //input
    private String profileFileName;
    private InOutDialog inOutDialog;
    //run output
    private Exception exitException;
    // logger
    private SEDALibProgressLogger spl;

    /**
     * Instantiates a new Check profile thread.
     *
     * @param profileFileName the profile file name
     * @param dialog          the dialog
     */
    public CheckProfileThread(String profileFileName, InOutDialog dialog) {
        this.profileFileName = profileFileName;
        this.inOutDialog = dialog;
        this.exitException = null;
        this.spl = null;
        dialog.setThread(this);
    }

    @Override
    public String doInBackground() {
        Work work = ResipGraphicApp.getTheApp().currentWork;
        try {
            spl = new SEDALibProgressLogger(ResipLogger.getGlobalLogger().getLogger(), SEDALibProgressLogger.OBJECTS_GROUP, (count, log) -> {
                String newLog = inOutDialog.extProgressTextArea.getText() + "\n" + log;
                inOutDialog.extProgressTextArea.setText(newLog);
                inOutDialog.extProgressTextArea.setCaretPosition(newLog.length());
            }, 1000, 2);

            if (work == null)
                throw new ResipException("Pas de contenu à valider");

            // first verify and reindex if neccesary
            if (work.getExportContext().isReindex()) {
                work.getDataObjectPackage().regenerateContinuousIds();
                ResipGraphicApp.getTheApp().mainWindow.treePane.allTreeChanged();
            }

            ArchiveTransfer archiveTransfer = new ArchiveTransfer();
            work.getDataObjectPackage().setManagementMetadataXmlData(work.getExportContext().getManagementMetadataXmlData());
            archiveTransfer.setDataObjectPackage(work.getDataObjectPackage());
            archiveTransfer.setGlobalMetadata(work.getExportContext().getArchiveTransferGlobalMetadata());

            if (profileFileName == null) {
                archiveTransfer.seda21Validate(spl);
            } else {
                archiveTransfer.sedaProfileValidate(profileFileName, spl);
            }
        } catch (Exception e) {
            exitException = e;
        }
        return "OK";
    }

    @Override
    protected void done() {
        JTextArea progressTextArea = inOutDialog.extProgressTextArea;
        ResipGraphicApp theApp = ResipGraphicApp.getTheApp();

        inOutDialog.okButton.setEnabled(true);
        inOutDialog.cancelButton.setEnabled(false);
        if (isCancelled())
            doProgressLogWithoutInterruption(spl, GLOBAL,"resip: validation annulée", null);
        else if (exitException != null)
            doProgressLogWithoutInterruption(spl, GLOBAL,"resip: erreur durant la validation", exitException);
        else
            doProgressLogWithoutInterruption(spl, GLOBAL,"resip: validation OK", null);
    }
}
