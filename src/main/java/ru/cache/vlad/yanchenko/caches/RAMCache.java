
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.ArgumentsConstants.LEVEL_1_CACHE_SIZE_ARGUMENT_KEY;

/**
 * In charge of an operations done with a RAM cache.
 *
 * @author v.yanchenko
 */
public class RAMCache extends AbstractCache implements Serializable, ICache {

    private final Map<String, String> mArguments;

    // Number of hits to a RAM cache done during one caching process.
    private int mCacheHits = 0;
    // Number of misses to a RAM cache done during one caching process.
    private int mCacheMisses = 0;
    // Number of entries to be present in a RAM cache.
    private int mCacheEntriesNumber;

    Map<Object, Integer> mMapFrequency;
    // Only for LFU algorithm
    LinkedHashMap<Integer, HashMap<Object, Object>> mMapObjectsLFU;

    /**
     * Public constructor. Provides dependencies and creates instance of class
     *
     * @param arguments from command line
     */
    public RAMCache(@NonNull Map<String, String> arguments) {
        mArguments = arguments;
        mCacheEntriesNumber = Integer.parseInt(mArguments.get(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY));
        // Defining which kind of map to be used, depending on a cache kind.
        switch (CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
            case LFU -> {
                mMapObjectsLFU = new LinkedHashMap<>();
                mMapFrequency = new HashMap<>();
            }
            case LRU -> mCacheEntries = new LinkedHashMap<>();
            case MRU -> mCacheEntries = new HashMap<>();
        }
        mMapFrequency = new LinkedHashMap<>();
    }

    @Override
    public Map<Object, Object> getCacheEntries() {
        return mCacheEntries;
    }

    @Override
    public void clearCache() {
        mCacheEntries.clear();
        mMapFrequency.clear();
    }

    @Override
    public Object getEntry(@NonNull Object key) {
        mMapFrequency.put(key, mMapFrequency.get(key) + 1);
        mLastAccessedEntryKey = key;
        switch (CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY).toUpperCase(Locale.ROOT))) {
            case LFU -> {
            }
            case LRU -> {
                /*
                 * Since "lru" states that object to be moved is the one that was least recently used, then one should
                 * put every requested object to the end of the LinkedHashMap. Finally, one will have a list of
                 * an objects beginning with least used, an ending with most used.
                 */
                mTempObject = mCacheEntries.get(key);
                mCacheEntries.remove(key);
                mCacheEntries.put(key, mTempObject);
            }
            case MRU -> {
                mLastAccessedEntryKey = key;
                return mLastAccessedEntryKey;
            }
        }
        return mCacheEntries.get(key);
    }

    @Override
    public void putEntry(@NonNull Object key, @NonNull Object obj) {
        mLastAccessedEntryKey = key;
        mCacheEntries.put(key, obj);
        mMapFrequency.put(key, 1);
        mSize++;
    }

    @Override
    public void removeCacheEntry(@NonNull Object key) {
        mCacheEntries.remove(key);
        mMapFrequency.remove(key);
        mSize--;
//        if (lfu) {
//            remove the one with a least mapFrequency
//        }
//        if (lru) {
//            remove the one that used a long time ago
//        }
//        if (mru) {
//            remove the one that used at most recent time
//        }
    }

    @Override
    public boolean hasCacheEntry(@NonNull Object key) {
        return mCacheEntries.containsKey(key);
    }

    @Override
    public Object getLeastUsed(@NonNull CacheKind cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU -> {
            }
            case LRU -> {
                /*
                 * Getting the first key from a map of objects, since first is
                 * the one that was used least recently.
                 */
                return mCacheEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return mLastAccessedEntryKey;
            }
        }
        return null;
    }

    /**
     * @return the hitsRAMCache
     */
    @Override
    public int getCacheHits() {
        return mCacheHits;
    }

    /**
     * @param hitsRAMCache the hitsRAMCache to set
     */
    @Override
    public void setCacheHits(int hitsRAMCache) {
        mCacheHits = hitsRAMCache;
    }

    /**
     * @return the missesRAMCache
     */
    @Override
    public int getCacheMisses() {
        return mCacheMisses;
    }

    /**
     * @param missesRAMCache the missesRAMCache to set
     */
    @Override
    public void setCacheMisses(int missesRAMCache) {
        mCacheMisses = missesRAMCache;
    }

    /**
     * TODO
     * @return
     */
    @Override
    public int getEntriesNumber() {
        return mCacheEntriesNumber;
    }

    @Override
    public void resetCacheStatistics() {
        mCacheMisses = 0;
        mCacheHits = 0;
    }

    /**
     * TODO
     * @param ramCacheEntriesNumber
     */
    public void setEntriesNumber(int ramCacheEntriesNumber) {
        mCacheEntriesNumber = ramCacheEntriesNumber;
    }

}
