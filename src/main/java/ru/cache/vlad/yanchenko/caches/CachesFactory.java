package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Creates required cache.
 */
public class CachesFactory {

    /**
     * Create a cache
     *
     * @param cacheType that define a required cache
     * @param arguments that keep needed data to crate cache
     * @return needed cache
     */
    public ICache createCache(@NonNull CacheType cacheType, @NonNull Map<String, String> arguments) {
        switch (cacheType) {
            case RAM: {
                return new RAMCache(arguments);
            }
            case HDD: {
                return new HDDCache(arguments);
            }
        }
        return null;
    }
}
