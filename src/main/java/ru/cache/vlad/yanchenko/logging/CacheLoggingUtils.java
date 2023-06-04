package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.Repository;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.*;

/** Utils to log the caching events. */
public class CacheLoggingUtils {

    private static Logger sLogger;
    private static final String LOGGER_NAME = "myLog";

    /** Get logger to log the events */
    public static Logger getLogger() {
        if  (sLogger == null) {
            sLogger = Logger.getLogger(LOGGER_NAME);
            sLogger.setUseParentHandlers(false);
            setupLogging(sLogger);
            Logger LOGGER = Logger.getLogger(CacheLoggingUtils.class.getName());
        }
        return sLogger;
    }

    private static void setupLogging(@NonNull Logger logger) {
        try {
            FileHandler fileHandler = new FileHandler("Cache.log", 1000000, 1);
//            ConsoleHandler fileHandler = new ConsoleHandler();
            MyFormatter formatter = new MyFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Writing a summary about a current caching process to a log file. */
    public static void printSummary(@NonNull Repository repository) {
        sLogger.info("");
        sLogger.info("--- Summary ---------------------------------------");
        sLogger.info("| Cache algorithm : " + repository.getCacheKind());
        sLogger.info("| Pipeline ran for: " + repository.getPipelineRunTimes() + " times");
        sLogger.info("| RAM cache hits  : " + repository.getHitsRAMCache() + " times");
        sLogger.info("| HDD cache hits  : " + repository.getHitsHDDCache() + " times");
        sLogger.info("| RAM cache misses: " + repository.getMissesRAMCache() + " times");
        sLogger.info("| HDD cache misses: " + repository.getMissesHDDCache() + " times");
        sLogger.info("---------------------------------------------------");
    }

    /**
     * Printing the command line arguments
     *
     * @param map of command line parameters
     */
    public static void printArgs(@NonNull Map<String, String> map) {
        if (map.isEmpty()) {
            sLogger.info("No command line arguments present");
        } else {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                sLogger.info(key + "=" + value);
            }
        }
    }

    /** Overriding an implementation of a standard formatter. It's done to remove excessive information that is put to log. */
    static class MyFormatter extends SimpleFormatter {

        private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

        // Changing a string that is going to be put to a log file.
        @Override
        public synchronized String format(@NonNull LogRecord record) {
            return String.format(format,
                    new Date(record.getMillis()),
                    record.getLevel().getLocalizedName(),
                    record.getMessage()
            );
        }
    }
}
