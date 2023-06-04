package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.FileExtensionException;
import ru.cache.vlad.yanchenko.exceptions.FilePrefixException;

/**
 *
 */
public class ValidatingUtils {
    /**
     * Validate some command line arguments.
     * TODO Maybe validate some more ?
     * @param logger to log the validation events
     */
    public static void validateArguments(@NonNull Logger logger) {
        FileUtils fileUtils = new FileUtils(logger);
        try {
            fileUtils.validateFilePath(CacheConstants.FILES_FOLDER);
        } catch (DirectoryException e) {
            logger.error(CacheConstants.FILES_FOLDER + " is not a valid folder. Program exits.");
            System.exit(1);
        }
        try {
            fileUtils.validateFilePrefix(CacheConstants.FILE_PREFIX);
        } catch (FilePrefixException e) {
            logger.error(CacheConstants.FILE_PREFIX + " is not a valid file prefix. Program exits.");
            System.exit(1);
        }
        try {
            fileUtils.validateFileExtension(CacheConstants.FILE_EXTENSION);
        } catch (FileExtensionException e) {
            logger.error(CacheConstants.FILE_EXTENSION + " is not a valid file extension. Program exits.");
            System.exit(1);
        }
    }
}
