package ru.cache.vlad.yanchenko.caches.hierarchy.memory;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;

/**
 * Least recently used type memory cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class MemoryLruCache<T, V> extends AbstractMemoryCache<T, V> implements ICache<T, V> {

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public MemoryLruCache(int size) {
        super(size);
    }

    // Clear cache seems identical to a parent's implementation

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        return cacheEntries.entrySet().iterator().next().getKey();
    }

}
