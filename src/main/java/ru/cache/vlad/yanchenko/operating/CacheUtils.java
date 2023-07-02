package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.io.IOException;
import java.util.Map;

/**
 * Cache population utils.
 */
public final class CacheUtils<T, V> {

    /**
     * Populating caches before running a caching-retrieval process.
     *
     * @param ramCache    memory cache
     * @param hddCache    disk cache
     * @param cacheFeeder cache data feeder
     * @throws IOException when a disk operating problem occurs
     */
    public void populateCaches(
            @NonNull ICache<T, V> ramCache,
            @NonNull ICache<T, V> hddCache,
            @NonNull CacheFeeder<T, V> cacheFeeder) throws IOException {
        while (ramCache.getSize() < ramCache.getEntriesNumber()) {
            ramCache.putEntry(cacheFeeder.fetchKey(), cacheFeeder.deliverCacheEntry(cacheFeeder.fetchKey()));
        }
        while (hddCache.getSize() < hddCache.getEntriesNumber()) {
            hddCache.putEntry(cacheFeeder.fetchKey(), cacheFeeder.deliverCacheEntry(cacheFeeder.fetchKey()));
        }
    }

    /**
     * Logging a contents of both the caches.
     *
     * @param logger   to log contents of a caches
     * @param ramCache memory cache
     * @param hddCache disk cache
     */
    public void logCaches(@NonNull Logger logger, @NonNull ICache<T, V> ramCache, @NonNull ICache<T, V> hddCache) {
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
                logger.info("\tkey=" + key + ", value=" + value);
            }
            logger.info("********    HDD cache contents    ********");
            if (hddCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<T, V> entrySet : hddCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                logger.info("\tkey=" + key + ", value=" + ((String) value).split("[/]+")[1]);
            }
        }
    }
}
