package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Cache abstract class for real caches to derive from.
 *
 * Created by v.yanchenko on 31.08.2016.
 */
public abstract class AbstractCache<T, V> implements ICache<T, V> {

    // Current size of a cache.
    protected int size = 0;
    // Key of an object that was accessed last.
    protected T lastAccessedEntryKey;
    // Reference to some object needed in a logic of an app.
    protected V tempCacheEntry;
    // Map that holds the keys to an entries that constitute a cache.
    protected Map<T, V> cacheEntries;

    public V getTempCacheEntry() {
        return tempCacheEntry;
    }

    public void setTempCacheEntry(@NonNull V tempCacheEntry) {
        this.tempCacheEntry = tempCacheEntry;
    }

    public T getKeyLastAccessed() {
        return lastAccessedEntryKey;
    }

    public void setKeyLastAccessed(@NonNull T keyLastAccessed) {
        lastAccessedEntryKey = keyLastAccessed;
    }

    public int getSize() {
        return cacheEntries.size();
    }

    public void setSize(int size) {
        this.size = size;
    }
}
