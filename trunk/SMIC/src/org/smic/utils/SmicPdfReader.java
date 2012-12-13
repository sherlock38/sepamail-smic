package org.smic.utils;

import com.itextpdf.text.pdf.PdfReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The SmicPdfReader class is used to read and extract XMP tags from a PDF file as an XML document.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmicPdfReader {

    private String source;
    private Document xmlDocument;
    private HashMap<String, String> documentInfo;

    /**
     * Get the document object which represents the given PDF file metadata
     * 
     * @return Document object which represents the given PDF file metadata
     */
    public Document getXmlMetadataDocument() {
        return this.xmlDocument;
    }

    /**
     * Get the document info map of the PDF document
     * 
     * @return Document info map of the PDF document
     */
    public HashMap<String, String> getDocumentInfo() {
        return this.documentInfo;
    }

    /**
     * SmicPdfReader class constructor
     * 
     * @param source Path and name of the PDF file that needs to be converted
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public SmicPdfReader(String source) throws FileNotFoundException, IOException, ParserConfigurationException,
            SAXException {

        // Initialise class attributes
        this.source = source;
        this.xmlDocument = null;

        // Read and parse the source PDF document
        this.parse();
    }

    /**
     * Read the PDF file and extract the metadata of the PDF file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private void parse() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {

        // XML source file instance
        File sourceFile = new File(this.source);

        // Check if the source file exists
        if (sourceFile.exists()) {

            // Source file exists so we read the PDF file
            PdfReader pdfReader = new PdfReader(this.source);

            // Get document info
            this.documentInfo = pdfReader.getInfo();

            // Get the metadata of the PDF file
            byte[] metadata = pdfReader.getMetadata();

            // Input stream from the PDF metadata
            ByteArrayInputStream bais = new ByteArrayInputStream(metadata);

            // XML document parser instance
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Parse the metadata input stream to XML document
            this.xmlDocument = db.parse(bais);

        } else {

            // File was not found
            throw new FileNotFoundException("The specified source file, " + this.source + ", was not found.");
        }
    }
}
