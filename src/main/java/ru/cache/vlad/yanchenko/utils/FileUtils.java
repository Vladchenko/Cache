package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.FileExtensionException;
import ru.cache.vlad.yanchenko.exceptions.FilePrefixException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checking validity of all the fields in the application.
 */
public final class FileUtils {

    // To perform a check if a filename, extension or have any of these letters.
    private static final String FILE_SPECIAL_CHARACTERS = "[\\\\:<>|*/?]";
    /**
     * Folder for a files that represents a 2nd level cache (Disk cache)
     */
    public static final String FILES_FOLDER = "Cache Data/";
    /**
     * File prefix for files to be cached
     */
    public static final String FILE_PREFIX = "cache_file_";
    /**
     * File extension for files to be cached
     */
    public static final String FILE_EXTENSION = ".cache";

    private FileUtils() {
        throw new IllegalStateException("Do not create an instance of a util class");
    }

    /**
     * Validate file path.
     *
     * @param logger to log events
     * @throws DirectoryException, when a file path is not valid
     */
    public static void validateFilePath(@NonNull Logger logger) throws DirectoryException {
        // To perform a check if a path is to have any of these letters.
        String pathSpecialCharacters = "[:<>|*?]";
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(FILES_FOLDER);
        if (m.find()) {
            // Some special characters are present, thus throw an exception.
            throw new DirectoryException("Folder path \"" + FILES_FOLDER + "\" has some special letters.", logger);
        } else {
            // If filepath has no slash at the end, add it.
            if (FILES_FOLDER.lastIndexOf('/') != FILES_FOLDER.length() - 1) {
                logger.error("Add slash at the end of path {}", FILES_FOLDER);
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
            logger.info("File prefix is set to: {}", FILE_PREFIX);
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
        } else {
            logger.info("File extension is set to: {}", FILE_EXTENSION);
        }
    }

    /**
     * Create a folder (in case its absent) for a files that constitute a disk cache.
     *
     * @param logger to log events
     */
    public static void createDiskCacheFolder(@NonNull Logger logger) {
        Path path = Path.of(FILES_FOLDER);
        // Check if a directory exists,
        if (!Files.exists(path)) {
            // and if not, create it.
            try {
                Files.createDirectory(path);
            } catch (IOException ioex) {
                logger.error(ioex);
            }
        } else {
            logger.info("Disk cache files folder already exists");
        }
    }
}
