
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.LEVEL_1_CACHE_SIZE_ARGUMENT_KEY;

/**
 * In charge of an operations done with a RAM cache.
 *
 * @author v.yanchenko
 */
public class RAMCache<T, V> extends AbstractCache<T, V> implements Serializable, ICache<T, V> {

    private final Map<String, String> commandLineArguments;

    // Number of hits to a RAM cache done during one caching process.
    private int cacheHits = 0;
    // Number of misses to a RAM cache done during one caching process.
    private int cacheMisses = 0;
    // Number of entries to be present in a RAM cache.
    private int cacheEntriesNumber;

    Map<T, Integer> mapFrequency;
    // Only for LFU algorithm
    LinkedHashMap<Integer, HashMap<T, V>> mapObjectsLFU;

    /**
     * Public constructor. Provides dependencies and creates instance of class
     *
     * @param arguments from command line
     */
    public RAMCache(@NonNull Map<String, String> arguments) {
        commandLineArguments = arguments;
        cacheEntriesNumber = Integer.parseInt(commandLineArguments.get(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY));
        // Defining which kind of map to be used, depending on a cache kind.
        switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
            case LFU -> {
                mapObjectsLFU = new LinkedHashMap<>();
                mapFrequency = new HashMap<>();
            }
            case LRU -> cacheEntries = new LinkedHashMap<>();
            case MRU -> cacheEntries = new HashMap<>();
        }
        mapFrequency = new LinkedHashMap<>();
    }

    @Override
    public Map<T, V> getCacheEntries() {
        return cacheEntries;
    }

    @Override
    public void clearCache() {
        cacheEntries.clear();
        mapFrequency.clear();
    }

    @Override
    public V getEntry(@NonNull T key) {
        mapFrequency.put(key, mapFrequency.get(key) + 1);
        switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY).toUpperCase(Locale.ROOT))) {
            case LFU -> {
                //TODO
            }
            case LRU -> {
                /*
                 * Since "lru" states that object to be moved is the one that was least recently used, then one should
                 * put every requested object to the end of the LinkedHashMap. Finally, one will have a list of
                 * an objects beginning with least used, an ending with most used.
                 */
                tempCacheEntry = cacheEntries.get(key);
                cacheEntries.remove(key);
                cacheEntries.put(key, tempCacheEntry);
            }
            case MRU -> {
                lastAccessedEntryKey = key;
                return cacheEntries.get(lastAccessedEntryKey);
            }
        }
        return cacheEntries.get(key);
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) {
        lastAccessedEntryKey = key;
        cacheEntries.put(key, cacheEntry);
        mapFrequency.put(key, 1);
        size++;
    }

    @Override
    public void removeEntry(@NonNull T key) {
        cacheEntries.remove(key);
        mapFrequency.remove(key);
        size--;
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
    public boolean hasCacheEntry(@NonNull T key) {
        return cacheEntries.containsKey(key);
    }

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU -> {
            }
            case LRU -> {
                // Getting the first key from a map of objects, since first is the one that was used least recently.
                return cacheEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return lastAccessedEntryKey;
            }
        }
        return null;
    }

    @Override
    public int getCacheHits() {
        return cacheHits;
    }

    @Override
    public void setCacheHits(int hitsRAMCache) {
        cacheHits = hitsRAMCache;
    }

    @Override
    public int getCacheMisses() {
        return cacheMisses;
    }

    @Override
    public void setCacheMisses(int missesRAMCache) {
        cacheMisses = missesRAMCache;
    }

    @Override
    public int getEntriesNumber() {
        return cacheEntriesNumber;
    }

    @Override
    public void resetCacheStatistics() {
        cacheMisses = 0;
        cacheHits = 0;
    }

    /**
     * Set number of entries for RAM cache
     *
     * @param ramCacheEntriesNumber Number of entries for RAM cache
     */
    public void setEntriesNumber(int ramCacheEntriesNumber) {
        cacheEntriesNumber = ramCacheEntriesNumber;
    }

}
