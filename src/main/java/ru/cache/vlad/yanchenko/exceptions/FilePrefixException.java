package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

import java.util.logging.Logger;

/**
 * Exception for file prefix being wrong.
 * <p>
 * Created by v.yanchenko on 05.09.2016.
 */
public class FilePrefixException extends Exception {

    /**
     * Create an exception to file prefix being wrong.
     *
     * @param message message for exception
     * @param logger  class to log this exception
     */
    public FilePrefixException(@NonNull String message, @NonNull Logger logger) {
        logger.info("\"" + message + "\"" + " is not a valid file prefix.");
    }
}
