package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;

/**
 * Utils to log the caching events.
 */
public final class CacheLoggingUtils {

    private static Logger logger;

    private CacheLoggingUtils() { }

    /**
     * Get logger to log the events
     */
    public static Logger getLogger() {
        if (logger == null) {
            logger = LogManager.getLogger(CacheLoggingUtils.class);
        }
        return logger;
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
        logger.info("╔══╣Summary╠═══════════════════════╗");
        logger.info("║ Cache algorithm : " + arguments.get(CACHE_KIND_ARGUMENT_KEY));
        logger.info("║ Pipeline ran for: " + arguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY) + " times");
        logger.info("║ RAM cache hits  : " + ramCache.getCacheHits() + " times");
        logger.info("║ HDD cache hits  : " + hddCache.getCacheHits() + " times");
        logger.info("║ RAM cache misses: " + ramCache.getCacheMisses() + " times");
        logger.info("║ HDD cache misses: " + hddCache.getCacheMisses() + " times");
        logger.info("╚══════════════════════════════════╝");
    }

    /**
     * Printing the command line arguments
     *
     * @param map of command line parameters
     */
    public static void printArgs(@NonNull Map<String, String> map) {
        logger.info("Command line arguments are:");
        if (map.isEmpty()) {
            logger.info("No command line arguments present");
        } else {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                logger.info("\t\t" + key + "=" + value);
            }
        }
    }
}
