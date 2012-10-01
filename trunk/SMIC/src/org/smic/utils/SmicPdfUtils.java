package org.smic.utils;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.smic.exceptions.InvalidPdfPageNumberException;

/**
 * SmicPdfUtils is a class which groups static methods pertaining PDF file operations used throughout the SMIC module.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class SmicPdfUtils {

    /**
     * Get the size of a page in a PDF document
     * 
     * @param pdfReader PDF reader instance which has been initialized with the PDF file that needs to be read
     * @param page Page number
     * @return Size of PDF page in the PDF document
     * @throws InvalidPdfPageNumberException
     */
    public static Rectangle getPageSize(PdfReader pdfReader, int page) throws InvalidPdfPageNumberException {

        // Check if the document contains the specified page number
        if (page > pdfReader.getNumberOfPages() || page < 1) {

            // Throw exception since page number does not exist in the PDF document
            throw new InvalidPdfPageNumberException(page);
        }

        // Get page size
        return pdfReader.getPageSize(page);
    }

    /**
     * Encode PDF given file content in base 64
     * 
     * @param pdfFilename Path and name of the PDF file that needs to be encoded
     * @return Base64 file content encoded string
     * @throws IOException 
     */
    public static String encodePdf(String pdfFilename) throws IOException {

        // PDF file
        File pdfFile = new File(pdfFilename);

        // PDF file content byte array
        byte[] content;

        // Try to read the content of the PDF file
        try (FileInputStream fis = new FileInputStream(pdfFile)) {

            // Set the size of the content byte array
            content = new byte[(int)pdfFile.length()];

            // Read the content of the PDF file
            fis.read(content);
        }

        // Base 64 encoder instance
        Base64 encoder = new Base64();

        // Encoded PDF file content
        return encoder.encodeToString(content);
    }
}
