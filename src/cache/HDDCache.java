/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

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
 * @author v.yanchenko
 */
public class HDDCache implements Serializable, ICache {

    public static Repository repository = Repository.getInstance();
    Map<String, Object> mapFiles;
    Map<String, Integer> mapFrequency;
    String keyLastAccessed;
    int size = 0;

    public HDDCache() {
        switch (repository.cacheKind) {
            case LFU: {
                mapFiles = new HashMap();
                break;
            }
            case LRU: {
                mapFiles = new HashMap();
                break;
            }
            case LRR: {
                mapFiles = new LinkedHashMap();
                break;
            }
            case MRU: {
                mapFiles = new HashMap();
                break;
            }
        }
        mapFrequency = new LinkedHashMap();
        createFilesFolder();    // Makes a folder, when there is no such
        clearCache();           // Clear a cache before run a caching loop
    }

    private void createFilesFolder() {
//        Path pth = Paths.get(Repository.FILESFOLDER);
        File theDir = new File(Repository.FILESFOLDER);
        // Checking if directory exists
//        if (!Files.exists(pth)) {
        if (!theDir.exists()) {
            // And if not, makу ше
            new File(Repository.FILESFOLDER).mkdir();
        }
    }

    @Override
    public void clearCache() {
        File dir = new File(Repository.FILESFOLDER);
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    // Uploads file to RAM. Checked for correct performance.
    @Override
    public Object getObject(String key) throws IOException,
            FileNotFoundException, ClassNotFoundException {
        Object obj = null;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serialize object
//        try {
        fos = new FileInputStream((String) mapFiles.get(key));
//        } catch (Exception ex) {
//            // No such file, means HDD cache has no such entry.
//            return null;
//        }
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
            // Increasing a call count for this entry.
            mapFrequency.put(key, mapFrequency.get(key) + 1);
            keyLastAccessed = key;
            switch (repository.cacheKind) {
                case LFU: {
                    break;
                }
                case LRU: {
                    break;
                }
                case LRR: {
                    return null;
//                    break;
                }
                case MRU: {
                    keyLastAccessed = key;
                    return keyLastAccessed;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HDDCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    // Saves file to disk. Checked for correct performance.
    @Override
    public void addObject(String key, Object obj) throws IOException,
            FileNotFoundException {
        String fullFileName = Repository.FILESFOLDER
                + Repository.FILEPREFIX + key + Repository.FILEEXT;
        FileOutputStream fos = null;
        ObjectOutputStream ous = null;
        // Deserialize object
        fos = new FileOutputStream(fullFileName);
        ous = new ObjectOutputStream(fos);
        ous.writeObject(obj);
        ous.flush();
        ous.close();
        fos.flush();
        fos.close();
        size++;
        mapFrequency.put(key, 1);
        mapFiles.put(key, fullFileName);
        keyLastAccessed = key;
    }

    @Override
    public void removeObject(String key) throws NotPresentException {
        File file = new File(Repository.FILESFOLDER
                + Repository.FILEPREFIX + key + Repository.FILEEXT);
        if (file.exists()) {
            file.delete();
            mapFrequency.remove(key);
            mapFiles.remove(key);
        } else {
            throw new NotPresentException();
        }
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasObject(String key) {
        File file = new File(Repository.FILESFOLDER
                + Repository.FILEPREFIX + key + Repository.FILEEXT);
        return file.exists();
    }

    @Override
    public String getLeastUsed(Repository.cacheKindEnum cacheKind) {
//        return mapFrequency.lastKey();
        switch (cacheKind) {
            case LFU: {
                break;
            }
            case LRU: {
                break;
            }
            case LRR: {
                /**
                 * Getting the last key from a map of objects, i.e. the first 
                 * downloaded object.
                 */
                String theLastKey = new ArrayList<>(
                        mapFiles.keySet()).get(mapFiles.size() - 1);
                return theLastKey;
            }
            case MRU: {
                return keyLastAccessed;
            }
            // mapFiles.entrySet().iterator().next().getKey();
        }
        return null;
    }

}
