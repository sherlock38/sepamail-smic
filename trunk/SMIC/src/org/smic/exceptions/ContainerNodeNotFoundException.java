package org.smic.exceptions;

/**
 * The ContainerNodeNotFoundException is the exception thrown when the container of the Document object could not be
 * found in the missive XML document using the given XPath expression.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class ContainerNodeNotFoundException extends Exception {

    /**
     * ContainerNodeNotFoundException constructor
     * 
     * @param xPath XPath expression used to obtain the Document object container
     */
    public ContainerNodeNotFoundException(String xPath) {

        // Initialise parent class
        super("The Document object container could not be found with the " + xPath + " XPath expression.");
    }
}
