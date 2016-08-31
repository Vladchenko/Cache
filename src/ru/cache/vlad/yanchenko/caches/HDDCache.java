/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.caches;

import ru.cache.vlad.yanchenko.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * In charge of an operations made with a RAM cache.
 *
 * @author v.yanchenko
 */
public class HDDCache extends AbstractCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();

    public HDDCache() {
        switch (repository.getCacheKind()) {
            case LFU: {
                mapEntries = new HashMap();
                break;
            }
            case LRU: {
                mapEntries = new LinkedHashMap();
                break;
            }
            case MRU: {
                mapEntries = new HashMap();
                break;
            }
        }
//        mapFrequency = new LinkedHashMap();
        createFilesFolder();    // Makes a folder, when there is no such
        clearCache();           // Clear a cache before run a caching loop
    }

    /**
     * Creating a folder (in case its absent) for a files that constitute an HDD
     * cache.
     */
    private void createFilesFolder() {
//        Path pth = Paths.get(Repository.FILES_FOLDER);
        File theDir = new File(Repository.FILES_FOLDER);
        // Checking if directory exists
//        if (!Files.exists(pth)) {
        if (!theDir.exists()) {
            // And if not, make it
            new File(Repository.FILES_FOLDER).mkdir();
        }
    }

    @Override
    public void clearCache() {
        File dir = new File(Repository.FILES_FOLDER);
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        mapEntries.clear();
//        mapFrequency.clear();
        size = 0;
    }

    // Uploading file to RAM.
    @Override
    public Object getObject(Object key) throws IOException,
            FileNotFoundException, ClassNotFoundException {
        Object obj = null;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serializing object
        fos = new FileInputStream((String) mapEntries.get(key));
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
            // Increasing a call count for this entry.
//            mapFrequency.put(key, mapFrequency.get(key) + 1);
            keyLastAccessed = key;
            switch (repository.getCacheKind()) {
                case LFU: {
                    break;
                }
                case LRU: {
                    obj = mapEntries.get(key);
                    mapEntries.remove(key);
                    mapEntries.put(key, obj);
                    break;
                }
                case MRU: {
                    keyLastAccessed = key;
                    return keyLastAccessed;
                }
            }
        } catch (ClassNotFoundException ex) {
            repository.getLogger().info("Class not found !");
        }
        finally {
            if (ous != null) {
                try {
                    ous.close();
                } catch (Exception ex) {
                    repository.getLogger().info("HDDCache object read stream failed to close !");
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    repository.getLogger().info("HDDCache file read stream failed to close !");
                }
            }
        }
        return obj;
    }

    // Saving file to disk.
    @Override
    public void addObject(Object key, Object obj) throws IOException,
            FileNotFoundException {
        String fullFileName = Repository.FILES_FOLDER
                + Repository.FILE_PREFIX + key + Repository.FILE_EXTENTION;
        FileOutputStream fos = null;
        ObjectOutputStream ous = null;
        // Deserializing object
        fos = new FileOutputStream(fullFileName);
        ous = new ObjectOutputStream(fos);
        try {
            ous.writeObject(obj);
        } catch (IOException ex) {
            repository.getLogger().info("HDD cache entry addition is failed. " +
                    "Some disk trouble. Cache integrity is broken.");
        }
        finally {
            if (ous != null) {
                try {
                    ous.close();
                } catch (Exception ex) {
                    repository.getLogger().info("HDDCache object write stream failed to close !");
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    repository.getLogger().info("HDDCache file write stream failed to close !");
                }
            }
        }
//        File file = new File(fullFileName);
//        cacheSize += file.length();
        size++;
//        mapFrequency.put(key, 1);
        mapEntries.put(key, fullFileName);
        keyLastAccessed = key;
    }

    @Override
    public void removeObject(Object key) throws NotPresentException {
        File file = new File(Repository.FILES_FOLDER
                + Repository.FILE_PREFIX + key + Repository.FILE_EXTENTION);
        if (file.exists()) {
            file.delete();
//            mapFrequency.remove(key);
            mapEntries.remove(key);
        } else {
            throw new NotPresentException();
        }
    }

    @Override
    public boolean hasObject(Object key) {
        File file = new File(Repository.FILES_FOLDER
                + Repository.FILE_PREFIX + key + Repository.FILE_EXTENTION);
        return file.exists();
    }

    @Override
    public Object getLeastUsed(Repository.cacheKindEnum cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU: {
                break;
            }
            case LRU: {
                /**
                 * Getting the first key from a map of objects, since first is
                 * the one that was used least recently.
                 */
                return mapEntries.entrySet().iterator().next().getKey();
            }
            case MRU: {
                return keyLastAccessed;
            }
        }
        return null;
    }

}