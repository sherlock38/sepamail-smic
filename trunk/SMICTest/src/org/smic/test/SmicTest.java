package org.smic.test;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.smic.Smic;
import org.smic.exceptions.*;
import org.xml.sax.SAXException;

/**
 * The SmicTest class is a command line application that is used to test the functionalities of the SMIC module
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class SmicTest {

    /**
     * SMIC module test application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String [] args) {

        // SMIC module configuration file
        String conf = getCurrentWorkingDirectory() + System.getProperty("file.separator") + "conf" +
                System.getProperty("file.separator") + "smic.properties";

        // SMIC module output folder
        String output = getCurrentWorkingDirectory() + System.getProperty("file.separator") + "output";
        
        // SMIC module temporary folder
        String temp = getCurrentWorkingDirectory() + System.getProperty("file.separator") + "tmp";

        // Check parameter count
        if (args.length == 2) {

            // Check the conversion parameter value
            if (args[0].equals("pdf") || args[0].equals("xml")) {

                // XML to PDF
                if (args[0].equals("pdf")) {

                    try {

                        // Smic class instance
                        Smic smic = new Smic(conf, output, temp);

                        // Name of resulting PDF file
                        System.out.println(smic.smicXml2Pdf(args[1]));

                    } catch (ConfigurationFileNotFoundException | InvalidConfigurationException ex) {

                        System.out.println(ex.getMessage());

                    } catch (ParserConfigurationException | IOException | SAXException |
                            DocumentObjectNotFoundException | PdfDocumentNotGeneratedException | TransformerException |
                            InvalidPdfPageNumberException | DocumentException ex) {

                        System.out.println(ex.getMessage());

                    }

                } else {

                    // Convert PDF to XML
                    try {

                        // Smic class instance
                        Smic smic = new Smic(conf, output, temp);

                        // Name of resulting XML file
                        System.out.println(smic.smicPdf2Xml(args[1]));

                    } catch (ConfigurationFileNotFoundException | InvalidConfigurationException ex) {

                        System.out.println(ex.getMessage());

                    } catch (ParserConfigurationException | IOException | SAXException | TransformerException |
                            MissiveXmlNotFoundException | InvalidDocumentObjectTemplateException |
                            XmlDocumentNotGeneratedException | XPathExpressionException |
                            ContainerNodeNotFoundException | InvalidNamespaceDefinitionException ex) {

                        System.out.println(ex.getMessage());

                    }
                }

            } else {

                // Display application usage
                showUsage();
            }

        } else {

            // Display application usage
            showUsage();
        }

    }

    /**
     * Get the current working directory of the application
     *
     * @return The absolute path to the application working directory
     */
    private static String getCurrentWorkingDirectory() {

        String path;

        // Current class directory
        URL location = SmicTest.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            path = URLDecoder.decode(location.getPath(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            path = location.getPath();
        }

        // Absolute current working directory
        return new File(path).getParentFile().getPath() + System.getProperty("file.separator") + ".";

        // Absolute current working directory - added for debugging
        //return new File(path).getParentFile().getPath() + System.getProperty("file.separator") + "./..";
    }

    /**
     * Display application usage
     */
    private static void showUsage() {

        System.out.println("java -jar SMICTest.jar (Type de conversion: xml|pdf) (fichier à convertir)");
    }
}
