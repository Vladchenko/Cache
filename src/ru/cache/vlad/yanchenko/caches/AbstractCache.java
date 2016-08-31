package ru.cache.vlad.yanchenko.caches;

import ru.cache.vlad.yanchenko.Repository;

import java.util.Map;

/**
 * Created by v.yanchenko on 31.08.2016.
 */
public abstract class AbstractCache implements ICache {

    // Current size of a cache.
    protected int size = 0;
    // Key of an object that was accessed last.
    protected Object keyLastAccessed;
    // Reference to some object neede in a logic of an app.
    protected Object tempObject;
    // Map that holds the keys to an entries that constitute a cache.
    protected Map<Object, Object> mapEntries;

    public Map<Object, Object> getMapEntries() {
        return mapEntries;
    }

//    protected void setMapEntries(Map<Object, Object> mapEntries) {
//        this.mapEntries = mapEntries;
//    }

    public Object getTempObject() {
        return tempObject;
    }

    public void setTempObject(Object tempObject) {
        this.tempObject = tempObject;
    }

    public Object getKeyLastAccessed() {
        return keyLastAccessed;
    }

    public void setKeyLastAccessed(Object keyLastAccessed) {
        this.keyLastAccessed = keyLastAccessed;
    }

    public int getSize() {
        return mapEntries.size();
    }

    public void setSize(int size) {
        this.size = size;
    }

}
