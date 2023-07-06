package ru.cache.vlad.yanchenko.caches.hierarchy.disk;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Least frequently used type disk cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class DiskLfuCache<T, V> extends AbstractDiskCache<T, V> implements ICache<T, V> {

    private T minimumRequestedEntryKey;
    // How many times each key was requested
    private final Map<T, Integer> mapFrequency;
    // Keeps a minimum value of a requests been made among all the cache entries
    private int minimumRequestsNumberAmongEntries = Integer.MAX_VALUE;

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public DiskLfuCache(int size) {
        super(size);
        mapFrequency = new HashMap<>();
        cacheEntries = new HashMap<>();
    }

    @Override
    public void clearCache() throws IOException {
        super.clearCache();
        mapFrequency.clear();
    }

    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException, NotPresentException {
        mapFrequency.put(key, mapFrequency.get(key) + 1);
        return super.getEntry(key);
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) throws IOException {
        super.putEntry(key, cacheEntry);
        mapFrequency.put(key, 1);
    }

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        // Search for a minimum demanded entry's key
        Iterator<Map.Entry<T, Integer>> iterator = mapFrequency.entrySet().iterator();
        while (iterator.hasNext()) {
            if (minimumRequestsNumberAmongEntries > iterator.next().getValue()) {
                minimumRequestsNumberAmongEntries = iterator.next().getValue();
                minimumRequestedEntryKey = iterator.next().getKey();
            }
        }
        return minimumRequestedEntryKey;
    }
}
