/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import ru.cache.vlad.yanchenko.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * In charge of an operations made with a RAM cache.
 * 
 * @author v.yanchenko
 */
public class RAMCache extends AbstractCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();
    
    // Only for LFU algorithm
    LinkedHashMap<Integer, HashMap<Object, Object>> mapObjectsLFU;
    Map<Object, Integer> mapFrequency;

    public RAMCache() {
        // Defining which kind of a map to be used, depending on a cache kind.
        switch (repository.getCacheKind()) {
            case LFU: {
                mapObjectsLFU = new LinkedHashMap();
                mapFrequency = new HashMap();
                break;
            }
            case LRU: {
                mapEntries = new LinkedHashMap();
                break;
            }
            case MRU: {
                mapEntries = new HashMap();
                break;
            }
        }
        mapFrequency = new LinkedHashMap();
    }

    @Override
    public void clearCache() {
        mapEntries.clear();
        mapFrequency.clear();
    }

    @Override
    public Object getObject(Object key) {
        mapFrequency.put(key, mapFrequency.get(key) + 1);
        keyLastAccessed = key;
        switch (repository.getCacheKind()) {
            case LFU: {
                break;
            }
            case LRU: {
                /**
                 * Since "lru" states that object to be moved is the one that
                 * was least recently used, then we should put every requested
                 * object to the end of the LinkedHashMap. Finally, we will have
                 * a list of an objects beginning with least used, an ending
                 * with most used.
                 */
                tempObject = mapEntries.get(key);
                mapEntries.remove(key);
                mapEntries.put(key, tempObject);
                break;
            }
            case MRU: {
                keyLastAccessed = key;
                return keyLastAccessed;
            }
        }
        return mapEntries.get(key);
    }

    @Override
    public void addObject(Object key, Object obj) {
        keyLastAccessed = key;
        mapEntries.put(key, obj);
        mapFrequency.put(key, 1);
        size++;
    }

    @Override
    public void removeObject(Object key) {
        mapEntries.remove(key);
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
    public boolean hasObject(Object key) {
        return mapEntries.containsKey(key);
    }

    @Override
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU: {
                break;
            }
            case LRU: {
                /**
                 * Getting the first key from a map of objects, since first is
                 * the one that was used least recently.
                 */
                return mapEntries.entrySet().iterator().next().getKey();
            }
            case MRU: {
                return keyLastAccessed;
            }
        }
        return null;
    }

}
