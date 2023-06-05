package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;

import java.io.IOException;

/**
 *
 */
public class CachePopulationUtils {

    // Populating caches before running a caching-retrieval process.
    public static void populateCaches(
            @NonNull Logger logger,
            @NonNull ICache ramCache,
            @NonNull ICache hddCache,
            @NonNull CacheFeeder cacheFeeder) throws IOException {
        while (ramCache.getSize() < ramCache.getEntriesNumber()) {
            ramCache.addCacheEntry(cacheFeeder.requestObject(),
                    cacheFeeder.deliverObject(cacheFeeder.requestObject()));
        }
        while (hddCache.getSize() < hddCache.getEntriesNumber()) {
            try {
                hddCache.addCacheEntry(cacheFeeder.requestObject(),
                        cacheFeeder.deliverObject(cacheFeeder.requestObject()));
            } catch (IOException ex) {
                logger.info("Cannot populate HDD cache, some IO problem.");
            }
        }
        logger.info("Caches have been populated.");
    }
}
