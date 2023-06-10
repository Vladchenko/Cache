package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.io.IOException;
import java.util.Map;

/**
 * Cache population utility.
 */
public final class CacheUtils {

    private CacheUtils() {
    }

    /**
     * Populating caches before running a caching-retrieval process.
     *
     * @param ramCache    memory cache
     * @param hddCache    disk cache
     * @param cacheFeeder cache data feeder
     * @throws IOException when a disk operating problem occurs
     */
    public static void populateCaches(
            @NonNull ICache ramCache,
            @NonNull ICache hddCache,
            @NonNull CacheFeeder cacheFeeder) throws IOException {
        while (ramCache.getSize() < ramCache.getEntriesNumber()) {
            ramCache.putEntry(cacheFeeder.requestObject(),
                    cacheFeeder.deliverObject(cacheFeeder.requestObject()));
        }
        while (hddCache.getSize() < hddCache.getEntriesNumber()) {
            hddCache.putEntry(cacheFeeder.requestObject(),
                    cacheFeeder.deliverObject(cacheFeeder.requestObject()));
        }
    }

    /**
     * Logging a contents of both the caches.
     *
     * @param logger   to log contents of a caches
     * @param ramCache memory cache
     * @param hddCache disk cache
     */
    public static void logCaches(@NonNull Logger logger, @NonNull ICache ramCache, @NonNull ICache hddCache) {
//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        logger.info("********    RAM cache contents    ********");
        if (ramCache.getCacheEntries() != null) {
            if (ramCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : ramCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue().toString();
                logger.info("\tkey=" + key + ", value=" + value);
            }
            logger.info("********    HDD cache contents    ********");
            if (hddCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : hddCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                logger.info("\tkey=" + key + ", value=" + ((String) value).split("[\\\\]+")[1]);
            }
        }
    }
}
