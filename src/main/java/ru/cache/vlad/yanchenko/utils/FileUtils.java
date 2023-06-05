package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.FileExtensionException;
import ru.cache.vlad.yanchenko.exceptions.FilePrefixException;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checking validity of all the fields in the application.
 * <p>
 * Created by v.yanchenko on 02.09.2016.
 */
public class FileUtils {

    // Logging the operations.
    private static Logger sLogger;
    // To perform a check if a filename, extension or have any of these letters.
    private final String mFileSpecialCharacters = "[\\\\:<>|*/?]";

    /**
     * Creates an instance of class
     *
     * @param logger to log cache operations
     */
    public FileUtils(@NonNull Logger logger) {
        sLogger = logger;
    }

    /**
     * Validate file path.
     *
     * @param path to be validated
     * @throws DirectoryException, when a file path is not valid
     */
    public void validateFilePath(@NonNull String path) throws DirectoryException {
        // To perform a check if a path is to have any of these letters.
        String pathSpecialCharacters = "[:<>|*/?]";
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(path);
        if (m.find()) {
            // Some special characters are present, thus throw an exception.
            throw new DirectoryException("Folder path \"" + path + "\" has some special letters.", sLogger);
        } else {
            // If filepath has no backslash at the end, add it.
            if (path.lastIndexOf('\\') != path.length() - 1) {
                path += "\\";
                sLogger.info("Log file path is = " + path);
            }
        }
    }

    /**
     * Validate file prefix.
     *
     * @param filePrefix to be validated
     * @throws FilePrefixException, when a file prefix is not valid
     */
    public void validateFilePrefix(@NonNull String filePrefix) throws FilePrefixException {
        Pattern p = Pattern.compile(mFileSpecialCharacters);
        Matcher m = p.matcher(filePrefix);
        if (m.find()) {
            // Some special characters are present, thus throw an exception
            throw new FilePrefixException("File prefix \"" + filePrefix + "\" has some special letters.", sLogger);
        } else {
            sLogger.info("File prefix is set to: " + filePrefix);
        }
    }

    /**
     * Validate file extension.
     *
     * @param fileExtension to be validated
     * @throws FileExtensionException, when a file extension is not valid
     */
    public void validateFileExtension(@NonNull String fileExtension) throws FileExtensionException {
        Pattern p = Pattern.compile(mFileSpecialCharacters);
        Matcher m = p.matcher(fileExtension);
        if (m.find()) { // Some special characters are present, thus ...
            // Throwing an exception.
            throw new FileExtensionException("File prefix \"" + fileExtension + "\" has some special letters.", sLogger);
            // Make a folder named, some valid path, say a current time.
//            filePrefix = "cache_file";
//            repository.getLogger().info("It is set to default " + filePrefix);
        } else {
            sLogger.info("File prefix is set to: " + fileExtension);
        }
    }

    /**
     * Create a folder (in case its absent) for a files that constitute an HDD cache.
     *
     * @param path for a cache files folder
     */
    public static boolean createFilesFolder(@NonNull String path) throws DirectoryException {
        File directory = new File(path);
        // Checking if a directory keeps the real path on a disk.
        if (!directory.isDirectory()) {
            throw new DirectoryException(
                    path + " is not a valid pathname. Change and rerun an app. Program exits.",
                    sLogger);
        }
        // Checking if directory exists.
        if (!directory.exists()) {
            // And if not, create it.
            return directory.mkdir();
        } else {
            sLogger.info("HDD cache files folder already exists");
        }
        return false;
    }
}
