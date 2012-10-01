package org.smic.exceptions;

/**
 * The DocumentObjectNotFoundException is the exception thrown when the Document object containing the encoded PDF file
 * could not be found in the missive XML document.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class DocumentObjectNotFoundException extends Exception {

    /**
     * DocumentObjectNotFoundException default constructor
     */
    public DocumentObjectNotFoundException() {

        // Initialise parent class
        super("The specified XML source file does not appear to contain the appropriate Document object.");
    }

    /**
     * DocumentObjectNotFoundException constructor
     * 
     * @param source Missive XML document file path and name
     */
    public DocumentObjectNotFoundException(String source) {

        // Initialise parent class
        super("The specified XML source at " + source + " does not appear to contain the appropriate Document object.");
    }
}
