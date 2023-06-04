/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.exceptions;

import android.support.annotation.NonNull;

import java.util.logging.Logger;

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
        logger.info("\"" + message + "\"" + " is not a valid file prefix.");
    }
}
