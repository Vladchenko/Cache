
package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

import org.apache.logging.log4j.Logger;

/**
 * Exception for cache entry not present in cache.
 *
 * @author v.yanchenko
 */
public class NotPresentException extends Exception {

    /**
     * Create an exception to cache entry being absent.
     *
     * @param message message for exception
     * @param logger  class to log this exception
     */
    public NotPresentException(@NonNull String message, @NonNull Logger logger) {
        logger.info("\"" + message + "\"" + " is not present in cache.");
    }
}
