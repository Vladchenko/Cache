
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
public interface ICache {

    /**
     * TODO
     */
    Map<Object, Object> getCacheEntries();

    /**
     * Clearing a cache of all the entries.
     */
    void clearCache() throws IOException;

    /**
     * Getting requested object by key.
     */
    Object getEntry(@NonNull Object key) throws IOException, ClassNotFoundException;

    /**
     * Adding an entry to a cache.
     */
    void putEntry(@NonNull Object key, @NonNull Object obj) throws IOException;

    /**
     * Getting the least used entry to make an eviction.
     */
    Object getLeastUsedEntry(@NonNull CacheKind cacheKind);

    /**
     * Checking if a specific object exists.
     *
     * @param key to get some entry from cache
     */
    boolean hasCacheEntry(@NonNull Object key);

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
     * Set entries number present in cache
     *
     * @param entriesNumber present in cache
     */
    void setEntriesNumber(int entriesNumber);

    /**
     * Reset some cache statistics.
     */
    void resetCacheStatistics();

    /**
     * Remove entry
     *
     * @param key to remove entry by
     */
    void removeEntry(@NonNull Object key) throws NotPresentException;

    /**
     * @return key of entry to remove from cache
     */
    Object getKeyLastAccessed();

    /**
     * Set last accessed entry's key
     *
     * @param keyLastAccessed
     */
    void setKeyLastAccessed(@NonNull Object keyLastAccessed);

    /**
     * Retrieve size of cache (number of entries in it)
     */
    int getSize();

}
