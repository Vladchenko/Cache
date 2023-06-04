/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * In charge of an operations made with a RAM cache.
 * 
 * @author v.yanchenko
 */
public class RAMCache extends AbstractCache implements Serializable, ICache {

    private final Logger mLogger;   //TODO Log the cache events
    private final Repository mRepository;
    
    // Only for LFU algorithm
    LinkedHashMap<Integer, HashMap<Object, Object>> mMapObjectsLFU;
    Map<Object, Integer> mMapFrequency;

    /**
     * Public constructor. Provides dependencies and creates instance of class
     *
     * @param logger        to log the cache events
     * @param repository    keeps environment data
     */
    public RAMCache(@NonNull Logger logger, @NonNull Repository repository) {
        mLogger = logger;
        mRepository = repository;
        // Defining which kind of map to be used, depending on a cache kind.
        switch (repository.getCacheKind()) {
            case LFU -> {
                mMapObjectsLFU = new LinkedHashMap<>();
                mMapFrequency = new HashMap<>();
            }
            case LRU -> mMapEntries = new LinkedHashMap<>();
            case MRU -> mMapEntries = new HashMap<>();
        }
        mMapFrequency = new LinkedHashMap<>();
    }

    @Override
    public void clearCache() {
        mMapEntries.clear();
        mMapFrequency.clear();
    }

    @Override
    public Object getCacheEntry(@NonNull Object key) {
        mMapFrequency.put(key, mMapFrequency.get(key) + 1);
        mLastAccessedEntryKey = key;
        switch (mRepository.getCacheKind()) {
            case LFU -> { }
            case LRU -> {
                /*
                 * Since "lru" states that object to be moved is the one that
                 * was least recently used, then we should put every requested
                 * object to the end of the LinkedHashMap. Finally, we will have
                 * a list of an objects beginning with least used, an ending
                 * with most used.
                 */
                mTempObject = mMapEntries.get(key);
                mMapEntries.remove(key);
                mMapEntries.put(key, mTempObject);
            }
            case MRU -> {
                mLastAccessedEntryKey = key;
                return mLastAccessedEntryKey;
            }
        }
        return mMapEntries.get(key);
    }

    @Override
    public void addCacheEntry(@NonNull Object key, @NonNull Object obj) {
        mLastAccessedEntryKey = key;
        mMapEntries.put(key, obj);
        mMapFrequency.put(key, 1);
        mSize++;
    }

    @Override
    public void removeCacheEntry(@NonNull Object key) {
        mMapEntries.remove(key);
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
        return mMapEntries.containsKey(key);
    }

    @Override
    public Object getLeastUsed(@NonNull Repository.cacheKindEnum cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU -> { }
            case LRU -> {
                /*
                 * Getting the first key from a map of objects, since first is
                 * the one that was used least recently.
                 */
                return mMapEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return mLastAccessedEntryKey;
            }
        }
        return null;
    }

}
