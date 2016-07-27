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
public class RAMCache implements ICache {
    
    Map<String,Object> objects = new HashMap();

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
    }

    @Override
    public void removeObject(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCacheSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
