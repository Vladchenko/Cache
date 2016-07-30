/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author v.yanchenko
 */
public class RAMCache implements ICache {
    
    Map<String,Object> objects;
    Map<String,Integer> frequency;
    
    public RAMCache() {
        objects = new HashMap();
        frequency = new TreeMap();
    }

    @Override
    public void clearCache() {
        objects.clear();
    }

    @Override
    public Object getObject(String key) {
        return objects.get(key);
    }

    @Override
    public void addObject(String guid, Object obj) {
        objects.put(guid, obj);
        frequency.put(guid, frequency.get(obj) + 1);
    }

    @Override
    public void removeObject(String key) {
        objects.remove(key);
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
    public int getCacheSize() {
        return objects.size();
    }

}
