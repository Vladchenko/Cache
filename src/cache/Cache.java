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
    
    private ProcessArguments prc;
    private CacheProcessor cacheProcessor;
    
    public Cache() {
        prc = new ProcessArguments();
        cacheProcessor = new CacheProcessor();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Cache cache = new Cache();
        cache.prc.processArgs(args);
        cache.cacheProcessor.processRequest(null);
    }
    
}
