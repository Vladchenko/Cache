package ru.cache.vlad.yanchenko.caches.hierarchy;

import ru.cache.vlad.yanchenko.caches.ICache;

import java.io.IOException;
import java.util.Map;

/**
 * Cache abstract class for real caches to derive from.
 *
 * Created by v.yanchenko on 31.08.2016.
 */
public abstract class AbstractCache<T, V> implements ICache<T, V> {

    // Maximum size of a cache.
    protected int size = 0;
    protected int cacheHits = 0;
    protected int cacheMisses = 0;
    // Reference to some object needed in a logic of an app.
    protected V tempCacheEntry;
    protected T lastAccessedEntryKey;
    // Map that holds the keys to an entries that constitute a cache.
    protected Map<T, V> cacheEntries;

    @Override
    public int getCacheHits() {
        return cacheHits;
    }

    @Override
    public void setCacheHits(int hitsHDDCache) {
        cacheHits = hitsHDDCache;
    }

    @Override
    public int getCacheMisses() {
        return cacheMisses;
    }

    @Override
    public void setCacheMisses(int missesHDDCache) {
        cacheMisses = missesHDDCache;
    }

    @Override
    public Map<T, V> getCacheEntries() {
        return cacheEntries;
    }

    @Override
    public int getEntriesNumber() {
        return cacheEntries.size();
    }

    @Override
    public void resetCacheStatistics() {
        cacheMisses = 0;
        cacheHits = 0;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void clearCache() throws IOException {
        cacheEntries.clear();
    }

    public T getKeyLastAccessed() {
        return lastAccessedEntryKey;
    }

    public int getSize() {
        return size;
    }
}
