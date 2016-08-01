/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author v.yanchenko
 */
public interface ICache {
    public void clearCache();
    public boolean hasObject(String key);
    public Object getObject(String key)  throws IOException,
            FileNotFoundException, ClassNotFoundException;
    public void addObject(String guid, Object obj) throws IOException, 
            FileNotFoundException;
    public void removeObject(String key) throws NotPresentException;
    public int getSize();
}
