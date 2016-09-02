package ru.cache.vlad.yanchenko.caches;

import java.util.logging.Logger;

/**
 * Created by v.yanchenko on 02.09.2016.
 */
public class WrongDirectoryException extends Exception {

    public WrongDirectoryException() {
        super();
    }

    public WrongDirectoryException(String message) { super(message); }

    public WrongDirectoryException(String message, Logger logger) {
        logger.info("\"" + message + "\"" +
                " is not a valid pathname. Change and rerun an app. Program exits.");
        System.exit(1);
    }

    public WrongDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongDirectoryException(Throwable cause) {
        super(cause);
    }}
