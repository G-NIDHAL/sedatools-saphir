package fr.gouv.vitam.tools.mailextractlib.store.javamail.handlers;

import jakarta.activation.ActivationDataFlavor;
import jakarta.mail.internet.MimeBodyPart;

public class pkcs7_signature
    extends SignatureContentHandler
{
    private static final ActivationDataFlavor ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/pkcs7-signature", "Signature");
    private static final ActivationDataFlavor[]         DFS = new ActivationDataFlavor[] { ADF };
    
    public pkcs7_signature()
    {
        super(ADF, DFS);
    }
}
