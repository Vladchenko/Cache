package ru.cache.vlad.yanchenko.caches;

import java.util.logging.Logger;

/**
 * Created by v.yanchenko on 02.09.2016.
 */
public class DirectoryException extends Exception {

    public DirectoryException() {
        super();
    }

    public DirectoryException(String message) { super(message); }

    public DirectoryException(String message, Logger logger) {
        logger.info("\"" + message + "\"" +
                " is not a valid pathname.");
//        System.exit(1);
    }

    public DirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryException(Throwable cause) {
        super(cause);
    }}
