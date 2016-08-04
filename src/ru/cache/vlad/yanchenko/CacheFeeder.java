/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class CacheFeeder {
    
    private int i = 0;
    // Number of an entries that a data map is to have. 
    private int entryNumber = 0;
    // Data that is gonna be fed to a cacheProcessor.
//    Map<String, Object> dataMap = new HashMap();
    private String[] values;
    private Map<Object, Object> mapObjectsFed;
    
    CacheFeeder(int enrtyNumber) { 
        this.entryNumber = enrtyNumber;
        values = new String [entryNumber];
        mapObjectsFed = new HashMap();
        populateData();
    }
    
    public CacheFeeder(Map<Object, Object> map) {
        mapObjectsFed = map;
    }
    
    // Adding entry to a map that holds the data to be fed to a cacheProcessor.
    private void addEntry(Map<Object, Object> map) {
        map.put(Integer.toString((int)(Math.random() * 1000000000)), 
                Integer.toString((int)(Math.random() * 1000000000)));
    }
    
    // Populating data that is going to be fed to a cacheProcessor.
    private void populateData() {
        for (int i = 0; i < entryNumber; i++) {
            values[i] = Integer.toString((int)(Math.random() * 1000000000));
            addEntry(mapObjectsFed);
        }
    }
    
    public Object deliverObject(String key) {
        i = (int)(Math.random() * entryNumber); 
        return values[i];
    }
    
    public String requestObject() {
        // Should randomly pick a 
        i = (int)(Math.random() * entryNumber); 
        return values[i];
    }
    
}
