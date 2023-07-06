package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.hierarchy.disk.DiskLfuCache;
import ru.cache.vlad.yanchenko.caches.hierarchy.disk.DiskLruCache;
import ru.cache.vlad.yanchenko.caches.hierarchy.disk.DiskMruCache;
import ru.cache.vlad.yanchenko.caches.hierarchy.memory.MemoryLfuCache;
import ru.cache.vlad.yanchenko.caches.hierarchy.memory.MemoryLruCache;
import ru.cache.vlad.yanchenko.caches.hierarchy.memory.MemoryMruCache;

import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Creates required cache.
 */
public class CachesFactory<T, V> {

    /**
     * Create a cache
     *
     * @param cacheType that define a required cache
     * @param arguments that keep needed data to crate cache
     * @return needed cache
     */
    public ICache<T, V> createCache(@NonNull CacheType cacheType, @NonNull Map<String, String> arguments) {
        int cacheSize;
        ICache<T, V> cache = null;
        switch (cacheType) {
            case MEMORY: {
                cacheSize = Integer.parseInt(arguments.get(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY));
                switch (CacheKind.valueOf(arguments.get(CACHE_KIND_ARGUMENT_KEY))) {
                    case LFU -> cache = new MemoryLfuCache<>(cacheSize);
                    case LRU -> cache = new MemoryLruCache<>(cacheSize);
                    case MRU -> cache = new MemoryMruCache<>(cacheSize);
                }
                break;
            }
            case DISK: {
                cacheSize = Integer.parseInt(arguments.get(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY));
                switch (CacheKind.valueOf(arguments.get(CACHE_KIND_ARGUMENT_KEY))) {
                    case LFU -> cache = new DiskLfuCache<>(cacheSize);
                    case LRU -> cache = new DiskLruCache<>(cacheSize);
                    case MRU -> cache = new DiskMruCache<>(cacheSize);
                }
                break;
            }
        }
        return cache;
    }
}
