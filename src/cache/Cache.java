/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author v.yanchenko
 */
public class Cache {
    
    ProcessArguments prc = new ProcessArguments();
    HDDCache hddcache = new HDDCache();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Cache cache = new Cache();
        cache.prc.processArgs(args);
        try {
            cache.hddcache.addObject("123", args);
        } catch (IOException ex) {
            System.out.println("No way");
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            cache.hddcache.addObject("123", args);
        } catch (IOException ex) {
            System.out.println("No way");
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println(cache.hddcache.getObject("123"));
        } catch (IOException ex) {
            System.out.println("No way 1");
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("No way 2");
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
