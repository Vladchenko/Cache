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
     * @param logger      to log summary of caching process
     * @param memoryCache memory cache
     * @param diskCache   disk cache
     * @param arguments   from command line
     */
    public void printSummary(@NonNull Logger logger,
                             @NonNull ICache<T, V> memoryCache,
                             @NonNull ICache<T, V> diskCache,
                             @NonNull Map<String, String> arguments) {
        logger.info("╔══╣Summary╠═══════════════════════╗");
        logger.info("║ Cache algorithm     : {}", arguments.get(CACHE_KIND_ARGUMENT_KEY));
        logger.info("║ Pipeline ran for    : {} times", arguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY));
        logger.info("║ Memory cache hits   : {} times", memoryCache.getCacheHits());
        logger.info("║ Disk cache hits     : {} times", diskCache.getCacheHits());
        logger.info("║ Memory cache misses : {} times", memoryCache.getCacheMisses());
        logger.info("║ Disk cache misses   : {} times", diskCache.getCacheMisses());
        logger.info("╚══════════════════════════════════╝");
    }

    /**
     * Logging a contents of both the caches.
     *
     * @param logger      to log contents of a caches
     * @param memoryCache memory cache
     * @param diskCache   disk cache
     */
    public void printCachesContents(@NonNull Logger logger,
                                    @NonNull ICache<T, V> memoryCache,
                                    @NonNull ICache<T, V> diskCache) {
        logger.info("********    Memory cache contents    ********");
        if (memoryCache.getCacheEntries() != null) {
            if (memoryCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<T, V> entrySet : memoryCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue().toString();
                logger.info("\tkey = {}, value={}", key, value);
            }
            logger.info("********    Disk cache contents    ********");
            if (diskCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<T, V> entrySet : diskCache.getCacheEntries().entrySet()) {
                T key = entrySet.getKey();
                V value = entrySet.getValue();
                logger.info("\tkey={}, value={}", key, value);
            }
        }
    }
}
