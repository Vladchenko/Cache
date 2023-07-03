package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.FileExtensionException;
import ru.cache.vlad.yanchenko.exceptions.FilePrefixException;

/**
 * Some data validating utils.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        throw new IllegalStateException("Do not create an instance of a util class");
    }

    /**
     * Validate some command line arguments.
     *
     * @param logger to log the validation events
     */
    public static void validateFileConstants(@NonNull Logger logger) {
        try {
            FileUtils.validateFilePath(logger);
        } catch (DirectoryException e) {
            logger.error("{} is not a valid folder. Program exits.", FileUtils.FILES_FOLDER);
            System.exit(1);
        }
        try {
            FileUtils.validateFilePrefix(logger);
        } catch (FilePrefixException e) {
            logger.error("{} is not a valid file prefix. Program exits.", FileUtils.FILE_PREFIX);
            System.exit(1);
        }
        try {
            FileUtils.validateFileExtension(logger);
        } catch (FileExtensionException e) {
            logger.error("{} is not a valid file extension. Program exits.", FileUtils.FILE_EXTENSION);
            System.exit(1);
        }
    }
}
