package org.smic.exceptions;

/**
 * The InvalidConfigurationException class is the exception raised when the configuration required for the SMIC module
 * is not valid.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidConfigurationException extends Exception {

    /**
     * InvalidConfigurationException default constructor
     */
    public InvalidConfigurationException() {

        // Initialise the parent class
        super("The configuration file does not appear to be valid.");
    }
    
    /**
     * InvalidConfigurationException constructor
     * 
     * @param property Name of the property that is missing in the configuration file
     */
    public InvalidConfigurationException(String property) {

        // Initialise the parent class
        super("The property " + property + " does not exist in the configuration file.");
    }
    
    /**
     * InvalidConfigurationException constructor
     * 
     * @param property Name of the property that is missing in the configuration file
     * @param value Value assigned to the specified property
     */
    public InvalidConfigurationException(String property, String value) {

        // Initialise the parent class
        super("The value " + value + " assigned to the property " + property + " is not valid.");
    }
}
