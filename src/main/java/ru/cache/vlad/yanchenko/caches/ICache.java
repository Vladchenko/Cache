/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.IOException;
import java.util.Map;

/**
 * Contract for a cache instance to implement.
 * 
 * @author v.yanchenko
 */
public interface ICache {

    /** TODO */
    Map<Object, Object> getCacheEntries();

    /** Clearing a cache of all the entries. */
    void clearCache();

    /** Adding an entry to a cache. */
    void addCacheEntry(@NonNull Object key, @NonNull Object obj) throws IOException;
    
    /** Getting the least used entry to make an eviction. */
    Object getLeastUsed(@NonNull Repository.cacheKindEnum cacheKind);
    
    /** Getting requested object by key. */
    Object getCacheEntry(@NonNull Object key) throws IOException, ClassNotFoundException;

    /** Checking if a specific object exists. */
    boolean hasCacheEntry(@NonNull Object key);

    /** Get number of cache hits */
    int getCacheHits();

    /** Sets number of cache hits */
    void setCacheHits(int hits);

    /** Get number of cache misses */
    int getCacheMisses();

    /** Sets number of cache misses */
    void setCacheMisses(int hits);

    /**
     * TODO
     * @return
     */
    int getEntriesNumber();

    /**
     * TODO
     * @param entriesNumber
     */
    void setEntriesNumber(int entriesNumber);

    /** Reset some cache statistics. */
    void resetCacheStatistics();
    
    /** Removing object, when needed. */
    void removeCacheEntry(@NonNull Object key) throws NotPresentException;

    /**
     * TODO
     * @return
     */
    Object getKeyLastAccessed();

    /**
     * TODO
     * @param keyLastAccessed
     */
    void setKeyLastAccessed(@NonNull Object keyLastAccessed);

    /** Retrieve size of cache (number of entries in it) */
    int getSize();

}
