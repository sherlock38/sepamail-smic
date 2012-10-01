package org.smic.exceptions;

/**
 * The InvalidPdfPageNumberException class is the exception thrown when the specified page could not be found in the PDF
 * document.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidPdfPageNumberException extends Exception {

    /**
     * InvalidPdfPageNumberException constructor
     * 
     * @param page Page number
     */
    public InvalidPdfPageNumberException(int page) {

        // Initialise parent class
        super("Page " + page + " could not be found in the PDF document.");
    }

    /**
     * InvalidPdfPageNumberException constructor
     * 
     * @param page Page number
     * @param pdfFilename Path and name of the PDF file
     */
    public InvalidPdfPageNumberException(int page, String pdfFilename) {

        // Initialise parent class
        super("Page " + page + " could not be found in the PDF document " + pdfFilename + ".");
    }
}
