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
package fr.gouv.vitam.tools.resip.sedaobjecteditor;

import fr.gouv.vitam.tools.resip.app.ResipGraphicApp;
import fr.gouv.vitam.tools.resip.frame.BigTextEditDialog;
import fr.gouv.vitam.tools.resip.sedaobjecteditor.components.structuredcomponents.AutomaticGrowingTextArea;
import fr.gouv.vitam.tools.resip.sedaobjecteditor.components.structuredcomponents.SEDAObjectEditorSimplePanel;
import fr.gouv.vitam.tools.sedalib.metadata.SEDAMetadata;
import fr.gouv.vitam.tools.sedalib.metadata.data.Relationship;
import fr.gouv.vitam.tools.sedalib.metadata.namedtype.TextType;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import static fr.gouv.vitam.tools.resip.sedaobjecteditor.SEDAObjectEditorConstants.translateTag;

/**
 * The TextType object editor class.
 */
public class RelationshipEditor extends SEDAObjectEditor {

    /**
     * The editedObject target attribute edition graphic component
     */
    private JTextField attributeTargetField;

    /**
     * The editedObject type attribute edition graphic component
     */
    private JTextField attributeTypeField;

    /**
     * The graphic elements
     */
    private JLabel beforeLabel, innerLabel;
    private JButton targetButton;
    private JButton typeButton;
    private GridBagLayout labelGBL;
    private int targetWidth, typeWidth;

    /**
     * Instantiates a new TextType editor.
     *
     * @param metadata the TextType editedObject
     * @param father   the father
     * @throws SEDALibException if not a TextType editedObject
     */
    public RelationshipEditor(SEDAMetadata metadata, SEDAObjectEditor father) throws SEDALibException {
        super(metadata, father);
        if (!(metadata instanceof Relationship))
            throw new SEDALibException("La métadonnée à éditer n'est pas du bon type");
    }

    private Relationship getRelationshipMetadata() {
        return (Relationship) editedObject;
    }

    /**
     * Gets TextType sample.
     *
     * @param elementName the element name, corresponding to the XML tag in SEDA
     * @param minimal     the minimal flag, if true subfields are selected and values are empty, if false all subfields are added and values are default values
     * @return the seda editedObject sample
     * @throws SEDALibException the seda lib exception
     */
    static public SEDAMetadata getSEDAMetadataSample(String elementName, boolean minimal) throws SEDALibException {
        return new Relationship();
    }

    @Override
    public SEDAMetadata extractEditedObject() throws SEDALibException {
        String target = attributeTargetField.getText();
        if (target.isEmpty()) target = null;
        String type = attributeTypeField.getText();
        if (type.isEmpty()) type = null;
        getRelationshipMetadata().setTarget(target);
        getRelationshipMetadata().setType(type);
        return getRelationshipMetadata();
    }

    @Override
    public String getSummary() throws SEDALibException {
        String result = "";
        if ((attributeTargetField.getText() != null) && !attributeTargetField.getText().isEmpty())
            result = "(" + attributeTargetField.getText() + ")";
        return result;
    }

    @Override
    public void createSEDAObjectEditorPanel() throws SEDALibException {
        JPanel labelPanel = new JPanel();
        GridBagLayout gbl;

        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        targetWidth = (int) SEDAObjectEditor.LABEL_FONT.getStringBounds("ID00000", frc).getWidth();
        typeWidth = (int) SEDAObjectEditor.LABEL_FONT.getStringBounds("0123456789", frc).getWidth();

        labelGBL = new GridBagLayout();
        labelGBL.columnWidths = new int[]{0, targetWidth, typeWidth,0};
        labelGBL.columnWeights = new double[]{1.0, 0.0, 0.0,0.0};
        labelPanel.setLayout(labelGBL);

        beforeLabel = new JLabel(getName() + (getRelationshipMetadata().getTarget() == null ? "" : "("));
        beforeLabel.setToolTipText(getTag());
        beforeLabel.setFont(SEDAObjectEditor.LABEL_FONT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        labelPanel.add(beforeLabel, gbc);

        targetButton = new JButton("+target");
        targetButton.setMargin(new Insets(0, 0, 0, 0));
        targetButton.setFont(SEDAObjectEditor.MINI_EDIT_FONT);
        targetButton.setFocusable(false);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        targetButton.addActionListener(arg -> this.targetActivate());
        labelPanel.add(targetButton, gbc);

        attributeTargetField = new JTextField();
        attributeTargetField.setText(getRelationshipMetadata().getTarget());
        attributeTargetField.setFont(SEDAObjectEditor.MINI_EDIT_FONT);
        attributeTargetField.setColumns(2);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        labelPanel.add(attributeTargetField, gbc);
        if (getRelationshipMetadata().getTarget() == null) {
            targetButton.setVisible(true);
            attributeTargetField.setVisible(false);
        } else {
            targetButton.setVisible(false);
            attributeTargetField.setVisible(true);
        }

        typeButton = new JButton("+type");
        typeButton.setMargin(new Insets(0, 0, 0, 0));
        typeButton.setFont(SEDAObjectEditor.MINI_EDIT_FONT);
        typeButton.setFocusable(false);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        typeButton.addActionListener(arg -> this.typeActivate());
        labelPanel.add(typeButton, gbc);

        attributeTypeField = new JTextField();
        attributeTypeField.setText(getRelationshipMetadata().getTarget());
        attributeTypeField.setFont(SEDAObjectEditor.MINI_EDIT_FONT);
        attributeTypeField.setColumns(2);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        labelPanel.add(attributeTypeField, gbc);
        if (getRelationshipMetadata().getTarget() == null) {
            typeButton.setVisible(true);
            attributeTypeField.setVisible(false);
        } else {
            typeButton.setVisible(false);
            attributeTypeField.setVisible(true);
        }

        innerLabel = new JLabel((getRelationshipMetadata().getTarget() == null ? ":" : ") :"));
        innerLabel.setToolTipText(translateTag("Lang attribute"));
        innerLabel.setFont(SEDAObjectEditor.LABEL_FONT);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 3;
        gbc.gridy = 0;
        labelPanel.add(innerLabel, gbc);

        JPanel editPanel = new JPanel();
        gbl = new GridBagLayout();
        gbl.columnWeights = new double[]{1.0, 0.0};
        gbl.rowWeights = new double[]{1.0};
        editPanel.setLayout(gbl);

        this.sedaObjectEditorPanel = new SEDAObjectEditorSimplePanel(this, labelPanel, editPanel);
    }

    private void targetActivate() {
        targetButton.setVisible(false);
        attributeTargetField.setVisible(true);
        beforeLabel.setText(getName() + " (");
        innerLabel.setText(") :");
        attributeTargetField.grabFocus();
    }

    private void typeActivate() {
        typeButton.setVisible(false);
        attributeTypeField.setVisible(true);
        beforeLabel.setText(getName() + " (");
        innerLabel.setText(") :");
        attributeTypeField.grabFocus();
    }
}
