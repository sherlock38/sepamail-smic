package org.smic.transformation;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import org.smic.exceptions.*;
import org.smic.utils.NamespaceContextMap;
import org.smic.utils.SmicFileUtils;
import org.smic.utils.SmicPdfReader;
import org.smic.utils.SmicPdfUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The SmicPdf class tries to find the required XMP tag within the given PDF document and tries to convert it to a
 * missive XML file.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class SmicPdf {

    private HashMap<String, String> documentInfo;
    private String documentLocale;
    private String documentMime;
    private String documentTemplate;
    private String documentType;
    private String missiveTag;
    private String missiveXmlEncodedValue;
    private String outputFolder;
    private String pdfFilename;
    private String tempFolder;
    private String xmlFilename;
    private Document xmlMetadata;

    /**
     * SmicPdf class constructor
     * 
     * @param pdfFilename Path and name of the PDF document
     * @param outputFolder Path of XML output folder
     * @param tempFolder Path of temporary files folder
     * @param missiveTag Name of the missive XML tag in the XMP metadata of the PDF file
     * @param documentType Type of document being added to the missive XML file
     * @param documentMime MIME type of the document being added to the missive XML file
     * @param documentLocale Locale of the document being added to the missive XML file
     * @param documentTemplate The Document object template that will be added to the missive XML file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public SmicPdf(String pdfFilename, String outputFolder, String tempFolder, String missiveTag, String documentType,
            String documentMime, String documentLocale, String documentTemplate) throws FileNotFoundException,
            IOException, ParserConfigurationException, SAXException {

        // Initialise class attributes
        this.documentLocale = documentLocale;
        this.documentMime = documentMime;
        this.documentTemplate = documentTemplate;
        this.documentType = documentType;
        this.missiveTag = missiveTag;
        this.missiveXmlEncodedValue = "";
        this.outputFolder = outputFolder;
        this.pdfFilename = pdfFilename;
        this.tempFolder = tempFolder;
        this.xmlFilename = "";

        // Read the given PDF file document
        SmicPdfReader smicPdfReader = new SmicPdfReader(pdfFilename);

        // PDF document info
        this.documentInfo = smicPdfReader.getDocumentInfo();

        // Get the PDF file metadata
        this.xmlMetadata = smicPdfReader.getXmlMetadataDocument();
    }

    /**
     * Convert the PDF document to the required missive XML document containing the encoded PDF file at the specified
     * XPath
     * 
     * @param namespace Comma-delimited list of name spaces used within the missive XML document
     * @param xPath Path at which the encoded PDF document will be added in the missive XML document
     * @return Path and name of the missive XML file
     * @throws MissiveXmlNotFoundException
     * @throws UnsupportedEncodingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws InvalidDocumentObjectTemplateException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XmlDocumentNotGeneratedException
     * @throws XPathExpressionException
     * @throws ContainerNodeNotFoundException
     * @throws InvalidNamespaceDefinitionException
     */
    public String convert(String namespace, String xPath) throws MissiveXmlNotFoundException,
            UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException,
            InvalidDocumentObjectTemplateException, TransformerConfigurationException, TransformerException,
            XmlDocumentNotGeneratedException, XPathExpressionException, ContainerNodeNotFoundException,
            InvalidNamespaceDefinitionException {

        // Check if the PDF file contains the required XMP tag
        if (this.hasMissiveXML()) {

            // Create the preliminary missive XML document from the XMP tag
            String preliminaryMissiveXmlFilename = this.createPreliminaryMissiveXmlDocument();

            // Base64 encoded PDF file
            String encodedPdfFileContent = SmicPdfUtils.encodePdf(this.pdfFilename);

            // Generate the document object node
            Document documentObject = this.generateDocumentObjectNode(encodedPdfFileContent);

            // Append the Document object node to the preliminary missive XML document
            Document finalMissiveXmlDocument = this.appendDocumentObject(preliminaryMissiveXmlFilename, documentObject,
                    namespace, xPath);

            // Name of the final missive XML document
            String pdfFilenameOnly = SmicFileUtils.getFilename(this.pdfFilename);
            String finalMissiveXmlFilename = pdfFilenameOnly.substring(0, pdfFilenameOnly.length() - 4) + ".xml";

            // Save the final missive XML file
            SmicFileUtils.writeFile(finalMissiveXmlDocument, this.outputFolder + System.getProperty("file.separator") +
                    finalMissiveXmlFilename);

            // Delete the preliminary missive XML file
            SmicFileUtils.deleteFile(preliminaryMissiveXmlFilename);

            // Set the value for the final missive XML filename
            this.xmlFilename = finalMissiveXmlFilename;

        } else {

            // Throw exception since the missive XML tag with the appropriate value was not found
            throw new MissiveXmlNotFoundException(this.pdfFilename);
        }

        return this.getOutputXmlFilename();
    }

    /**
     * Check if the metadata of the PDF file contains the required missive XML tag and content
     * 
     * @return Whether the metadata of the PDF file contains the required missive XML tag and content
     */
    private boolean hasMissiveXML() {

        // Get the missive XML node from the metadata XML document
        NodeList nl = this.xmlMetadata.getElementsByTagName(this.missiveTag);

        // Check if the metadata XML document object contains the required missive XML document tag
        if (nl.getLength() > 0) {

            // Value node
            Node valueNode = nl.item(0).getFirstChild();

            // Check if the value node was found
            if (valueNode != null) {

                // Check if the value node contains data
                if (valueNode.getNodeValue().length() > 0) {

                    // Missive XML data
                    this.missiveXmlEncodedValue = valueNode.getNodeValue();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Create the preliminary missive XML document from the missive data obtained from the given PDF file metadata
     * 
     * @return Preliminary missive XML document path and filename
     * @throws UnsupportedEncodingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private String createPreliminaryMissiveXmlDocument() throws UnsupportedEncodingException,
            ParserConfigurationException, SAXException, IOException, TransformerConfigurationException,
            TransformerException {

        // Decode the missive XML data obtained from the PDF file metadata
        String decodedMissiveXmlData = new String(this.missiveXmlEncodedValue.getBytes(), "UTF-8");

        // Input stream from the PDF metadata
        ByteArrayInputStream bais = new ByteArrayInputStream(decodedMissiveXmlData.getBytes());

        // XML document parser instance
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Name of preliminary missive XML file
        String pdfFilenameOnly = SmicFileUtils.getFilename(this.pdfFilename);
        String preliminaryXmlFilename = pdfFilenameOnly.substring(0, pdfFilenameOnly.length() - 4) + "_temp.xml";

        // Save the preliminary missive XML file to temporary folder
        SmicFileUtils.writeFile(db.parse(bais), this.tempFolder + System.getProperty("file.separator") +
                preliminaryXmlFilename);

        // Path and name of preliminary missive XML file
        return this.tempFolder + System.getProperty("file.separator") + preliminaryXmlFilename;
    }

    /**
     * Generate the document object node using the document node template and encoded PDF file content string
     * 
     * @param encodedPdfFileContent
     * @return Document object node that needs to be appended to the missive XML file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws InvalidDocumentObjectTemplateException
     */
    private Document generateDocumentObjectNode(String encodedPdfFileContent) throws ParserConfigurationException,
            SAXException, IOException, InvalidDocumentObjectTemplateException {

        // Create a map of tokens and corresponding values
        HashMap<String, String> tokens = new HashMap<>();
        HashMap<String, String> placedTokens = new HashMap<>();

        // Define token place holders and corresponding values
        tokens.put("#SMIC#DocumentType#", this.documentType);
        tokens.put("#SMIC#DocumentDate#", this.getIsoDate(this.documentInfo.get("CreationDate")));
        tokens.put("#SMIC#DocumentTitle#", this.documentInfo.get("Title"));
        tokens.put("#SMIC#DocumentLanguage#", this.documentLocale);
        tokens.put("#SMIC#DocumentApplicationType#", this.documentMime);
        tokens.put("#SMIC#DocumentFilename#", SmicFileUtils.getFilename(this.pdfFilename));
        tokens.put("#SMIC#DocumentData#", encodedPdfFileContent);

        // Tokens map iterator
        Iterator mapIt = tokens.entrySet().iterator();

        // Replace the tokens with their corresponding values in the document object template
        while (mapIt.hasNext()) {

            // Current map entry
            Map.Entry<String, String> entry = (Map.Entry<String, String>)mapIt.next();

            // Get the beginning position of the placeholder
            int placeHolderIndex = this.documentTemplate.indexOf(entry.getKey());

            // Check if the placeholder was found in the document template
            if (placeHolderIndex > -1) {

                // Check if the current placeholder could be found in the document template
                if (this.documentTemplate.substring(placeHolderIndex, entry.getKey().length() +
                        placeHolderIndex).equals(entry.getKey())) {

                    // Replace the placeholders in the template string
                    this.documentTemplate = this.documentTemplate.replaceAll(entry.getKey(), entry.getValue());

                    // Update the list of mapped placeholders
                    if (!placedTokens.containsKey(entry.getKey())) {
                        placedTokens.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        // List of unmapped tokens
        HashMap<String, String> unmappedTokens = this.getUnmappedTokens(tokens, placedTokens);

        // Check if all placeholders were mapped onto the Document object XML template
        if (unmappedTokens.size() > 0) {

            // All the tokens were not successfully mapped, so we throw an exception to indicate that the Document
            // object XML template is not valid
            throw new InvalidDocumentObjectTemplateException(unmappedTokens.toString());
        }

        // Input stream from the document template
        ByteArrayInputStream bais = new ByteArrayInputStream(this.documentTemplate.getBytes());

        // XML document parser instance
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Create the document object node from the template string
        return db.parse(bais);
    }

    /**
     * Get the ISO date format from the creation date string specified in a PDF metadata
     * 
     * @param pdfCreationDate Creation date string specified in a PDF metadata
     * @return ISO date format
     */
    private String getIsoDate(String pdfCreationDate) {

        return pdfCreationDate.substring(2, 6) + "-" + pdfCreationDate.substring(6, 8) + "-" + 
                pdfCreationDate.substring(8, 10);
    }

    /**
     * Get the list of tokens that were not mapped onto the Document object XML template
     * 
     * @param tokens List of tokens that had to be mapped onto the the Document object XML template
     * @param placedTokens List of tokens that have successfully been mapped onto the Document object XML template
     * @return List of tokens and their corresponding values that were not mapped
     */
    private HashMap<String, String> getUnmappedTokens(HashMap<String, String> tokens,
            HashMap<String, String> placedTokens) {

        // List of tokens that were not mapped
        HashMap<String, String> unmappedTokens = new HashMap<>();

        // Tokens map iterator
        Iterator mapIt = tokens.entrySet().iterator();

        // Scan the list of tokens that had to be mapped
        while (mapIt.hasNext()) {

            // Current map entry
            Map.Entry<String, String> entry = (Map.Entry<String, String>)mapIt.next();

            // Check if the token was not successfully mapped
            if (!placedTokens.containsKey(entry.getKey())) {

                // Add the current token to the list of unmapped tokens
                unmappedTokens.put(entry.getKey(), entry.getValue());
            }
        }

        return unmappedTokens;
    }

    /**
     * Append the document node to the missive XML document at the end of the container defined by the XPath
     * 
     * @param preliminaryMissiveXmlFilename Preliminary missive XML document path and filename
     * @param documentObject Document object
     * @param namespace Comma-delimited list of name spaces used within the missive XML document
     * @param xPathExpression XPath expression string which defines the container of the Document object node
     * @return Final missive XML document object
     * @throws XPathExpressionException
     * @throws ContainerNodeNotFoundException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws InvalidNamespaceDefinitionException
     */
    private Document appendDocumentObject(String preliminaryMissiveXmlFilename, Document documentObject,
            String namespace, String xPathExpression) throws XPathExpressionException, ContainerNodeNotFoundException,
            ParserConfigurationException, SAXException, IOException, InvalidNamespaceDefinitionException {

        // Load the preliminary missive XML file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Preliminary missive XML file document
        Document preliminaryMissiveXml = db.parse(preliminaryMissiveXmlFilename);

        try {

            // XPath factory for Document object container
            XPath xPath = XPathFactory.newInstance().newXPath();

            // Missive XML document name space context
            NamespaceContext nsContext = this.getNamespaceContext(namespace);

            // Set the name space context of the XPath
            xPath.setNamespaceContext(nsContext);

            // XPath expression for Document object container
            XPathExpression expression = xPath.compile(xPathExpression);

            // XPath evaluation result
            NodeList containerNodeList = (NodeList) expression.evaluate(preliminaryMissiveXml, XPathConstants.NODESET);

            // Check if the container node was obtained with the given XPath expression
            if (containerNodeList.getLength() > 0) {

                // Append the Document object node to the container (we consider the last occurence of the container)
                Node containerNode = containerNodeList.item(containerNodeList.getLength() - 1);

                // Document object node
                Node documentObjectNode = preliminaryMissiveXml.importNode(documentObject.getFirstChild(), true);

                // Append the document object node to the preliminary document
                containerNode.appendChild(documentObjectNode);

                // Normalise the missive document object
                preliminaryMissiveXml.normalizeDocument();

            } else {

                // Throw exception since Document object container could not be found
                throw new ContainerNodeNotFoundException(xPathExpression);
            }

        } catch (XPathExpressionException ex) {

            // Invalid XPath expression
            throw new XPathExpressionException(xPathExpression + " is not a valid XPath expression.");
        }
        
        return preliminaryMissiveXml;
    }

    /**
     * Get the path and name of the resulting XML file
     * 
     * @return Path and name of the resulting XML file
     * @throws XmlDocumentNotGeneratedException
     */
    private String getOutputXmlFilename() throws XmlDocumentNotGeneratedException {

        // Check if the XML document has been generated
        if (this.xmlFilename.length() == 0) {
            throw new XmlDocumentNotGeneratedException();
        }

        return this.outputFolder + System.getProperty("file.separator") + this.xmlFilename;
    }

    /**
     * Get the name space context map defined by the given name space context string
     * 
     * @param namespace Comma-delimited list of name spaces used within the missive XML document
     * @return Name space context map
     * @throws InvalidNamespaceDefinitionException
     */
    private NamespaceContext getNamespaceContext(String namespace) throws InvalidNamespaceDefinitionException {

        // Name space mappings
        HashMap<String, String> mappings = new HashMap<>();

        // Namespace parts
        String[] namespaceParts = namespace.split(",");

        // Scan the list of namespace parts and populate the mappings
        for (int i = 0; i < namespaceParts.length; i++) {

            // Split the name space mapping
            String[] currentMappingParts = namespaceParts[i].split("=", 2);

            // Check the length of current mapping parts
            if (currentMappingParts.length == 2) {
                mappings.put(currentMappingParts[0], currentMappingParts[1]);
            } else {

                // Throw exception since mapping does not appear to be valid
                throw new InvalidNamespaceDefinitionException(namespaceParts[i]);
            }
        }

        return new NamespaceContextMap(mappings);
    }
}
