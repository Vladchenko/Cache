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
public final class FileUtils {

    // To perform a check if a filename, extension or have any of these letters.
    private static final String FILE_SPECIAL_CHARACTERS = "[\\\\:<>|*/?]";
    /**
     * Folder for a files that represents a 2nd level cache (HDD cache)
     */
    public static final String FILES_FOLDER = "Cache Data\\";
    /**
     * File prefix for files to be cached
     */
    public static final String FILE_PREFIX = "cache_file_";
    /**
     * File extension for files to be cached
     */
    public static final String FILE_EXTENSION = ".cache";

    private FileUtils() {
    }

    /**
     * Validate file path.
     *
     * @param logger to log events
     * @throws DirectoryException, when a file path is not valid
     */
    public static void validateFilePath(@NonNull Logger logger) throws DirectoryException {
        // To perform a check if a path is to have any of these letters.
        String pathSpecialCharacters = "[:<>|*/?]";
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(FILES_FOLDER);
        if (m.find()) {
            // Some special characters are present, thus throw an exception.
            throw new DirectoryException("Folder path \"" + FILES_FOLDER + "\" has some special letters.", logger);
        } else {
            // If filepath has no backslash at the end, add it.
            if (FILES_FOLDER.lastIndexOf('\\') != FILES_FOLDER.length() - 1) {
                logger.error("Add slash at the end of path " + FILES_FOLDER);
            }
        }
    }

    /**
     * Validate file prefix.
     *
     * @param logger to log events
     * @throws FilePrefixException, when a file prefix is not valid
     */
    public static void validateFilePrefix(@NonNull Logger logger) throws FilePrefixException {
        Pattern p = Pattern.compile(FILE_SPECIAL_CHARACTERS);
        Matcher m = p.matcher(FILE_PREFIX);
        if (m.find()) {
            // Some special characters are present, thus throw an exception
            throw new FilePrefixException("File prefix \"" + FILE_PREFIX + "\" has some special letters.", logger);
        } else {
            logger.info("File prefix is set to: " + FILE_PREFIX);
        }
    }

    /**
     * Validate file extension.
     *
     * @param logger to log events
     * @throws FileExtensionException, when a file extension is not valid
     */
    public static void validateFileExtension(@NonNull Logger logger) throws FileExtensionException {
        Pattern p = Pattern.compile(FILE_SPECIAL_CHARACTERS);
        Matcher m = p.matcher(FILE_EXTENSION);
        if (m.find()) { // Some special characters are present, thus ...
            // Throwing an exception.
            throw new FileExtensionException("File prefix \"" + FILE_EXTENSION + "\" has some special letters.", logger);
            // Make a folder named, some valid path, say a current time.
//            filePrefix = "cache_file";
//            repository.getLogger().info("It is set to default " + filePrefix);
        } else {
            logger.info("File extension is set to: " + FILE_EXTENSION);
        }
    }

    /**
     * Create a folder (in case its absent) for a files that constitute an HDD cache.
     *
     * @param logger to log events
     */
    public static boolean createFilesFolder(@NonNull Logger logger) throws DirectoryException {
        File directory = new File(FILES_FOLDER);
        // Checking if a directory keeps the real path on a disk.
        if (!directory.isDirectory()) {
            throw new DirectoryException(
                    FILES_FOLDER + " is not a valid pathname. Change and rerun an app. Program exits.",
                    logger);
        }
        // Checking if directory exists.
        if (!directory.exists()) {
            // And if not, create it.
            return directory.mkdir();
        } else {
            logger.info("HDD cache files folder already exists");
        }
        return false;
    }
}
