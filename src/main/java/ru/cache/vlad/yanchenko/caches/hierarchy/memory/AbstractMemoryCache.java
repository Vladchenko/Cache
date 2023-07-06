package ru.cache.vlad.yanchenko.caches.hierarchy.memory;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.caches.hierarchy.AbstractCache;

import java.io.IOException;
import java.util.HashMap;

/**
 * Memory abstract cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public abstract class AbstractMemoryCache<T, V> extends AbstractCache<T, V> implements ICache<T, V> {

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    protected AbstractMemoryCache(int size) {
        cacheEntries = new HashMap<>();
        this.size = size;
    }

    @Override
    public boolean hasCacheEntry(@NonNull T key) {
        return cacheEntries.containsKey(key);
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) {
        cacheEntries.put(key, cacheEntry);
    }

    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException {
        return cacheEntries.get(key);
    }

    @Override
    public void removeEntry(@NonNull T key) {
        cacheEntries.remove(key);
    }

    // Clear cache is identical to a parent implementation
}
