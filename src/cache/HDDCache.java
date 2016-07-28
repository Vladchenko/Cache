/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author v.yanchenko
 */
public class HDDCache implements Serializable, ICache {

    Map<String, Object> objects = new HashMap();
    Map<String, Integer> frequency = new HashMap();
//    public static Repository oRepository = Repository.getInstance();

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getObject(String uid) throws IOException, FileNotFoundException {
        Object obj;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serialize object
        fos = new FileInputStream(Repository.FILEPREFIX + uid);
        ous = new ObjectInputStream(fos);
        try {
            obj = ous.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HDDCache.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if (ous != null) {
//            objects.put(uid, obj);
//        }
        return ous;
    }

    @Override
    public void addObject(String uid, Object obj) throws IOException, FileNotFoundException {
        FileOutputStream fos = null;
        ObjectOutputStream ous = null;
        if (ous != null) {
            objects.put(uid, obj);
        }
        // Deserialize object
        fos = new FileOutputStream(Repository.FILEPREFIX + uid);
        ous = new ObjectOutputStream(fos);
        ous.writeObject(obj);
        ous.flush();
        ous.close();
        fos.flush();
        fos.close();
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
