package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Cache arguments parser
 */
public class CacheArgumentsParser {

    private final Logger mLogger;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger logger to log the events
     */
    public CacheArgumentsParser(@NonNull Logger logger) {
        mLogger = logger;
    }

    /**
     * Reading arguments entered from command line.
     *
     * @param args command line arguments
     */
    public Map<String, String> readArguments(@NonNull String[] args) {
        // Delimiter that separates key from value in an entry.
        String delimiters = "[=]+";    // Use = as a delimiter
        // Map that represents "key=argument" entries, like (n=50).
        Map<String, String> arguments = new HashMap<>();
        String[] keyValueArg;
        String currentArg;
        for (String arg : args) {
            currentArg = arg.toLowerCase(Locale.ROOT);
            // Checking if a detailed report is enabled.
            if (currentArg.equals("dr")) {
                arguments.put("dr", "true");
                continue;
            }
            // Checking if a testing is enabled.
            if (currentArg.equals("test")) {
                arguments.put("test", "true");
                continue;
            }
            keyValueArg = currentArg.split(delimiters);
            try {
                if (!keyValueArg[0].isEmpty()) {
                    arguments.put(keyValueArg[0].toLowerCase(), keyValueArg[1].toLowerCase());
                }
            } catch (Exception ex) {
                mLogger.info("Some wrong argument present, check it.");
            }
        }
        return arguments;
    }
}
