
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.IOException;
import java.util.Map;

/**
 * Contract for a cache instance to implement.
 *
 * @author v.yanchenko
 */
public interface ICache<T, V> {

    /**
     * @return entries present in a cache
     */
    Map<T, V> getCacheEntries();

    /**
     * Clearing a cache of all the entries.
     */
    void clearCache() throws IOException;

    /**
     * @return requested entry V by key T.
     */
    V getEntry(@NonNull T key) throws NotPresentException, IOException, ClassNotFoundException;

    /**
     * Adding an entry to a cache.
     *
     * @param key        to get a cache entry by
     * @param cacheEntry cache entry
     */
    void putEntry(@NonNull T key, @NonNull V cacheEntry) throws IOException;

    /**
     * Getting the least used entry to make an eviction.
     */
    T getLeastUsedEntryKey(@NonNull CacheKind cacheKind);

    /**
     * Checking if a specific object exists.
     *
     * @param key to get some entry from cache
     */
    boolean hasCacheEntry(@NonNull T key);

    /**
     * Get number of cache hits
     */
    int getCacheHits();

    /**
     * Set number of cache hits
     *
     * @param hits number of entries that were fetched from cache
     */
    void setCacheHits(int hits);

    /**
     * Get number of cache misses
     */
    int getCacheMisses();

    /**
     * Set number of cache misses
     *
     * @param misses number of entries that were not fetched from cache
     */
    void setCacheMisses(int misses);

    /**
     * @return get entries number present
     */
    int getEntriesNumber();

    /**
     * Reset some cache statistics.
     */
    void resetCacheStatistics();

    /**
     * Remove entry
     *
     * @param key to remove entry by
     */
    void removeEntry(@NonNull T key) throws NotPresentException;

    /**
     * @return key of entry to remove from cache
     */
    T getKeyLastAccessed();

    /**
     * Retrieve size of cache (number of entries in it)
     */
    int getSize();

    /**
     * Set cache maximum size.
     *
     * @param size of cache
     */
    void setSize(int size);
}
