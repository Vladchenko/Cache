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
public class CachePopulator {
    
    // Data that is gonna be fed to a cacheProcessor.
    Map<String, Object> dataMap = new HashMap();
    // Number of an entries that a data map is to have. 
    int entryNumber = 20;
    
    CachePopulator() { }
    
    // Adding entry to a map that holds the data to be fed to a cacheProcessor.
    public void addEntry(Map<String, Object> map) {
        map.put(Integer.toString((int)(Math.random() * 1000000000)), 
                Integer.toString((int)(Math.random() * 1000000000)));
    }
    
    // Populates data that is going to be fed to a cacheProcessor.
    public void populateData() {
        for (int i = 0; i < entryNumber; i++) {
            addEntry(dataMap);
        }
    }
    
}
