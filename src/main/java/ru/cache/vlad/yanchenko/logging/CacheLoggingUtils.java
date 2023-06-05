package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.util.Map;

/**
 * Utils to log the caching events.
 */
public class CacheLoggingUtils {

    private static Logger sLogger;

    /**
     * Get logger to log the events
     */
    public static Logger getLogger() {
        if (sLogger == null) {
            sLogger = LogManager.getLogger(CacheLoggingUtils.class);
        }
        return sLogger;
    }

    /**
     * Writing a summary about a current caching process to a log file.
     *
     * @param ramCache  memory cache
     * @param hddCache  disk cache
     * @param arguments from command line
     */
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
        sLogger.info("Command line arguments are:");
        if (map.isEmpty()) {
            sLogger.info("No command line arguments present");
        } else {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                sLogger.info("\t\t" + key + "=" + value);
            }
        }
    }
}
