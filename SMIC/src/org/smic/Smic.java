package org.smic;

import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.smic.exceptions.*;
import org.smic.transformation.SmicPdf;
import org.smic.transformation.SmicXml;
import org.smic.utils.ConfigReader;
import org.smic.utils.SmicDirectoryUtils;
import org.xml.sax.SAXException;

/**
 * The Smic class provides methods which allows to convert a PDF document containing an encoded missive XML document to 
 * an XML document containing the Base64 encoded PDF file and vice-versa.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class Smic {

    private boolean hasConfiguration;
    private boolean hasValidConfiguration;
    private String outputFolder;
    private HashMap<String, String> smicConfig;
    private String tempFolder;

    /**
     * Smic class default constructor
     * 
     * @param configFilename SMIC module configuration file path and name
     * @param outputFolder Folder where resulting files will be written
     * @param tempFolder Folder where temporary files will be written when required
     * @throws ConfigurationFileNotFoundException
     * @throws InvalidConfigurationException
     * @throws IOException
     */
    public Smic(String configFilename, String outputFolder, String tempFolder) throws
            ConfigurationFileNotFoundException, IOException, InvalidConfigurationException {

        // Initialise class attributes
        this.hasConfiguration = false;
        this.hasValidConfiguration = false;
        this.outputFolder = outputFolder;
        this.smicConfig = new HashMap<>();
        this.tempFolder = tempFolder;

        try {

            // Configuration file reader instance
            ConfigReader configReader = new ConfigReader(configFilename);

            // Configuration has been found
            this.hasConfiguration = true;

            // Parse the configuration file
            this.smicConfig = configReader.parse();

            // Configuration file is valid
            this.hasValidConfiguration = true;

            // Create output and temporary folders if they do not exist
            SmicDirectoryUtils.createFolderIfNotExist(this.outputFolder);
            SmicDirectoryUtils.createFolderIfNotExist(this.tempFolder);

        } catch (ConfigurationFileNotFoundException ex) {

            // Configuration file was not found
            this.hasConfiguration = false;

            // Throw the raised exception
            throw ex;

        } catch (InvalidConfigurationException ex) {

            // Configuration file is not valid
            this.hasValidConfiguration = false;

            // Throw the raised exception
            throw ex;
        }
    }

    /**
     * Convert a PDF document to a missive XML document containing the PDF document encoded in Base64
     * 
     * @param pdfFilename Path and name of PDF file that need to be converted to XML missive document
     * @return Path and name of resulting XML document
     * @throws ConfigurationFileNotFoundException
     * @throws InvalidConfigurationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws MissiveXmlNotFoundException
     * @throws UnsupportedEncodingException
     * @throws InvalidDocumentObjectTemplateException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XmlDocumentNotGeneratedException
     * @throws XPathExpressionException
     * @throws ContainerNodeNotFoundException
     * @throws InvalidNamespaceDefinitionException
     */
    public String smicPdf2Xml(String pdfFilename) throws ConfigurationFileNotFoundException,
            InvalidConfigurationException, FileNotFoundException, IOException, ParserConfigurationException,
            SAXException, MissiveXmlNotFoundException, UnsupportedEncodingException,
            InvalidDocumentObjectTemplateException, TransformerConfigurationException, TransformerException,
            XmlDocumentNotGeneratedException, XPathExpressionException, ContainerNodeNotFoundException,
            InvalidNamespaceDefinitionException {

        // Check if we have a valid configuration
        if (this.validateConfiguration()) {

            // XML to PDF conversion class instance
            SmicPdf smicPdf = new SmicPdf(pdfFilename, this.outputFolder, this.tempFolder,
                    smicConfig.get("xmp.missive"), smicConfig.get("document.type"), smicConfig.get("document.mime"),
                    smicConfig.get("document.locale"), smicConfig.get("document.template"));

            // Convert the PDF document to missive XML and get the path and name of the resulting XML file
            return smicPdf.convert(smicConfig.get("document.namespace"), smicConfig.get("document.xpath"));
        }
        
        return null;
    }

    /**
     * Convert a PDF document to a missive XML document containing the PDF document encoded in Base64
     * 
     * @param pdfFilename Path and name of PDF file that need to be converted to XML missive document
     * @param documentType Type of document being added to the missive XML file
     * @return Path and name of resulting XML document
     * @throws ConfigurationFileNotFoundException
     * @throws InvalidConfigurationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws MissiveXmlNotFoundException
     * @throws UnsupportedEncodingException
     * @throws InvalidDocumentObjectTemplateException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XmlDocumentNotGeneratedException
     * @throws XPathExpressionException
     * @throws ContainerNodeNotFoundException
     * @throws InvalidNamespaceDefinitionException
     */
    public String smicPdf2Xml(String pdfFilename, String documentType) throws ConfigurationFileNotFoundException,
            InvalidConfigurationException, FileNotFoundException, IOException, ParserConfigurationException,
            SAXException, MissiveXmlNotFoundException, UnsupportedEncodingException,
            InvalidDocumentObjectTemplateException, TransformerConfigurationException, TransformerException,
            XmlDocumentNotGeneratedException, XPathExpressionException, ContainerNodeNotFoundException,
            InvalidNamespaceDefinitionException {

        // Check if we have a valid configuration
        if (this.validateConfiguration()) {

            // XML to PDF conversion class instance
            SmicPdf smicPdf = new SmicPdf(pdfFilename, this.outputFolder, this.tempFolder,
                    smicConfig.get("xmp.missive"), documentType, smicConfig.get("document.mime"), 
                    smicConfig.get("document.locale"), smicConfig.get("document.template"));

            // Convert the PDF document to missive XML and get the path and name of the resulting XML file
            return smicPdf.convert(smicConfig.get("document.namespace"), smicConfig.get("document.xpath"));
        }
        
        return null;
    }

    /**
     * Convert a missive XML document to a PDF containing the missive XML document without the document object
     * 
     * @param xmlFilename Path and name of XML file that need to be converted to PDF document
     * @return Path and name of the resulting PDF document
     * @throws ConfigurationFileNotFoundException
     * @throws InvalidConfigurationException
     * @throws FileNotFoundException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws DocumentObjectNotFoundException
     * @throws PdfDocumentNotGeneratedException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws InvalidPdfPageNumberException
     * @throws DocumentException
     */
    public String smicXml2Pdf(String xmlFilename) throws ConfigurationFileNotFoundException,
            InvalidConfigurationException, FileNotFoundException, ParserConfigurationException, IOException,
            SAXException, DocumentObjectNotFoundException, PdfDocumentNotGeneratedException, 
            TransformerConfigurationException, TransformerException, InvalidPdfPageNumberException, DocumentException {

        // Check if we have a valid configuration
        if (this.validateConfiguration()) {

            // XML to PDF conversion class instance
            SmicXml smicXml = new SmicXml(xmlFilename, this.outputFolder, this.tempFolder);

            // Convert the missive XML document to PDF and get the path and name of the resulting PDF file
            return smicXml.convert(smicConfig.get("pdf.generator"));
        }

        return null;
    }
    
    /**
     * Check if the SMIC configuration file was successfully read and parsed
     * 
     * @return Whether the SMIC configuration file was successfully read and parsed
     * @throws ConfigurationFileNotFoundException
     * @throws InvalidConfigurationException
     */
    private boolean validateConfiguration() throws ConfigurationFileNotFoundException, InvalidConfigurationException {

        // Check if configuration file was not found
        if (!this.hasConfiguration) {
            throw new ConfigurationFileNotFoundException();
        }

        // Check if configuration file is not valid
        if (!this.hasValidConfiguration) {
            throw new InvalidConfigurationException();
        }

        // Check if we have the configuration file and that it is valid
        if (this.hasConfiguration && this.hasValidConfiguration) {
            return true;
        }

        return false;
    }
}
