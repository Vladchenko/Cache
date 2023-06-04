package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.util.Date;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/** Utils to log the caching events. */
public class CacheLoggingUtils {

    private static Logger sLogger;

    /** Get logger to log the events */
    public static Logger getLogger() {
        if  (sLogger == null) {
            sLogger = LogManager.getLogger(CacheLoggingUtils.class);
        }
        return sLogger;
    }
    
    /** Writing a summary about a current caching process to a log file. */
    public static void printSummary(@NonNull ICache ramCache,
                                    @NonNull ICache hddCache,
                                    @NonNull Map<String, String> arguments) {
        sLogger.info("");
        sLogger.info("--- Summary ---------------------------------------");
        sLogger.info("| Cache algorithm : " + arguments.get("cachekind"));
        sLogger.info("| Pipeline ran for: " + arguments.get("n") + " times");
        sLogger.info("| RAM cache hits  : " + ramCache.getCacheHits() + " times");
        sLogger.info("| HDD cache hits  : " + hddCache.getCacheHits() + " times");
        sLogger.info("| RAM cache misses: " + ramCache.getCacheMisses() + " times");
        sLogger.info("| HDD cache misses: " + hddCache.getCacheMisses() + " times");
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
