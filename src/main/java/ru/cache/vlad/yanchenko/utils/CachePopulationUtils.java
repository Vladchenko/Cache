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
     * @param memoryCache memory cache
     * @param diskCache   disk cache
     * @param cacheFeeder cache data feeder
     * @throws IOException when a disk operating problem occurs
     */
    public void populateCaches(
            @NonNull ICache<T, V> memoryCache,
            @NonNull ICache<T, V> diskCache,
            @NonNull CacheFeeder<T, V> cacheFeeder) throws IOException {
        while (memoryCache.getSize() > memoryCache.getEntriesNumber()) {
            memoryCache.putEntry(cacheFeeder.fetchKey(), cacheFeeder.deliverCacheEntry(cacheFeeder.fetchKey()));
        }
        while (diskCache.getSize() > diskCache.getEntriesNumber()) {
            diskCache.putEntry(cacheFeeder.fetchKey(), cacheFeeder.deliverCacheEntry(cacheFeeder.fetchKey()));
        }
    }
}
