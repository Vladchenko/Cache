/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In charge of an operations made with a RAM cache.
 *
 * @author v.yanchenko
 */
public class HDDCache extends AbstractCache implements Serializable, ICache {

    private final Logger mLogger;
    private final Repository mRepository;

    /**
     * Create an instance of this class
     *
     * @param logger     to log the events
     * @param repository that holds a settings for program.
     */
    public HDDCache(@NonNull Logger logger, @NonNull Repository repository) {
        mLogger = logger;
        mRepository = repository;
        switch (repository.getCacheKind()) {
            case LFU, MRU -> mMapEntries = new HashMap<>();
            case LRU -> mMapEntries = new LinkedHashMap<>();
        }
        try {
            createFilesFolder(CacheConstants.FILES_FOLDER);    // Makes a folder, when there is no such
        } catch (DirectoryException e) {
            e.printStackTrace();
        }
        clearCache();           // Clear a cache before run a caching loop
    }

    // Checking if a directory path has no special characters, such as :"\/|?<>
    private boolean isPath(@NonNull String path) {
        Pattern p = Pattern.compile("[:<>|*/?]");
        Matcher m = p.matcher(path);
        if (path.lastIndexOf('\\') != path.length() - 1) {
            path += "\\";
            System.out.println(path);
        }
        return !m.find();
    }

    // Create a folder (in case its absent) for a files that constitute an HDD cache.
    private void createFilesFolder(@NonNull String path) throws DirectoryException {
        File directory = new File(path);
        // Checking if a directory keep the real path on a disk.
        if (!isPath(path)) {
            throw new DirectoryException("\"" + path + "\"" +
                    " is not a valid pathname. Change and rerun an app. Program exits.",
                    mLogger);
        }
        // Checking if directory exists.
        if (!directory.exists()) {
            // And if not, make it.
            new File(path).mkdir();
        }
    }

    @Override
    public void clearCache() {
        File dir = new File(CacheConstants.FILES_FOLDER);
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        mMapEntries.clear();
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
        fos = new FileInputStream((String) mMapEntries.get(key));
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
            // Increasing a call count for this entry.
//            mapFrequency.put(key, mapFrequency.get(key) + 1);
            mLastAccessedEntryKey = key;
            switch (mRepository.getCacheKind()) {
                case LFU -> {
                }
                case LRU -> {
                    obj = mMapEntries.get(key);
                    mMapEntries.remove(key);
                    mMapEntries.put(key, obj);
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
        String fullFileName = CacheConstants.FILES_FOLDER + CacheConstants.FILE_PREFIX + key + CacheConstants.FILE_EXTENSION;
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
        mMapEntries.put(key, fullFileName);
        mLastAccessedEntryKey = key;
    }

    @Override
    public void removeCacheEntry(@NonNull Object key) throws NotPresentException {
        File file = new File(CacheConstants.FILES_FOLDER + CacheConstants.FILE_PREFIX + key + CacheConstants.FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
//            mapFrequency.remove(key);
            mMapEntries.remove(key);
        } else {
            throw new NotPresentException(file.getName(), mLogger);
        }
    }

    @Override
    public boolean hasCacheEntry(@NonNull Object key) {
        File file = new File(
                CacheConstants.FILES_FOLDER
                + CacheConstants.FILE_PREFIX
                + key
                + CacheConstants.FILE_EXTENSION);
        return file.exists();
    }

    @Override
    public Object getLeastUsed(@NonNull Repository.cacheKindEnum cacheKind) {
        switch (cacheKind) {
            case LFU -> {
            }
            case LRU -> {
                // Getting the first key from a map of objects, since first is the one that was used least recently.
                return mMapEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return mLastAccessedEntryKey;
            }
        }
        return null;
    }

}
