package org.smic.exceptions;

/**
 * The InvalidDocumentObjectTemplateException class is the exception raised when the document object XML template is not
 * valid.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidDocumentObjectTemplateException extends Exception {

    /**
     * InvalidDocumentObjectTemplateException default constructor
     */
    public InvalidDocumentObjectTemplateException() {

        // Initialise the parent class
        super("The Document object XML template does not appear to be valid.");
    }
    
    /**
     * InvalidDocumentObjectTemplateException constructor
     * 
     * @param placeholdersValues List of placeholder keys and values that could not be mapped onto the template
     */
    public InvalidDocumentObjectTemplateException(String placeholdersValues) {

        // Initialise the parent class
        super("The Document object XML template does not appear to be valid. The keys (and corresponding values): " +
                placeholdersValues + " could not be mapped onto the given Document object XML template.");
    }
}
