/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author v.yanchenko
 */
public class CacheProcessor {

    RAMCache ramCache;
    HDDCache hddCache;
    Repository repository;
    CacheFeeder cacheFeeder;

    // Number of times cache process is going to be performed
    int number = 100;

    public CacheProcessor(Repository repository) {
        ramCache = new RAMCache();
        hddCache = new HDDCache();
        this.repository = repository;
        cacheFeeder = new CacheFeeder();
    }

    // Retrieving an object from a cache
    private Object processRequest(String key) {
        /**
         * Initially, ram cache (RC) and disk cache (DC) are empty. CPU receives
         * a command to get data. Algorythm: 1. CPU checks if an RC has this
         * data, i.e. object (obj) 1.1 If so, retrieves it and does (1). 1.2 If
         * not, checks if it is present in a DC. 1.2.1 If present, retrieves it
         * to CPU and does (1). 1.2.2 If not present, addresses to some memory,
         * gets an (obj) from it, checks if there is a room in RC for it and
         * 1.2.3 if there is, add obj to RC and does (1). 1.2.4 if there is no,
         * checks if there is a room in DC for it. 1.2.4.1 If there is one,
         * passes other obj, defined by picked algorithm to DC, does (1),
         * 1.2.4.2 and writes an obj to RC. 1.2.4.3 If there is no, removes some
         * _obj from DC, using a picked removal algorithm, moves one from an RC
         * to DC, using a picked removal algorithm, and does (1). 1.2.5 Obj from
         * DC might go to RC, if algorythm tells so.
         *
         * (1) - Some manipulation with a data that goes along with an
         * algorythm.
         */
        Map.Entry<String, Integer> entry;
        Object obj = null;

        System.out.println("Requested key=" + key);
        printCaches();

        if (ramCache.hasObject(key)) {
//            if (repository.getCacheKind().equals(key))
            // Increasing a retrieval count for this object
//            ramCache.mapFrequency.put(key, ramCache.mapFrequency.get(key) + 1);
            System.out.println("RAM cache hit, key=" + key + "\n");
            return ramCache.getObject(key);
        } else {

            // Trying to retrieve an entry from an HDD cache
            try {
                obj = hddCache.getObject(key);
                System.out.println("HDD cache hit\n");
            } catch (NullPointerException ex) {
                    
            } catch (IOException ex) {
//                System.out.println("Cannot retrieve an entry from an HDD cache");
//                Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
//                System.out.println("Cannot retrieve an entry from an HDD cache");
//                Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (obj == null) {
                System.out.print("No hit to any cache, ");
                // Try adding a newly downloaded object to cache.
                if (ramCache.getSize() < repository.getLevel1CacheSize()) {
                    // Adding a newly downloaded object to a RAM cache.
                    ramCache.addObject(key, cacheFeeder.feed(key));
                    System.out.println("object with key=" + key + " is added to RAM cache.\n");
                } else {    // RAM cache is full, it needs an extrusion 
                    /*
                     * Find the least used object in RAM cache and move it to HDD 
                     * cache and if a HDD cache is full, remove the least used 
                     * one. Then write to RAM cache a new entry.
                     */
                    System.out.print("\nRAM cache is full, performing extrusion.");
                    if (hddCache.size < repository.getLevel2CacheSize()) {
                        // Looking for least used entry in a RAM cache
                        entry = ramCache.frequency.entrySet().iterator().next();
                        // Moving a least used RAM object to a HDD cache.
//                        hddCache.objects.put(key, ramCache.objects.get(entry.getKey()));
                        try {
                            hddCache.addObject(entry.getKey(), ramCache.getObject(entry.getKey()));
                            System.out.println(" An object with key=" + entry.getKey() + " is moved to HDD cache.\n");
                            // Removing such object from a cache
                            ramCache.removeObject(entry.getKey());
                            // Adding a newly downloaded object to a RAM cache.
                            ramCache.addObject(key, cacheFeeder.feed(key));
                        } catch (IOException ex) {
                            System.out.println();
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        
                        /**
                         * When all the caches are full and new entry is 
                         * downloaded, remove least used entry from an HDD cache
                         * , then move least used RAM cache entry to an HDD 
                         * cache and write a new entry to RAM cache.
                         */
                        
                        // Getting the least used entry in an HDD cache.
                        entry = hddCache.mapFrequency.entrySet().iterator().next();
                        try {
                            // Removing this entry
                            hddCache.removeObject(entry.getKey());
                        } catch (NotPresentException ex) {
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Getting the least used entry in a RAM cache.
                        entry = ramCache.frequency.entrySet().iterator().next();
//                        System.out.println("Entry is |" + entry.getKey() + "|");
//                        System.out.println(key);
                        try {
                            // Moving a least used RAM object to a HDD cache.
                            hddCache.addObject(entry.getKey(), entry);
                        } catch (IOException ex) {
                            System.out.println("Cannot move to HDD cache !");
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Removing a least used entry from a RAM cache.
                        ramCache.removeObject(entry.getKey());

                        // Adding a newly downloaded object to a RAM cache.
                        ramCache.addObject(key, cacheFeeder.feed(key));
                    }
                }
            }
        }
        return new Object();
    }

    private void printCaches() {
        System.out.print("--- RAM cache: ");
        for (Map.Entry<String, Object> entrySet : ramCache.objects.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();
            System.out.print("key=" + key + "(" + (int) ramCache.frequency.get(key) + "), ");
        }
        System.out.println("");
        System.out.print("--- HDD cache: ");
        for (Map.Entry<String, Object> entrySet : hddCache.mapFiles.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();
            System.out.print("file=" + ((String)value).split("[\\\\]+")[1] + "(" + (int) hddCache.mapFrequency.get(key) + "), ");
        }
        System.out.println("");
    }

    public void performCachingProcess() {
        Object obj;
        System.out.println("\n--- Data retrieval/caching loop begun ---");
        for (int i = 0; i < number; i++) {
            obj = processRequest(cacheFeeder.dummyAddress());
        }
    }

}
