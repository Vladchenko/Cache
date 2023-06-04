package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

import org.apache.logging.log4j.Logger;

/**
 * Exception for file extension being wrong.
 *
 * Created by v.yanchenko on 05.09.2016.
 */
public class FileExtensionException extends Exception {

    /**
     * Create an exception to file extension being wrong.
     *
     * @param message message for exception
     * @param logger  class to log this exception
     */
    public FileExtensionException(@NonNull String message, @NonNull Logger logger) {
        logger.info("\"" + message + "\"" + " is not a valid file extension.");
    }
}
