package ru.cache.vlad.yanchenko.caches.hierarchy.disk;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Most recently used type disk cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class DiskMruCache<T, V> extends AbstractDiskCache<T, V> implements ICache<T, V> {

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public DiskMruCache(int size) {
        super(size);
        cacheEntries = new HashMap<>();
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) throws IOException {
        lastAccessedEntryKey = key;
        super.putEntry(key, cacheEntry);
    }

    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException, NotPresentException {
        lastAccessedEntryKey = key;
        return super.getEntry(key);
    }

    @Override
    public void removeEntry(@NonNull T key) {
        cacheEntries.remove(lastAccessedEntryKey);
    }

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        return lastAccessedEntryKey;
    }
}
