package org.smic.exceptions;

/**
 * The XmlTagNotFoundException is the exception thrown when the source XML document does not contain the Base64 encoded
 * PDF file content tag
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class XmlTagNotFoundException extends Exception {

    /**
     * MissiveXmlNotFoundException constructor
     * 
     * @param tag XML tag that contains the Base64 encoded PDF file contents
     */
    public XmlTagNotFoundException(String tag) {

        // Initialise parent class
        super("The specified XML source file does not contain the " + tag + " tag.");
    }
}
