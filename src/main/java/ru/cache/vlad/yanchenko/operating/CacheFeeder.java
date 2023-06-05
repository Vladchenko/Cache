package ru.cache.vlad.yanchenko.operating;

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
    private final int mEntryNumber;
    // Data that is going to be fed to a cacheProcessor.
    private Map<Object, Object> mMapObjectsFed;
    private final Random mRandom = new Random();
    // Array of objects for one could refer to objects by index, to get a fast retrieval.
    private final Object[] mArrValues;

    /**
     * Constructor that creates an instance of a class
     *
     * @param entryNumber maximum number of entries present in a cache
     */
    public CacheFeeder(int entryNumber) {
        mEntryNumber = entryNumber;
        // Array that keeps the keys to all the maps, for further picking a 
        // random key out of it, that will be requested from cacheProcessor.
        mArrValues = new Object[entryNumber];
        mMapObjectsFed = new HashMap<>();
        mMapObjectsFed = populateData();
    }

    /**
     * Populating data that is going to be fed to a cacheProcessor.
     *
     * @return map of cache entries <key, cached-object>
     */
    public Map<Object, Object> populateData() {
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < mEntryNumber; i++) {
            mArrValues[i] = Integer.toString(mRandom.nextInt(1000000000));
            String key = mArrValues[i].toString();
            map.put(key, Integer.toString(mRandom.nextInt(1000000000)));
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
    public Object deliverObject(String key) {
        return mMapObjectsFed.get(key);
    }

    /**
     * Randomly picks a key and further gives it to a cacheProcessor, for it
     * could get it from its caches or download from alleged source and
     * finally, retrieve to alleged CPU.
     */
    public String requestObject() {
        int i = (int) (mRandom.nextDouble() * mEntryNumber);
        return (String) mArrValues[i];
    }

    /** @param mapObjectsFed the mapObjectsFed to set */
    public void setMapObjectsFed(Map<Object, Object> mapObjectsFed) {
        mMapObjectsFed = mapObjectsFed;
    }
}
