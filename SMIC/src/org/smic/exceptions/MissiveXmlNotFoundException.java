package org.smic.exceptions;

/**
 * The MissiveXmlNotFoundException is the exception thrown when the PDF document that has to be converted does not
 * contain the required missive XML tags
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class MissiveXmlNotFoundException extends Exception {

    /**
     * MissiveXmlNotFoundException default constructor
     */
    public MissiveXmlNotFoundException() {

        // Initialise parent class
        super("The specified PDF source file does not appear to contain the appropriate missive XML.");
    }

    /**
     * MissiveXmlNotFoundException constructor
     * 
     * @param source Source PDF document file path and name
     */
    public MissiveXmlNotFoundException(String source) {

        // Initialise parent class
        super("The specified PDF source at " + source + " does not appear to contain the appropriate missive XML.");
    }
}
