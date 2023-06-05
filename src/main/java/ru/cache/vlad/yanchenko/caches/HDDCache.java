/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.utils.FileUtils;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In charge of an operations made with a RAM cache.
 *
 * @author v.yanchenko
 */
public class HDDCache extends AbstractCache implements Serializable, ICache {

    private final Logger mLogger;
    private final Map<String, String> mArguments;

    private int mCacheHits = 0;
    private int mCacheMisses = 0;
    // Number of entries to be present in cache.
    private int mCacheEntriesNumber;

    /**
     * Create an instance of this class
     *
     * @param logger    to log the events
     * @param arguments for define settings of a cache process
     */
    public HDDCache(@NonNull Logger logger, @NonNull Map<String, String> arguments) {
        mLogger = logger;
        mArguments = arguments;
        mCacheEntriesNumber = Integer.parseInt(mArguments.get("l2s"));
        switch (Repository.cacheKindEnum.valueOf(mArguments.get("cachekind").toUpperCase(Locale.ROOT))) {
            case LFU, MRU -> mCacheEntries = new HashMap<>();
            case LRU -> mCacheEntries = new LinkedHashMap<>();
        }
        clearCache();           // Clear a cache before run a caching loop
    }

    @Override
    public Map<Object, Object> getCacheEntries() {
        return mCacheEntries;
    }

    @Override
    public void clearCache() {
        File dir = new File(FileUtils.FILES_FOLDER);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                try {
                    Files.delete(file.toPath());
                    if (mArguments.get("dr") != null) {
                        mLogger.info(file.getName() + " was deleted");
                    }
                } catch (IOException ioex) {
                    mLogger.error(ioex.getMessage());
                }
            }
        }
        mCacheEntries.clear();
//        mapFrequency.clear();
        mSize = 0;
    }

    // Uploading file to RAM.
    @Override
    public Object getCacheEntry(@NonNull Object key) throws IOException, ClassNotFoundException {
        Object obj = null;
        FileInputStream fos;
        ObjectInputStream ous;
        // Serializing object
        fos = new FileInputStream((String) mCacheEntries.get(key));
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
            // Increasing a call count for this entry.
//            mapFrequency.put(key, mapFrequency.get(key) + 1);
            mLastAccessedEntryKey = key;
            switch (Repository.cacheKindEnum.valueOf(mArguments.get("cachekind").toUpperCase(Locale.ROOT))) {
                case LFU -> {
                }
                case LRU -> {
                    obj = mCacheEntries.get(key);
                    mCacheEntries.remove(key);
                    mCacheEntries.put(key, obj);
                }
                case MRU -> {
                    mLastAccessedEntryKey = key;
                    return mLastAccessedEntryKey;
                }
            }
        } catch (ClassNotFoundException ex) {
            mLogger.info("Class not found !");
        } finally {
            if (ous != null) {
                try {
                    ous.close();
                } catch (Exception ex) {
                    mLogger.info("HDDCache object read stream failed to close !");
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    mLogger.info("HDDCache file read stream failed to close !");
                }
            }
        }
        return obj;
    }

    // Saving file to disk.
    @Override
    public void addCacheEntry(@NonNull Object key, @NonNull Object obj) throws IOException {
        String fullFileName = FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION;
        FileOutputStream fos;
        ObjectOutputStream ous;
        // Deserializing object
        fos = new FileOutputStream(fullFileName);
        ous = new ObjectOutputStream(fos);
        try {
            ous.writeObject(obj);
        } catch (IOException ex) {
            mLogger.info("HDD cache entry addition is failed. Some disk trouble. Cache integrity is broken.");
        } finally {
            if (ous != null) {
                try {
                    ous.close();
                } catch (Exception ex) {
                    mLogger.info("HDDCache object write stream failed to close !");
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    mLogger.info("HDDCache file write stream failed to close !");
                }
            }
        }
//        File file = new File(fullFileName);
//        cacheSize += file.length();
        mSize++;
//        mapFrequency.put(key, 1);
        mCacheEntries.put(key, fullFileName);
        mLastAccessedEntryKey = key;
    }

    @Override
    public void removeCacheEntry(@NonNull Object key) throws NotPresentException {
        File file = new File(FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
//            mapFrequency.remove(key);
            mCacheEntries.remove(key);
        } else {
            throw new NotPresentException(file.getName(), mLogger);
        }
    }

    @Override
    public boolean hasCacheEntry(@NonNull Object key) {
        File file = new File(
                FileUtils.FILES_FOLDER
                        + FileUtils.FILE_PREFIX
                        + key
                        + FileUtils.FILE_EXTENSION);
        return file.exists();
    }

    @Override
    public Object getLeastUsed(@NonNull Repository.cacheKindEnum cacheKind) {
        switch (cacheKind) {
            case LFU -> {
            }
            case LRU -> {
                // Getting the first key from a map of objects, since first is the one that was used least recently.
                return mCacheEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return mLastAccessedEntryKey;
            }
            default -> {
            }
        }
        return null;
    }

    /**
     * @return the hitsHDDCache
     */
    @Override
    public int getCacheHits() {
        return mCacheHits;
    }

    /**
     * @param hitsHDDCache the hitsHDDCache to set
     */
    @Override
    public void setCacheHits(int hitsHDDCache) {
        mCacheHits = hitsHDDCache;
    }

    /**
     * @return the missesHDDCache
     */
    @Override
    public int getCacheMisses() {
        return mCacheMisses;
    }

    /**
     * @param missesHDDCache the missesHDDCache to set
     */
    @Override
    public void setCacheMisses(int missesHDDCache) {
        mCacheMisses = missesHDDCache;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public int getEntriesNumber() {
        return mCacheEntriesNumber;
    }

    @Override
    public void setEntriesNumber(int entriesNumber) {
        mCacheEntriesNumber = entriesNumber;
    }

    @Override
    public void resetCacheStatistics() {
        mCacheMisses = 0;
        mCacheHits = 0;
    }

    /**
     * TODO
     *
     * @param hddCacheEntriesNumber
     */
    public void setHDDCacheEntriesNumber(int hddCacheEntriesNumber) {
        mCacheEntriesNumber = hddCacheEntriesNumber;
    }
}
