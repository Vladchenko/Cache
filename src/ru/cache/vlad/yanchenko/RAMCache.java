/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class RAMCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();
    Map<Object, Object> mapObjects;
//    Map.Entry<String, Object> mapObject;
    Object obj;
    Map<Object, Integer> frequency;
    Object keyLastAccessed;
    int size = 0;

    public RAMCache() {
        // Defining which kind of a map to be used, depending on a cache kind.
        switch (repository.cacheKind) {
            case LFU: {
                mapObjects = new HashMap();
                break;
            }
            case LRU: {
                mapObjects = new LinkedHashMap();
                break;
            }
            case LRR: {
                mapObjects = new LinkedHashMap();
                break;
            }
            case MRU: {
                mapObjects = new HashMap();
                break;
            }
        }
        frequency = new LinkedHashMap();
    }

    @Override
    public void clearCache() {
        mapObjects.clear();
    }

    @Override
    public Object getObject(Object key) {
        frequency.put(key, frequency.get(key) + 1);
        keyLastAccessed = key;
        switch (repository.cacheKind) {
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
                obj = mapObjects.get(key);
                mapObjects.remove(key);
                mapObjects.put(key, obj);
                break;
            }
            case LRR: {
                break;
            }
            case MRU: {
                keyLastAccessed = key;
                return keyLastAccessed;
            }
        }
        return mapObjects.get(key);
    }

    @Override
    public void addObject(Object key, Object obj) {
        keyLastAccessed = key;
        mapObjects.put(key, obj);
        frequency.put(key, 1);
        size++;
    }

    @Override
    public void removeObject(Object key) {
        mapObjects.remove(key);
        frequency.remove(key);
        size--;
//        if (lfu) {
//            remove the one with a least frequency
//        }
//        if (lru) {
//            remove the one that used a long time ago
//        }
//        if (mru) {
//            remove the one that used at most recent time
//        }
    }

    @Override
    public int getSize() {
        return mapObjects.size();
    }

    @Override
    public boolean hasObject(Object key) {
        return mapObjects.containsKey(key);
    }

    @Override
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind) {
//        return frequency.lastKey();
        switch (cacheKind) {
            case LFU: {
                break;
            }
            case LRR: {
                /**
                 * Getting the first key from a map of objects, i.e. the first
                 * downloaded object.
                 */
//                String theLastKey = new ArrayList<>(
//                        mapObjects.keySet()).get(mapObjects.size() - 1);
//                return theLastKey;
                return mapObjects.entrySet().iterator().next().getKey();
            }
            case LRU: {
                /**
                 * Getting the first key from a map of objects, since first is
                 * the one that was used least recently.
                 */
                return mapObjects.entrySet().iterator().next().getKey();
            }
            case MRU: {
                return keyLastAccessed;
            }
        }
        return null;
    }

}
