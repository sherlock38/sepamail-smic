package org.smic.exceptions;

import java.io.FileNotFoundException;

/**
 * The ConfigurationFileNotFoundException class is the exception raised when the configuration required for the SMIC
 * module could not be found.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class ConfigurationFileNotFoundException extends FileNotFoundException {

    /**
     * ConfigurationFileNotFoundException default constructor
     */
    public ConfigurationFileNotFoundException() {

        // Initialise the parent class
        super("The SMOC configuration file could not be found.");
    }

    /**
     * ConfigurationFileNotFoundException constructor
     * 
     * @param filename SMOC configuration filename
     */
    public ConfigurationFileNotFoundException(String filename) {

        // Initialise the parent class
        super("The SMOC configuration file " + filename + " could not be found.");
    }
}
