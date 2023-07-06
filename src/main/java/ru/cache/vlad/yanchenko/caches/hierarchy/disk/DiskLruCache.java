package ru.cache.vlad.yanchenko.caches.hierarchy.disk;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.util.LinkedHashMap;

/**
 * Least recently used type disk cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class DiskLruCache<T, V> extends AbstractDiskCache<T, V> implements ICache<T, V> {

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public DiskLruCache(int size) {
        super(size);
        cacheEntries = new LinkedHashMap<>();
    }

    @Override
    public V getEntry(@NonNull T key) throws NotPresentException {
        if (hasCacheEntry(key)) {
            V cacheEntry = cacheEntries.get(key);
            cacheEntries.remove(key);
            cacheEntries.put(key, cacheEntry);
            return cacheEntry;
        } else {
            throw new NotPresentException(key.toString());
        }
    }

    // Put entry seems no need to override

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        // Getting the first key from a map of cache entries, since first is the one that was used least recently.
        return cacheEntries.entrySet().iterator().next().getKey();
    }
}
