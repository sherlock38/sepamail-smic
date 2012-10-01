package org.smic.exceptions;

/**
 * The PdfDocumentNotGeneratedException is the exception thrown when the missive XML document has not been converted to
 * the required PDF document.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class PdfDocumentNotGeneratedException extends Exception {

    /**
     * PdfDocumentNotGeneratedException default constructor
     */
    public PdfDocumentNotGeneratedException() {

        // Initialise parent class
        super("The missive XML document has not yet been converted to the required PDF document.");
    }
}
