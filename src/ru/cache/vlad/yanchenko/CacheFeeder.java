/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class CacheFeeder {

    private int i = 0;
    // Number of an entries that a data map is to have. 
    private int entryNumber = 0;
    private String[] values;
    // Data that is gonna be fed to a cacheProcessor.
    private Map<Object, Object> mapObjectsFed;
    Object[] arrValues;

    CacheFeeder(int enrtyNumber) {
        this.entryNumber = enrtyNumber;
        // Array that keeps the keys to all the maps, for further picking a 
        // random key out of it, that will be requested from cacheProcessor.
        arrValues = new Object[entryNumber];
        mapObjectsFed = new HashMap();
        mapObjectsFed = populateData(mapObjectsFed);
//        arrValues = (new ArrayList<Object>(mapObjectsFed.keySet())).toArray();
        
    }

    // Populating data that is going to be fed to a cacheProcessor.
    private Map<Object, Object> populateData(Map<Object, Object> map) {
        for (int i = 0; i < entryNumber; i++) {
            arrValues[i] = Integer.toString((int) (Math.random() * 1000000000));
            String key = arrValues[i].toString();
            map.put(key, Integer.toString((int) (Math.random() * 1000000000)));
        }
        return map;
    }

    /**
     * This is an alleged source where object are downloaded from.
     * @param key
     * @return 
     */
    public Object deliverObject(String key) {
        return mapObjectsFed.get(key);
    }

    /**
     * Randomly picks a key and further gives it to a cacheProcessor, for it
     * could get it from its caches or download from alleged source and 
     * finally, retrieve to alleged CPU.
     */
    public String requestObject() {
        i = (int) (Math.random() * entryNumber);
        return (String)arrValues[i];
    }

    public void runAllegedPipeline() {
        
    }
}
