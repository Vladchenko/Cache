package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

import java.util.logging.Logger;

/**
 * Exception for file directory being not valid.
 * <p>
 * Created by v.yanchenko on 02.09.2016.
 */
public class DirectoryException extends Exception {

    /**
     * Create an exception for file directory being not valid.
     *
     * @param message message for exception
     * @param logger  class to log this exception
     */
    public DirectoryException(@NonNull String message, @NonNull Logger logger) {
        logger.info("\"" + message + "\"" + " is not a valid pathname.");
    }
}
