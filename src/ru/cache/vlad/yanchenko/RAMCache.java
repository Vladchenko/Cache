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
 * In charge of an operations made with a RAM cache.
 * 
 * @author v.yanchenko
 */
public class RAMCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();
    
    // Only for LFU algorithm
    LinkedHashMap<Integer, HashMap<Object, Object>> mapObjectsLFU;
    Map<Object, Integer> mapFrequency;

    // Map of objects that incoporate a cache.
    private Map<Object, Object> mapObjects;
    private Object object;
    // Key to a last accessed object.
    private Object keyLastAccessed;
    private int size = 0;

    RAMCache() {
        // Defining which kind of a map to be used, depending on a cache kind.
        switch (repository.getCacheKind()) {
            case LFU: {
                mapObjectsLFU = new LinkedHashMap();
                mapFrequency = new HashMap();
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
        mapFrequency = new LinkedHashMap();
    }

    @Override
    public void clearCache() {
        mapObjects.clear();
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
                object = mapObjects.get(key);
                mapObjects.remove(key);
                mapObjects.put(key, object);
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
        mapFrequency.put(key, 1);
        size++;
    }

    @Override
    public void removeObject(Object key) {
        mapObjects.remove(key);
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
    public int getSize() {
        return mapObjects.size();
    }

    @Override
    public boolean hasObject(Object key) {
        return mapObjects.containsKey(key);
    }

    @Override
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind) {
//        return mapFrequency.lastKey();
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

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    public Map<Object, Object> getMapObjects() {
        return mapObjects;
    }
    
    public void setMapObjects(Map<Object, Object> mapObjects) {
        this.mapObjects = mapObjects;
    }
    
    public Object getObject() {
        return object;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    public Object getKeyLastAccessed() {
        return keyLastAccessed;
    }
    
    public void setKeyLastAccessed(Object keyLastAccessed) {
        this.keyLastAccessed = keyLastAccessed;
    }
    
    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
    //</editor-fold>

}
