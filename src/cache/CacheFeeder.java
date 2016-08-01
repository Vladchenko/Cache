/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class CacheFeeder {
    
    private int i = 0;
    // Number of an entries that a data map is to have. 
    private int entryNumber = 20;
    // Data that is gonna be fed to a cacheProcessor.
//    Map<String, Object> dataMap = new HashMap();
    private String[] data = new String [entryNumber];
    
    CacheFeeder() { 
        populateData();
    }
    
    // Adding entry to a map that holds the data to be fed to a cacheProcessor.
    private void addEntry(Map<String, Object> map) {
        map.put(Integer.toString((int)(Math.random() * 1000000000)), 
                Integer.toString((int)(Math.random() * 1000000000)));
    }
    
    // Populates data that is going to be fed to a cacheProcessor.
    private void populateData() {
//        for (int i = 0; i < entryNumber; i++) {
//            addEntry(dataMap);
//        }
        for (int i = 0; i < entryNumber; i++) {
            data[i] = Integer.toString((int)(Math.random() * 1000000000));
        }
    }
    
    public Object feed(String key) {
        i = (int)(Math.random() * entryNumber); 
        return data[i];
    }
    
    public String dummyAddress() {
        i = (int)(Math.random() * entryNumber); 
        return data[i];
    }
    
}
