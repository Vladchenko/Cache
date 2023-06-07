package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;

import java.io.IOException;

/**
 * Cache population utility.
 */
public final class CachePopulationUtils {

    private CachePopulationUtils() {
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
}
