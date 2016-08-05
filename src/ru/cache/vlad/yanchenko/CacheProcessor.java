/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author v.yanchenko
 */
public class CacheProcessor {

    int hitsRAMCache = 0;
    int missesRAMCache = 0;
    int hitsHDDCache = 0;
    int missesHDDCache = 0;

    RAMCache ramCache;
    HDDCache hddCache;
    Repository repository;
    CacheFeeder cacheFeeder;

    // Number of times cache process is going to be performed.
    int number = 100;

    public CacheProcessor(Repository repository) {
        ramCache = new RAMCache();
        hddCache = new HDDCache();
        this.repository = repository;
        cacheFeeder = new CacheFeeder(20);
    }

    /**
     * Retrieving an entry from a cache or mock source, to allegedly pass it to
     * a requestor.
     */
    public Object processRequest(String key) {

//        Map.Entry<String, Integer> entry;
        // Data that is going to be retrieved on a CPU alleged request.
        Object obj = null;

        System.out.println("Requested key=" + key);
        printCaches();

        // If RAM cache has a requested entry,
        if (ramCache.hasObject(key)) {
            System.out.println("RAM cache hit, key=" + key + "\n");
            // return it to CPU.
            hitsRAMCache++;
            return ramCache.getObject(key);
        } else {
            // RAM cache miss, 
            missesRAMCache++;
            try {
                // trying to retrieve an entry from an HDD cache.
                obj = hddCache.getObject(key);
                hitsHDDCache++;
                System.out.println("HDD cache hit, key=" + key + "\n");
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ex) {
            }
            // When both caches miss,
            if (obj == null) {
                missesHDDCache++;
                System.out.print("Cache miss, ");
                // if RAM cache is not full,
                if (ramCache.getSize() < repository.getLevel1CacheSize()) {
                    // downloading a requested data from a mock source,
                    obj = cacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    ramCache.addObject(key, obj);
                    System.out.println("entry with key=" + key + " is added to a RAM cache.\n");
                    return obj;
                } else {    // RAM cache is full, it needs an extrusion.
                    /*
                     * Find the least used entry in a RAM cache and move it to 
                     * an HDD cache and if HDD cache is full, remove the least 
                     * used one. Then write to a RAM cache a new entry.
                     */
                    System.out.print("\nRAM cache is full, performing an extrusion.");
                    if (hddCache.size < repository.getLevel2CacheSize()) {
                        // Getting the least used entry in a RAM cache.
                        String key_ = ramCache.getLeastUsed(repository.cacheKind);
                        // Moving a least used RAM entry to an HDD cache.
                        try {
                            hddCache.addObject(key_, ramCache.getObject(key_));
                            hddCache.mapFrequency.put(key_, 0);
                            System.out.println(" An entry with key=" + key_ + " is moved to an HDD cache.\n");
                            // Removing such entry from a RAM cache.
                            ramCache.removeObject(key_);
                            // Downloading a requested data from a mock source.
                            obj = cacheFeeder.deliverObject(key);
                            // Adding a newly downloaded entry to a RAM cache.
                            ramCache.addObject(key, obj);
                            return obj;
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
                        System.out.print("\nHDD cache is full, removing a least used entry.");
                        // Getting the least used entry in an HDD cache 
                        String key_ = hddCache.getLeastUsed(repository.cacheKind);
                        try {
                            // and removing this entry.
                            hddCache.removeObject(key_);
                            System.out.println("Entry with key=" + key_ + " is removed from an HDD cache. ");
                        } catch (NotPresentException ex) {
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = ramCache.getLeastUsed(repository.cacheKind);
                        try {
                            // Moving a least used RAM entry to a HDD cache.
                            hddCache.addObject(key_, ramCache.getObject(key_));
                            hddCache.mapFrequency.put(key_, 0);
                            System.out.println("Least used entry in RAM cache with key="
                                    + key_ + " is moved to an HDD cache. ");
                        } catch (IOException ex) {
                            System.out.println("Cannot move to HDD cache !");
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Removing a least used entry from a RAM cache.
                        ramCache.removeObject(key_);
                        System.out.println("Least used in RAM cache entry with key="
                                + key_ + " is removed.");
                        // Adding a newly downloaded entry to a RAM cache.
                        ramCache.addObject(key, cacheFeeder.deliverObject(key));
                        System.out.println("New entry with key="
                                + key + " is added to a RAM cache.\n");
                    }
                }
            }
        }
        recache();
        // Retrieving a requested entry to a CPU.
        return obj;
    }

    private void recache() {
        switch (repository.getCacheKind()) {
            case LFU: {
                break;
            }
            case LRR: {
                break;
            }
            case LRU: {
                break;
            }
            case MRU: {
                // If there was an HDDCache hit, then move this entry to a RAMCache.
                break;
            } 
        }

    }

    private void printCaches() {

        if (ramCache.mapObjects instanceof LinkedHashMap) {

        }

        System.out.print("--- RAM cache: ");
        if (ramCache.mapObjects != null) {
            for (Map.Entry<String, Object> entrySet : ramCache.mapObjects.entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                System.out.print("key=" + key + "(" + (int) ramCache.frequency.get(key) + "), ");
            }
            System.out.println("");
            System.out.print("--- HDD cache: ");
            if (hddCache.mapFiles instanceof LinkedHashMap) {

            } else {
                for (Map.Entry<String, Object> entrySet : hddCache.mapFiles.entrySet()) {
                    Object key = entrySet.getKey();
                    Object value = entrySet.getValue();
                    System.out.print("file=" + ((String) value).split("[\\\\]+")[1] + "(" + (int) hddCache.mapFrequency.get(key) + "), ");
                }
            }
            System.out.println("");
        }
    }

    public void performCachingProcess() {
        Object obj;
        System.out.println("\n\n<<<--- Data retrieval/caching loop begun --->>>\n");
        for (int i = 0; i < number; i++) {
            obj = processRequest(cacheFeeder.requestObject());
        }
    }

    public void resetCachingInfo() {
        hitsRAMCache = 0;
        missesRAMCache = 0;
        hitsHDDCache = 0;
        missesHDDCache = 0;
    }

}
