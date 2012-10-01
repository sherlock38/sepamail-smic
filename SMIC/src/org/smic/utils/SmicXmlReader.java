package org.smic.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The SmicXmlReader class is used to read a source XML file which contains the Base64 encoded PDF file.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmicXmlReader {

    private String source;
    private Document xmlDocument;

    /**
     * Get the document object which represents the given XML missive file
     * 
     * @return Document object which represents the given XML missive file
     */
    public Document getXmlDocument() {
        return this.xmlDocument;
    }

    /**
     * SmicXmlReader class constructor
     * 
     * @param source Path and name of the XML file that needs to be converted
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    public SmicXmlReader(String source) throws ParserConfigurationException, FileNotFoundException, SAXException,
            IOException {

        // Initialise class attributes
        this.source = source;
        this.xmlDocument = null;

        // Parse the source XML document
        this.parse();
    }
    
    /**
     * Parse the XML source file
     * 
     * @throws ParserConfigurationException 
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    private void parse() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {

        // Parse the source XML file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // XML source file instance
        File sourceFile = new File(this.source);

        // Check if the source file exists
        if (sourceFile.exists()) {

            // Source file exists so we parse the XML file
            this.xmlDocument = db.parse(sourceFile);

        } else {

            // File was not found
            throw new FileNotFoundException("The specified source file, " + this.source + ", was not found.");
        }
    }
}
