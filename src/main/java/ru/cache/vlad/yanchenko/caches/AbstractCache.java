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
    protected int mSize = 0;
    // Key of an object that was accessed last.
    protected Object mLastAccessedEntryKey;
    // Reference to some object needed in a logic of an app.
    protected Object mTempObject;
    // Map that holds the keys to an entries that constitute a cache.
    protected Map<Object, Object> mMapEntries;

    public Map<Object, Object> getMapEntries() {
        return mMapEntries;
    }

//    protected void setMapEntries(Map<Object, Object> mapEntries) {
//        this.mapEntries = mapEntries;
//    }

    public Object getTempObject() {
        return mTempObject;
    }

    public void setTempObject(@NonNull Object tempObject) {
        this.mTempObject = tempObject;
    }

    public Object getKeyLastAccessed() {
        return mLastAccessedEntryKey;
    }

    public void setKeyLastAccessed(@NonNull Object keyLastAccessed) {
        this.mLastAccessedEntryKey = keyLastAccessed;
    }

    public int getSize() {
        return mMapEntries.size();
    }

    public void setSize(int size) {
        this.mSize = size;
    }

}
