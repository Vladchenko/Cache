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
    public void addObject(Object key, Object obj) throws IOException, 
            FileNotFoundException;
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind);
    public Object getObject(Object key)  throws IOException,
            FileNotFoundException, ClassNotFoundException;
    public int getSize();
    public boolean hasObject(Object key);
    public void removeObject(Object key) throws NotPresentException;
    
}
