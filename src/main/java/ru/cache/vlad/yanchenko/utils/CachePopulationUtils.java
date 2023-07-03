package ru.cache.vlad.yanchenko.utils;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;

import java.io.IOException;

/**
 * Cache population utility.
 */
public final class CachePopulationUtils<T, V> {

    /**
     * Populating caches before running a caching-data-retrieval process.
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
}
