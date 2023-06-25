package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class simulates a source where the objects, that are not present in cache,
 * are downloaded from. It also simulates a requests sent to a caches.
 *
 * @author v.yanchenko
 */
public class CacheFeeder {

    // Number of an entries that a data map is to have.
    private final int entryNumber;
    // Data that is going to be fed to a cacheProcessor.
    private Map<Object, Object> keyToObjectsMap;
    private final Random random = new Random();
    // Array of objects for one could get by index.
    private final Object[] values;

    /**
     * Constructor that creates an instance of a class
     *
     * @param entryNumber maximum number of entries present in a cache
     */
    public CacheFeeder(int entryNumber) {
        this.entryNumber = entryNumber;
        // Array that keeps the keys to all the maps, for further picking a 
        // random key out of it, that will be requested from cacheProcessor.
        values = new Object[entryNumber];
        keyToObjectsMap = new HashMap<>();
        keyToObjectsMap = populateMap();
    }

    /**
     * Populating data that is going to be fed to a cacheProcessor.
     *
     * @return map of cache entries <key, cached-object>
     */
    public Map<Object, Object> populateMap() {
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < entryNumber; i++) {
            values[i] = Integer.toString(random.nextInt(1000000000));
            String key = values[i].toString();
            map.put(key, Integer.toString(random.nextInt(1000000000)));
        }
        return map;
    }

    /**
     * Copying one map into another
     *
     * @param map to be copied
     * @return copied map
     */
    public Map<Object, Object> copyData(Map<Object, Object> map) {
        Map<Object, Object> newMap = new HashMap<>();
        for (Map.Entry<Object, Object> entrySet : map.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();
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
    public Object deliverObject(@NonNull String key) {
        return keyToObjectsMap.get(key);
    }

    /**
     * Randomly pick a key and further fetch it to a cacheProcessor, for it could get it from its caches or download
     * from alleged source and finally, retrieve to alleged CPU.
     *
     * @return TODO
     */
    public String fetchObject() {
        int i = (int) (random.nextDouble() * entryNumber);
        return (String) values[i];
    }

    /**
     * Set map of objects for cache
     *
     * @param keyToObjectsMap the mapObjectsFed to set
     */
    public void setKeyToObjectsMap(@NonNull Map<Object, Object> keyToObjectsMap) {
        this.keyToObjectsMap = keyToObjectsMap;
    }
}
