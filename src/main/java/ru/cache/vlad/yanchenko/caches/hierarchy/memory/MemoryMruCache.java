package ru.cache.vlad.yanchenko.caches.hierarchy.memory;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;

/**
 * Most recently used type memory cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class MemoryMruCache<T, V> extends AbstractMemoryCache<T, V> implements ICache<T, V> {

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public MemoryMruCache(int size) {
        super(size);
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) {
        super.putEntry(key, cacheEntry);
        lastAccessedEntryKey = key;
    }

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        return lastAccessedEntryKey;
    }
}
