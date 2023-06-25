package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Cache abstract class for real caches to derive from.
 *
 * Created by v.yanchenko on 31.08.2016.
 */
public abstract class AbstractCache implements ICache {

    // Current size of a cache.
    protected int size = 0;
    // Key of an object that was accessed last.
    protected Object lastAccessedEntryKey;
    // Reference to some object needed in a logic of an app.
    protected Object tempObject;
    // Map that holds the keys to an entries that constitute a cache.
    protected Map<Object, Object> cacheEntries;

    public Object getTempObject() {
        return tempObject;
    }

    public void setTempObject(@NonNull Object tempObject) {
        this.tempObject = tempObject;
    }

    public Object getKeyLastAccessed() {
        return lastAccessedEntryKey;
    }

    public void setKeyLastAccessed(@NonNull Object keyLastAccessed) {
        lastAccessedEntryKey = keyLastAccessed;
    }

    public int getSize() {
        return cacheEntries.size();
    }

    public void setSize(int size) {
        this.size = size;
    }
}
