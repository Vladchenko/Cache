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

    private static Logger sLogger;

    private CacheLoggingUtils() { }

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
        sLogger.info("╔══╣Summary╠═══════════════════════╗");
        sLogger.info("║ Cache algorithm : " + arguments.get(CACHE_KIND_ARGUMENT_KEY));
        sLogger.info("║ Pipeline ran for: " + arguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY) + " times");
        sLogger.info("║ RAM cache hits  : " + ramCache.getCacheHits() + " times");
        sLogger.info("║ HDD cache hits  : " + hddCache.getCacheHits() + " times");
        sLogger.info("║ RAM cache misses: " + ramCache.getCacheMisses() + " times");
        sLogger.info("║ HDD cache misses: " + hddCache.getCacheMisses() + " times");
        sLogger.info("╚══════════════════════════════════╝");
    }
}
