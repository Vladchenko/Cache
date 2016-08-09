/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * In charge of an operations made with a RAM cache.
 *
 * @author v.yanchenko
 */
public class HDDCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();

    private Map<Object, Object> mapFiles;
//    private Map<Object, Integer> mapFrequency;
    private Object keyLastAccessed;
    private int filesNumber = 0;
//    private int cacheSize = 0;

    HDDCache() {
        switch (repository.getCacheKind()) {
            case LFU: {
                mapFiles = new HashMap();
                break;
            }
            case LRU: {
                mapFiles = new LinkedHashMap();
                break;
            }
            case MRU: {
                mapFiles = new HashMap();
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
            // And if not, makу ше
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
        mapFiles.clear();
//        mapFrequency.clear();
        filesNumber = 0;
    }

    // Uploading file to RAM.
    @Override
    public Object getObject(Object key) throws IOException,
            FileNotFoundException, ClassNotFoundException {
        Object obj = null;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serializing object
        fos = new FileInputStream((String) mapFiles.get(key));
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
                    obj = mapFiles.get(key);
                    mapFiles.remove(key);
                    mapFiles.put(key, obj);
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
        return obj;
    }

    // Saves file to disk.
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
        ous.writeObject(obj);
        ous.flush();
        ous.close();
        fos.flush();
        fos.close();
//        File file = new File(fullFileName);
//        cacheSize += file.length();
        filesNumber++;
//        mapFrequency.put(key, 1);
        mapFiles.put(key, fullFileName);
        keyLastAccessed = key;
    }

    @Override
    public void removeObject(Object key) throws NotPresentException {
        File file = new File(Repository.FILES_FOLDER
                + Repository.FILE_PREFIX + key + Repository.FILE_EXTENTION);
        if (file.exists()) {
            file.delete();
//            mapFrequency.remove(key);
            mapFiles.remove(key);
        } else {
            throw new NotPresentException();
        }
    }

    @Override
    public int getSize() {
        return filesNumber;
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
                return mapFiles.entrySet().iterator().next().getKey();
            }
            case MRU: {
                return keyLastAccessed;
            }
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    /**
     * @return the mapFiles
     */
    public Map<Object, Object> getMapFiles() {
        return mapFiles;
    }

    /**
     * @param mapFiles the mapFiles to set
     */
    public void setMapFiles(Map<Object, Object> mapFiles) {
        this.mapFiles = mapFiles;
    }

//    /**
//     * @return the mapFrequency
//     */
//    public Map<Object, Integer> getMapFrequency() {
//        return mapFrequency;
//    }
//
//    /**
//     * @param mapFrequency the mapFrequency to set
//     */
//    public void setMapFrequency(Map<Object, Integer> mapFrequency) {
//        this.mapFrequency = mapFrequency;
//    }

    /**
     * @return the keyLastAccessed
     */
    public Object getKeyLastAccessed() {
        return keyLastAccessed;
    }

    /**
     * @param keyLastAccessed the keyLastAccessed to set
     */
    public void setKeyLastAccessed(Object keyLastAccessed) {
        this.keyLastAccessed = keyLastAccessed;
    }

    /**
     * @param size the filesNumber to set
     */
    public void setSize(int size) {
        this.filesNumber = size;
    }
    //</editor-fold>
}
