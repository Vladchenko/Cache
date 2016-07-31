/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

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

//    Timer tmr = new Timer();
    public CacheProcessor(Repository repository) {
        ramCache = new RAMCache();
        hddCache = new HDDCache();
        this.repository = repository;
        cacheFeeder = new CacheFeeder();
    }

    // Retrieving an object from a cache
    private Object processRequest(String uid) {
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

        System.out.println("Requested key=" + uid);
        printCaches();

        if (ramCache.objects.containsKey(uid)) {
//            if (repository.getCacheKind().equals(uid))
            // Increasing a retrieval count for this object
            ramCache.frequency.put(uid, ramCache.frequency.get(uid) + 1);
            System.out.println("RAM cache hit, key=" + uid + "\n");
            return ramCache.objects.get(uid);
        } else {

            // Trying to retrieve an entry from an HDD cache
            try {
                obj = hddCache.getObject(uid);
            } catch (IOException ex) {
//                System.out.println("Cannot retrieve an entry from an HDD cache");
//                Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
//                System.out.println("Cannot retrieve an entry from an HDD cache");
//                Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (obj != null) {
                // Increasing a retrieval frequency for this object
                hddCache.frequency.put(uid, hddCache.frequency.get(uid) + 1);
                System.out.println("HDD cache hit");
                return obj;

            } else {
                System.out.print("No hit to any cache, ");
                // Try adding a newly downloaded object to cache.
                if (ramCache.objects.size() < repository.getLevel1CacheSize()) {
                    // Adding a newly downloaded object to a RAM cache.
                    ramCache.objects.put(uid, cacheFeeder.feed(uid));
                    // Making a retrieval count for this object to be 1.
                    ramCache.frequency.put(uid, 1);
                    System.out.println("object with key=" + uid + " is added.\n");
                } else {    // RAM cache is full, it needs an extrusion 
                    /*
                     * Find the least used object in RAM cache and move it to HDD 
                     * cache and if a HDD cache is full, remove the least used 
                     * one. Then write to RAM cache a new entry.
                     */
                    System.out.println(" performing extrusion ... \n");
                    if (hddCache.size < repository.getLevel2CacheSize()) {
                        // Looking for least used entry in a RAM cache
                        entry = ramCache.frequency.entrySet().iterator().next();
                        // Moving a least used RAM object to a HDD cache.
//                        hddCache.objects.put(uid, ramCache.objects.get(entry.getKey()));
                        try {
                            hddCache.addObject(entry.getKey(), ramCache.objects.get(entry.getKey()));
                        } catch (IOException ex) {
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Making a retrieval count for this object to be 1.
                        hddCache.frequency.put(uid, 1);
                        ramCache.objects.remove(entry.getKey());
                        ramCache.frequency.remove(entry.getKey());

                        // Adding a newly downloaded object to a RAM cache.
                        ramCache.objects.put(uid, cacheFeeder.feed(uid));
                        // Making a retrieval count for this object to be 1.
                        ramCache.frequency.put(uid, 1);

                    } else {
                        // Remove least used item from an HDD cache
                        entry = hddCache.frequency.entrySet().iterator().next();
                        ramCache.objects.remove(entry.getKey(), ramCache.objects.get(entry.getKey()));

                        // Looking for least used entry in a RAM cache
                        entry = ramCache.frequency.entrySet().iterator().next();
                        try {
                            // Moving a least used RAM object to a HDD cache.
//                        hddCache.objects.put(uid, ramCache.frequency.get(entry.getKey()));
                            hddCache.addObject(uid, entry);
                        } catch (IOException ex) {
                            System.out.println("Cannot move to HDD cache !");
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Making a retrieval count for this object to be 1.
                        hddCache.frequency.put(uid, 1);
                        ramCache.objects.remove(entry.getKey());

                        // Adding a newly downloaded object to a RAM cache.
                        ramCache.objects.put(uid, cacheFeeder.feed(uid));
                        // Making a retrieval count for this object to be 1.
                        ramCache.frequency.put(uid, 1);
                    }
                }
            }
        }
        return new Object();
    }

    /**
     * Method generates a new object, pretending that is was downloaded from
     * outer source.
     */
//    private Object readObject(String uid) {
//        return new Object();
//    }
    private void printCaches() {
        System.out.print("--- RAM cache: ");
        for (Map.Entry<String, Object> entrySet : ramCache.objects.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();
            System.out.print("key=" + key + " (" + (int) ramCache.frequency.get(key) + "), ");
        }
//        if (ramCache.objects.size() > 0) {
            System.out.println("");
//        }
        System.out.print("--- HDD cache: ");
        File dir = new File(Repository.FILESFOLDER);
        for (File file : dir.listFiles()) {
            System.out.print(file + ", ");
        }
//        if (hddCache.size > 0) {
            System.out.println("");
//        }
    }

    public void performCachingProcess() {
        Object obj;
        System.out.println("\n--- Data retrieval/caching loop begun ---");
        for (int i = 0; i < number; i++) {
            obj = processRequest(cacheFeeder.dummyAddress());
        }
    }

}
