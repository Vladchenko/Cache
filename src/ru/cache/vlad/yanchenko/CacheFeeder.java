/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.util.HashMap;
import java.util.Map;

/**
 * Class simulates a source where the object, that are not present in cache, 
 * are downloaded from. It also simulates a requests sent to a caches.
 * 
 * @author v.yanchenko
 */
public class CacheFeeder {

    private int i = 0;
    // Number of an entries that a data map is to have. 
    private int entryNumber = 0;
    // Data that is gonna be fed to a cacheProcessor.
    private Map<Object, Object> mapObjectsFed;
    /** 
     * Array of objects for one could refer to objects by index, to get a fast 
     * retrieval.
     */
    private Object[] arrValues;

    CacheFeeder(int enrtyNumber) {
        this.entryNumber = enrtyNumber;
        // Array that keeps the keys to all the maps, for further picking a 
        // random key out of it, that will be requested from cacheProcessor.
        arrValues = new Object[entryNumber];
        mapObjectsFed = new HashMap();
        mapObjectsFed = populateData();
//        arrValues = (new ArrayList<Object>(mapObjectsFed.keySet())).toArray();
        
    }

    // Populating data that is going to be fed to a cacheProcessor.
    public Map<Object, Object> populateData() {
        Map<Object, Object> map = new HashMap();
        for (int i = 0; i < getEntryNumber(); i++) {
            arrValues[i] = Integer.toString((int) (Math.random() * 1000000000));
            String key = arrValues[i].toString();
            map.put(key, Integer.toString((int) (Math.random() * 1000000000)));
        }
        return map;
    }
    
    // Copying one map into another
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
     * @param key
     * @return 
     */
    public Object deliverObject(String key) {
        return getMapObjectsFed().get(key);
    }

    /**
     * Randomly picks a key and further gives it to a cacheProcessor, for it
     * could get it from its caches or download from alleged source and 
     * finally, retrieve to alleged CPU.
     */
    public String requestObject() {
        i = (int) (Math.random() * getEntryNumber());
        return (String)arrValues[i];
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">

    /**
     * @return the arrValues
     */
    public Object[] getArrValues() {
        return arrValues;
    }

    /**
     * @param arrValues the arrValues to set
     */
    public void setArrValues(Object[] arrValues) {
        this.arrValues = arrValues;
    }
    
    /**
     * @return the entryNumber
     */
    public int getEntryNumber() {
        return entryNumber;
    }
    
    /**
     * @param entryNumber the entryNumber to set
     */
    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }
    
    /**
     * @return the mapObjectsFed
     */
    public Map<Object, Object> getMapObjectsFed() {
        return mapObjectsFed;
    }
    
    /**
     * @param mapObjectsFed the mapObjectsFed to set
     */
    public void setMapObjectsFed(Map<Object, Object> mapObjectsFed) {
        this.mapObjectsFed = mapObjectsFed;
    }
//</editor-fold>

}
