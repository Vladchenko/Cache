/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author v.yanchenko
 */
public interface ICache {
    public void clearCache();
    public void addObject(String key, Object obj) throws IOException, 
            FileNotFoundException;
    public String getLeastUsed(Repository.cacheKindEnum cacheKind);
    public Object getObject(String key)  throws IOException,
            FileNotFoundException, ClassNotFoundException;
    public int getSize();
    public boolean hasObject(String key);
    public void removeObject(String key) throws NotPresentException;
    
}
