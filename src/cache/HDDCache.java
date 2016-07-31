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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author v.yanchenko
 */
public class HDDCache implements Serializable, ICache {

    Map<String, Object> objects;
    Map<String, Integer> frequency;
    int size = 0;
//    public static Repository oRepository = Repository.getInstance();

    public HDDCache() {
        objects = new HashMap();
        frequency = new TreeMap();
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
    public Object getObject(String uid) throws IOException,
            FileNotFoundException, ClassNotFoundException {
        Object obj = null;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serialize object
        fos = new FileInputStream(Repository.FILESFOLDER
                + Repository.FILEPREFIX + uid + Repository.FILEEXT);
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HDDCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    // Saves file to disk. Checked for correct performance.
    @Override
    public void addObject(String uid, Object obj) throws IOException,
            FileNotFoundException {
        FileOutputStream fos = null;
        ObjectOutputStream ous = null;
        if (ous != null) {
            objects.put(uid, obj);
        }
        // Deserialize object
        fos = new FileOutputStream(Repository.FILESFOLDER
                + Repository.FILEPREFIX + uid + Repository.FILEEXT);
        ous = new ObjectOutputStream(fos);
        ous.writeObject(obj);
        ous.flush();
        ous.close();
        fos.flush();
        fos.close();
        size++;
    }

    @Override
    public void removeObject(String key) throws NotPresentException {
        if (objects.containsKey(key)) {
            objects.remove(key);
        } else {
            throw new NotPresentException();
        }
    }

    @Override
    public int getCacheSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
