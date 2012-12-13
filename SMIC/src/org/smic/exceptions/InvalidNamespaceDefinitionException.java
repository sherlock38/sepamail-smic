package org.smic.exceptions;

/**
 * The InvalidNamespaceDefinitionException is the exception thrown when the specified missive XML document name space
 * does not appear to be valid
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidNamespaceDefinitionException extends Exception {

    /**
     * InvalidNamespaceDefinitionException constructor
     * 
     * @param namespace Name space mapping that does not appear to be valid
     */
    public InvalidNamespaceDefinitionException(String namespace) {

        // Initialise parent class
        super("The name space mapping + " + namespace + " does not appear to be valid.");
    }
}
