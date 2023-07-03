package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Arguments utils class
 */
public final class ArgumentsUtils {

    private ArgumentsUtils() {
        throw new IllegalStateException("Do not create an instance of a util class");
    }

    /**
     * Printing the command line arguments
     *
     * @param map of command line parameters
     * @param logger to print the arguments
     */
    public static void printArgs(@NonNull Logger logger, @NonNull Map<String, String> map) {
        logger.info("Command line arguments are:");
        if (map.isEmpty()) {
            logger.info("No command line arguments present");
        } else {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                logger.info("\t\t {} = {}", key, value);
            }
        }
    }
}
