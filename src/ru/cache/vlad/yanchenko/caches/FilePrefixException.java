package ru.cache.vlad.yanchenko.caches;

import java.util.logging.Logger;

/**
 * Created by v.yanchenko on 05.09.2016.
 */
public class FilePrefixException extends Exception {

    public FilePrefixException(String message, Logger logger) {
        logger.info("\"" + message + "\"" + " is not a valid file prefix.");
//        System.exit(1);
    }
}
