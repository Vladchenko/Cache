
package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

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
     */
    public NotPresentException(@NonNull String message) {
        super(message);
    }
}
