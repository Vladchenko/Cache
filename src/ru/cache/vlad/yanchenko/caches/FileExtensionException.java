package ru.cache.vlad.yanchenko.caches;

import java.util.logging.Logger;

/**
 * Created by v.yanchenko on 05.09.2016.
 */
public class FileExtensionException extends Exception {

    public FileExtensionException(String message, Logger logger) {
        logger.info("\"" + message + "\"" + " is not a valid file extension.");
//        System.exit(1);
    }
}
