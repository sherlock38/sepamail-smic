package org.smic.exceptions;

/**
 * The XmlDocumentNotGeneratedException is the exception thrown when the PDF document has not been converted to the
 * required missive XML document.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class XmlDocumentNotGeneratedException extends Exception {

    /**
     * XmlDocumentNotGeneratedException default constructor
     */
    public XmlDocumentNotGeneratedException() {

        // Initialise parent class
        super("The PDF document has not yet been converted to the missive XML document.");
    }
}
