package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class simulates a source where the objects, that are not present in cache, are downloaded from.
 * It also simulates a requests sent to a caches.
 *
 * @author v.yanchenko
 */
public class CacheFeeder<T, V> {

    // Number of an entries that a data map is to have.
    private final int entryNumber;
    // Data that is going to be fed to a cacheProcessor.
    private Map<T, V> keysToObjectsMap;
    private final Random random = new Random();
    // Array of objects for one could get by index.
    private final T[] cacheKeys;

    /**
     * Constructor that creates an instance of a class
     *
     * @param entryNumber maximum number of entries present in a cache
     */
    public CacheFeeder(int entryNumber) {
        this.entryNumber = entryNumber;
        // Array that keeps the keys to all the maps, for further picking a random key out of it, that will be
        // requested from cacheProcessor.
        cacheKeys = (T[]) new Object[entryNumber];
        keysToObjectsMap = populateMap();
    }

    /**
     * Populating data that is going to be fed to a cacheProcessor.
     *
     * @return map of cache entries <key, cached-object>
     */
    public Map<T, V> populateMap() {
        Map<T, V> map = new HashMap<>();
        for (int i = 0; i < entryNumber; i++) {
            cacheKeys[i] = (T) Integer.toString(random.nextInt(1000000000));
            String key = cacheKeys[i].toString();
            map.put((T) key, (V) Integer.toString(random.nextInt(1000000000)));
        }
        return map;
    }

    /**
     * Copying one map into another
     *
     * @param map to be copied
     * @return copied map
     */
    public Map<T, V> copyData(Map<T, V> map) {
        Map<T, V> newMap = new HashMap<>();
        for (Map.Entry<T, V> entrySet : map.entrySet()) {
            T key = entrySet.getKey();
            V value = entrySet.getValue();
            newMap.put(key, value);
        }
        return newMap;
    }

    /**
     * This is an alleged source where object are downloaded from.
     *
     * @param key to get cached-object by
     * @return cached-object
     */
    public V deliverCacheEntry(@NonNull T key) {
        return keysToObjectsMap.get(key);
    }

    /**
     * @return randomly picked key
     */
    public T fetchKey() {
        int i = (int) (random.nextDouble() * entryNumber);
        return cacheKeys[i];
    }

    /**
     * Set map of objects for cache
     *
     * @param keysToObjectsMap a new keys->objects map to set
     */
    public void setKeysToObjectsMap(@NonNull Map<T, V> keysToObjectsMap) {
        this.keysToObjectsMap = keysToObjectsMap;
    }
}
