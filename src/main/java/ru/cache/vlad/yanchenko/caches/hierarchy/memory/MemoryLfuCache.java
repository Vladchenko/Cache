package ru.cache.vlad.yanchenko.caches.hierarchy.memory;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Least frequently used type memory cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public class MemoryLfuCache<T, V> extends AbstractMemoryCache<T, V> implements ICache<T, V> {

    private T minimumRequestedEntryKey;
    // How many times each key was requested
    private final Map<T, Integer> mapFrequency;
    private int minimumRequestsNumberAmongEntries = Integer.MAX_VALUE;

    /**
     * Public constructor. Creates an instance of a class.
     *
     * @param size of a cache
     */
    public MemoryLfuCache(int size) {
        super(size);
        mapFrequency = new HashMap<>();
    }

    @Override
    public void clearCache() throws IOException {
        super.clearCache();
        mapFrequency.clear();
    }

    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException {
        mapFrequency.put(key, mapFrequency.get(key) + 1);
        return super.getEntry(key);
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) {
        super.putEntry(key, cacheEntry);
        mapFrequency.put(key, 1);
    }

    @Override
    public void removeEntry(@NonNull T key) {
        cacheEntries.remove(key);
        mapFrequency.remove(key);
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
