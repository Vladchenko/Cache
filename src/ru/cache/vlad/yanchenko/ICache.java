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
 * Since we have two caches, both of them ougth to have the same methods of 
 * operating, thus they should have the same contract, i.e. interface.
 * 
 * @author v.yanchenko
 */
public interface ICache {
    
    // Clearing a cache of all the entries.
    public void clearCache();
    
    // Adding an object to a cache.
    public void addObject(Object key, Object obj) throws IOException, 
            FileNotFoundException;
    
    // Getting a least used object to make an eviction.
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind);
    
    // Getting an requested object by key.
    public Object getObject(Object key)  throws IOException,
            FileNotFoundException, ClassNotFoundException;
    
    // Retrieving a size of a cache.
    public int getSize();
    
    // Checking if a specific object exists.
    public boolean hasObject(Object key);
    
    // Removing object, when needed.
    public void removeObject(Object key) throws NotPresentException;
    
}
