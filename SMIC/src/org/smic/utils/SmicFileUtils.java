package org.smic.utils;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * SmicFileUtils is a class which groups static methods pertaining file operations used throughout the SMIC module.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmicFileUtils {

    /**
     * Write the contents of a byte array to the specified file
     * 
     * @param content File contents that need to be written
     * @param filename Path and name of the file
     * @throws IOException
     */
    public static void writeFile(byte[] content, String filename) throws IOException {

        // Write contents to file
        IOUtils.write(content, new FileOutputStream(new File(filename)));
    }

    /**
     * Write the contents of an XML document object to the specified file
     * 
     * @param content XML document object
     * @param filename Path and name of the file
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void writeFile(Document content, String filename) throws TransformerConfigurationException,
            TransformerException {

        // Document object for writing to file
        Source source = new DOMSource(content);

        // Output file and stream
        File outputXmlFile = new File(filename);
        Result result = new StreamResult(outputXmlFile);

        // Write the DOM document to the file
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);
    }

    /**
     * Delete the list of files given using their paths and names
     * 
     * @param filenames List of files that have to be deleted
     */
    public static void deleteFiles(String[] filenames) {

        // Scan the lisf of files given and delete them if possible
        for (int i = 0; i < filenames.length; i++) {

            // Current file
            File currentFile = new File(filenames[i]);

            // Check if the file exist
            if (currentFile.exists()) {

                // Delete the file since it exists
                currentFile.delete();
            }
        }
    }

    /**
     * Delete a file specified by its path and name
     * 
     * @param filename Path and name of the file that needs to be deleted
     */
    public static void deleteFile(String filename) {

        // File name container
        String[] filenames = new String[1];
        filenames[0] = filename;

        // Delete the file
        deleteFiles(filenames);
    }
    
    /**
     * Read a missive XML file identified by the given filename and encode content to UTF-8
     * 
     * @param xmlFilename Missive XML file name
     * @return UTF-8 encoded string
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String encodedMissiveXmlContent(String xmlFilename) throws FileNotFoundException, IOException {

        // Missive XML file
        File xmlFile = new File(xmlFilename);

        // Missive XML file content array
        byte[] content;

        // Try to read the content of the missive XML file
        try (FileInputStream fis = new FileInputStream(xmlFile)) {

            // Content array size
            content = new byte[(int)xmlFile.length()];

            // Read content
            fis.read(content);
        }

        return new String(content, "UTF-8");
    }

    /**
     * Get the name of the file from a full path and filename string
     * 
     * @param fullPathAndFilename Full path and filename string
     * @return Name of file
     */
    public static String getFilename(String fullPathAndFilename) {

        // Get the parts of the full path and filename string
        String[] parts = fullPathAndFilename.split(System.getProperty("file.separator"));

        // Check if parts where found
        if (parts.length > 0) {
            return parts[parts.length - 1];
        } else {
            return "";
        }
    }
}
