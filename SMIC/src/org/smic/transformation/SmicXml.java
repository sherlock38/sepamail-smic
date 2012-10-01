package org.smic.transformation;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.xml.xmp.DublinCoreSchema;
import com.itextpdf.text.xml.xmp.PdfA1Schema;
import com.itextpdf.text.xml.xmp.XmpBasicSchema;
import com.itextpdf.text.xml.xmp.XmpWriter;
import java.io.*;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.codec.binary.Base64;
import org.smic.exceptions.DocumentObjectNotFoundException;
import org.smic.exceptions.InvalidPdfPageNumberException;
import org.smic.exceptions.PdfDocumentNotGeneratedException;
import org.smic.utils.SmicFileUtils;
import org.smic.utils.SmicPdfUtils;
import org.smic.utils.SmicXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The SmicXml class tries to find the required Document object within a specified missive XML document and converts it
 * to PDF if possible.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmicXml {

    private String encodedPdf;
    private String encodedPdfFilename;
    private String outputFolder;
    private String pdfFilename;
    private String tempFolder;
    private Document xmlDocument;
    private String xmlFilename;

    /**
     * SmicXml class constructor
     * 
     * @param xmlFilename Path and name of the missive XML document
     * @param outputFolder Path of PDF output folder
     * @param tempFolder Path of temporary files folder
     * @throws FileNotFoundException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public SmicXml(String xmlFilename, String outputFolder, String tempFolder) throws FileNotFoundException,
            ParserConfigurationException, IOException, SAXException {

        // Initialise class attributes
        this.outputFolder = outputFolder;
        this.pdfFilename = "";
        this.tempFolder = tempFolder;
        this.xmlFilename = xmlFilename;

        // Read the missive XML document
        SmicXmlReader smicXmlReader = new SmicXmlReader(xmlFilename);

        // XML document representing the missive XML file
        this.xmlDocument = smicXmlReader.getXmlDocument();
    }

    /**
     * Convert the specified missive XML document to PDF
     * 
     * @param generatorName Name of the PDF document generator that will appear on the final output PDF
     * @return Path and name of the PDF document
     * @throws DocumentObjectNotFoundException
     * @throws PdfDocumentNotGeneratedException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws InvalidPdfPageNumberException
     * @throws DocumentException
     */
    public String convert(String generatorName) throws DocumentObjectNotFoundException,
            PdfDocumentNotGeneratedException, IOException, TransformerConfigurationException, TransformerException,
            InvalidPdfPageNumberException, DocumentException {

        // Get the document object node
        Node documentObjectNode = this.getDocumentObjectNode(this.xmlDocument.getFirstChild());

        // Check if the Document object node was found
        if (documentObjectNode != null) {

            // Generate the preliminary PDF file
            String preliminaryPdfFilename = this.getPreliminaryPdfFile();

            // Generate the missive XML document without the Document object node
            String missiveXMLWithoutDocumentObjectFilename = 
                    this.generateMissiveWithoutDocumentObject(documentObjectNode);

            // Create the final PDF document with required XMP tags using the decoded PDF document
            this.createFinalPdfDocument(generatorName, preliminaryPdfFilename, missiveXMLWithoutDocumentObjectFilename);

            // Delete preliminary files used to create the output PDF
            SmicFileUtils.deleteFile(this.tempFolder + System.getProperty("file.separator") + preliminaryPdfFilename);
            SmicFileUtils.deleteFile(this.tempFolder + System.getProperty("file.separator") +
                    missiveXMLWithoutDocumentObjectFilename);

        } else {

            // Throw exception since the Document object node was not found
            throw new DocumentObjectNotFoundException(this.xmlFilename);
        }

        return this.getOutputPdfFilename();
    }

    /**
     * Scan the missive XML object to detect the Document object containing the encoded PDF document
     * 
     * @return The node of the required Document object
     */
    private Node getDocumentObjectNode(Node node) {

        Node documentObjectNode = null;

        // Get the child nodes of the current node
        NodeList children = node.getChildNodes();

        // Check if the list of child node is not empty
        if (children != null) {

            // Scan the list of child nodes and check the name of the node
            for (int i = 0; i < children.getLength(); i++) {

                // Current child node
                Node childNode = children.item(i);

                // Check the name of the node
                if (childNode.getNodeName().toLowerCase().startsWith("document") || 
                        childNode.getNodeName().toLowerCase().endsWith("document")) {

                    // Contents node
                    Node contentsNode = this.getContentsNode(childNode);

                    // Check if the 'Contents' node was found
                    if (contentsNode != null) {

                        // Test the 'Contents' node for required nodes and values
                        if (this.testContentsNode(contentsNode)) {

                            // Set the Document object node
                            documentObjectNode = childNode;
                        }
                    }
                }
                
                // Check if the Document object node was found
                if (documentObjectNode != null) {
                    break;
                }

                // Traverse the child nodes of the current node
                documentObjectNode = this.getDocumentObjectNode(childNode);
            }
        }

        return documentObjectNode;
    }

    /**
     * Get the 'Contents' child node of the given node
     * 
     * @param node XML node that needs to be checked
     * @return The 'Contents' node
     */
    private Node getContentsNode(Node node) {

        // Get the child nodes of the current node
        NodeList children = node.getChildNodes();

        // Check if the Document object node contains children
        if (children != null) {

            // Scan the list of child nodes and check for the 'Contents' node
            for (int i = 0; i < children.getLength(); i++) {

                // Current child node
                Node childNode = children.item(i);

                // Check the name of the node
                if (childNode.getNodeName().toLowerCase().startsWith("contents") || 
                        childNode.getNodeName().toLowerCase().endsWith("contents")) {
                    return childNode;
                }

                // Traverse the child nodes of the current node
                this.getDocumentObjectNode(childNode);
            }
        }

        return null;
    }

    /**
     * Test the given 'Contents' node for required node names and values
     * 
     * @param node 'Contents' node that needs to be tested
     * @return Whether the given 'Contents' node contains the required node names and values
     */
    private boolean testContentsNode(Node node) {

        // Character array to store the results of the tests carried on the given 'Contents' node
        char[] resultSet = "000000".toCharArray();

        // Get the child nodes of the 'Contents' node
        NodeList children = node.getChildNodes();

        // Check if the 'Contents' node contains children
        if (children != null) {

            // Scan the list of child nodes and check for the 'Mime-type' node
            for (int i = 0; i < children.getLength(); i++) {

                // Current child node
                Node childNode = children.item(i);

                // Check the name of the child node
                if (childNode.getNodeName().toLowerCase().startsWith("mime-type") ||
                        childNode.getNodeName().toLowerCase().endsWith("mime-type")) {

                    // We have a potential Mime-type node
                    resultSet[0] = '1';

                    // Value node
                    Node valueNode = childNode.getFirstChild();

                    // Check if the value node was found
                    if (valueNode != null) {

                        // Check the value of the node
                        if (valueNode.getNodeValue().toLowerCase().equals("application/pdf")) {

                            // The required Mime-type value was found
                            resultSet[1] = '1';
                        }
                    }

                } else if (childNode.getNodeName().toLowerCase().startsWith("name") ||
                        childNode.getNodeName().toLowerCase().endsWith("name")) {

                    // We have a potential Name node
                    resultSet[2] = '1';

                    // Value node
                    Node valueNode = childNode.getFirstChild();
                    
                    // Check if the value node was found
                    if (valueNode != null) {
                        
                        // Check the value of the node for valid PDF filename
                        if (valueNode.getNodeValue().length() > 0 &&
                                valueNode.getNodeValue().toLowerCase().endsWith(".pdf")) {

                            // Set the value of the encoded PDF filename
                            this.encodedPdfFilename = valueNode.getNodeValue();

                            // We have the name of the PDF document
                            resultSet[3] = '1';
                        }
                    }

                } else if (childNode.getNodeName().toLowerCase().startsWith("data") ||
                        childNode.getNodeName().toLowerCase().endsWith("data")) {

                    // We have a potential Data node
                    resultSet[4] = '1';

                    // Value node
                    Node valueNode = childNode.getFirstChild();

                    // Check if the value node was found
                    if (valueNode != null) {

                        // Check the value of the Data node is not empty
                        if (valueNode.getNodeValue().length() > 0) {

                            // Encoded PDF file contents
                            this.encodedPdf = valueNode.getNodeValue();

                            // We have the Base64 encoded PDF file
                            resultSet[5] = '1';
                        }
                    }
                }
                
                // Check the result set of the tests carried out
                if (new String(resultSet).equals("111111")) {

                    // All tests were successful
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the path and name of the resulting PDF file
     * 
     * @return Path and name of the resulting PDF file
     * @throws PdfDocumentNotGeneratedException
     */
    private String getOutputPdfFilename() throws PdfDocumentNotGeneratedException {

        // Check if the PDF document has been generated
        if (this.pdfFilename.length() == 0) {
            throw new PdfDocumentNotGeneratedException();
        }

        return this.outputFolder + System.getProperty("file.separator") + this.pdfFilename;
    }

    /**
     * Create the preliminary output PDF file
     * 
     * @return Name of preliminary PDF file
     */
    private String getPreliminaryPdfFile() throws IOException {

        // Name of the preliminary PDF file
        String preliminaryPdfFilename = this.encodedPdfFilename.substring(0, this.encodedPdfFilename.length() - 4) +
                "_temp.pdf";

        // Write decoded PDF file contents to file
        SmicFileUtils.writeFile(Base64.decodeBase64(this.encodedPdf), this.tempFolder +
                System.getProperty("file.separator") + preliminaryPdfFilename);

        return preliminaryPdfFilename;
    }

    /**
     * Create a missive XML document without the Document object which will later be embedded in the output PDF file
     * 
     * @param documentObjectNode DOcument object node that needs to be removed from the missive XML document object
     * @return Name of the missive XML document without the Document object node
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private String generateMissiveWithoutDocumentObject(Node documentObjectNode) throws 
            TransformerConfigurationException, TransformerException {

        // Name of missive XML document without encoded PDF document
        String missiveNoDocumentObjectFilename =
                this.encodedPdfFilename.substring(0, this.encodedPdfFilename.length() - 4) + "_temp.xml";

        // Remove the Document object from the missive XML document object
        documentObjectNode.getParentNode().removeChild(documentObjectNode);

        // Normalise the missive XML document after removal of Document object
        this.xmlDocument.normalizeDocument();

        // Write the document to file
        SmicFileUtils.writeFile(this.xmlDocument, this.tempFolder + System.getProperty("file.separator") +
                missiveNoDocumentObjectFilename);

        return missiveNoDocumentObjectFilename;
    }

    /**
     * Create new PDF file with the contents of the decoded PDF file and add the given missive XML and other required
     * XMP tags to the specified PDF file
     * 
     * @param generatorName Name of the PDF document generator that will appear on the final output PDF
     * @param pdfFilename Preliminary PDF filename
     * @param xmlFilename Missive XML document filename
     * @throws IOException
     * @throws InvalidPdfPageNumberException
     * @throws DocumentException
     */
    private void createFinalPdfDocument(String generatorName, String pdfFilename, String xmlFilename)
            throws IOException, InvalidPdfPageNumberException, DocumentException {

        // Final PDF filename
        String finalPdfFilename = pdfFilename.substring(0, pdfFilename.length() - 9) + ".pdf";

        // Read the preliminary PDF file
        PdfReader pdfReader = new PdfReader(this.tempFolder + System.getProperty("file.separator") + pdfFilename);

        // Get the size of the PDF document page
        Rectangle pageSize = SmicPdfUtils.getPageSize(pdfReader, 1);

        // Output filename
        String outputFilename = this.outputFolder + System.getProperty("file.separator") + finalPdfFilename;

        // Create PDF document using the print size of the ODS document
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(pageSize);

        // Final PDF document output file
        File outFile = new File(outputFilename);

        // PDF document content writer
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
        PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);

        // PDF document version
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_5);
        writer.setPDFXConformance(PdfWriter.PDFA1A);
        writer.setTagged();

        // Open the final PDF document
        document.open();

        // Decoded PDF document info
        HashMap<String, String> documentInfo = pdfReader.getInfo();
        
        // PDF document tags
        document.addAuthor(documentInfo.get("Author"));
        document.addCreator(documentInfo.get("Creator"));
        document.addCreationDate();
        document.addKeywords(documentInfo.get("Keywords"));
        document.addProducer();
        document.addSubject(documentInfo.get("Subject"));
        document.addTitle(documentInfo.get("Title"));

        // Get a handle to PDF document content
        PdfContentByte contentByte = writer.getDirectContent();

        // Import the first page of the decoded PDF file
        PdfImportedPage page = writer.getImportedPage(pdfReader, 1);

        // Add the duplicated page to the new PDF document
        contentByte.addTemplate(page, 0, 0);

        // Set the colour profile of the document
        PdfDictionary outputIntent = new PdfDictionary(PdfName.OUTPUTINTENT);
        
        // Colour profile dictionary properties
        outputIntent.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("sRGB IEC61966-2.1"));
        outputIntent.put(PdfName.INFO, new PdfString("sRGB IEC61966-2.1"));
        outputIntent.put(PdfName.S, PdfName.GTS_PDFA1);

        // Load the PDF document ICC profile
        ICC_Profile icc = ICC_Profile.getInstance(SmicXml.class.getResourceAsStream("srgb.icc"));

        // PDF ICC profile
        PdfICCBased pdfIcc = new PdfICCBased(icc);
        pdfIcc.remove(PdfName.ALTERNATE);
        
        // Add profile to PDF document
        outputIntent.put(PdfName.DESTOUTPUTPROFILE, writer.addToBody(pdfIcc).getIndirectReference()); 
        writer.getExtraCatalog().put(PdfName.OUTPUTINTENTS, new PdfArray(outputIntent)); 

        // Byte array output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       
        // XMP data writer
        XmpWriter xmp = new XmpWriter(baos);

        // XMP DublinCore schema
        DublinCoreSchema dcs = new DublinCoreSchema();

        // DublinCore schema properties
        dcs.addAuthor(documentInfo.get("Author"));
        dcs.addDescription("");
        dcs.addPublisher("smurf");
        dcs.addSubject(documentInfo.get("Subject"));
        dcs.addTitle(documentInfo.get("Title"));

        // Add DublinCore data to XMP
        xmp.addRdfDescription(dcs);

        // XMP schema for SEPAmail properties
        XmpBasicSchema cp = new XmpBasicSchema();

        // Encoded false
        String jFalse = "false";
        byte[] bFalse = jFalse.getBytes("UTF-8");

        // Encoded generator name
        byte[] bGeneratorName = generatorName.getBytes("UTF-8");

        // SEPAmail XMP properties
        cp.setProperty("xmp:sepamail_missive", SmicFileUtils.encodedMissiveXmlContent(this.tempFolder +
                System.getProperty("file.separator") + xmlFilename));
        cp.setProperty("xmp:sepamail_document.signed", new String(bFalse, "UTF-8"));
        cp.setProperty("xmp:sepamail_document.generator", new String(bGeneratorName, "UTF-8"));

        // Add SEPAmail data to XMP
        xmp.addRdfDescription(cp);

        // XMP schema for PDF conformance
        PdfA1Schema cs = new PdfA1Schema();

        // Set conformance schema properties
        cs.addConformance("A");

        // Add conformance schema to PDF
        xmp.addRdfDescription(cs);

        // Close XMP writer
        xmp.close();

        // Add XMP data to the PDF file
        writer.setXmpMetadata(baos.toByteArray());

        // Close the PDF document
        document.close();

        // Set the name of the final PDF file
        this.pdfFilename = finalPdfFilename;
    }
}
