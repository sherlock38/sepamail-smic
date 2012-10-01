package org.smic.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * SmicDirectoryUtils is a class which groups static methods pertaining folder operations used throughout the SMIC
 * module.
 * 
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmicDirectoryUtils {

    /**
     * Get the current working directory of the application
     *
     * @return The absolute path to the application working directory
     */
    public static String getCurrentWorkingDirectory() {

        String path;

        // Current class directory
        URL location = SmicDirectoryUtils.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            path = URLDecoder.decode(location.getPath(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            path = location.getPath();
        }

        // Absolute current working directory
        //return new File(path).getParentFile().getPath() + System.getProperty("file.separator") + ".";

        // Absolute current working directory - added for debugging
        return new File(path).getParentFile().getPath() + System.getProperty("file.separator") + "./..";
    }

    /**
     * Check if a specified folder exists and create it if required
     * 
     * @param folderName Path and name of folder
     */
    public static void createFolderIfNotExist(String folderName) {

        // Check if the folder name has been specified
        if ((folderName != null) && (folderName.length() > 0)) {

            // File object for specified path and folder name
            File folder = new File(folderName);

            // Check if the file exist and is a directory
            if (!((folder.exists()) && (folder.isDirectory()))) {

                // Create the required folder
                folder.mkdir();
            }
        }
    }
}
