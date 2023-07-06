package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;

/**
 * Utils to log the caching events.
 */
public final class CacheLoggingUtils<T, V> {

    /**
     * Writing a summary about a current caching process to a log file.
     *
     * @param logger    to log summary of caching process
     * @param ramCache  memory cache
     * @param hddCache  disk cache
     * @param arguments from command line
     */
    public void printSummary(@NonNull Logger logger,
                             @NonNull ICache<T, V> ramCache,
                             @NonNull ICache<T, V> hddCache,
                             @NonNull Map<String, String> arguments) {
        logger.info("╔══╣Summary╠═══════════════════════╗");
        logger.info("║ Cache algorithm : {}", arguments.get(CACHE_KIND_ARGUMENT_KEY));
        logger.info("║ Pipeline ran for: {} times", arguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY));
        logger.info("║ RAM cache hits  : {} times", ramCache.getCacheHits());
        logger.info("║ HDD cache hits  : {} times", hddCache.getCacheHits());
        logger.info("║ RAM cache misses: {} times", ramCache.getCacheMisses());
        logger.info("║ HDD cache misses: {} times", hddCache.getCacheMisses());
        logger.info("╚══════════════════════════════════╝");
    }

    /**
     * Logging a contents of both the caches.
     *
     * @param logger   to log contents of a caches
     * @param ramCache memory cache
     * @param hddCache disk cache
     */
    public void printCachesContents(@NonNull Logger logger,
                                    @NonNull ICache<T, V> ramCache,
                                    @NonNull ICache<T, V> hddCache) {
//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        logger.info("********    RAM cache contents    ********");
        if (ramCache.getCacheEntries() != null) {
            if (ramCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<T, V> entrySet : ramCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue().toString();
                logger.info("\tkey = {}, value={}", key, value);
            }
            logger.info("********    HDD cache contents    ********");
            if (hddCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<T, V> entrySet : hddCache.getCacheEntries().entrySet()) {
                T key = entrySet.getKey();
                V value = entrySet.getValue();
                logger.info("\tkey={}, value={}", key, value);
            }
        }
    }
}
