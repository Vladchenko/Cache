/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.FileNotFoundException;
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

//    boolean HDDCacheHit = false;
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

//        System.out.println("Requested key=" + key);
        repository.logger.info("Requested key=" + key);
        printCaches();

        // If RAM cache has a requested entry,
        if (ramCache.hasObject(key)) {
//            System.out.println("RAM cache hit, key=" + key + "\n");
            repository.logger.info("RAM cache hit, key=" + key);
            repository.logger.info("");
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
//                System.out.println("HDD cache hit, key=" + key + "\n");
                repository.logger.info("HDD cache hit, key=" + key);
                repository.logger.info("");
//                HDDCacheHit = true;
                recache(key);
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ex) {
            }
            // When both caches miss,
            if (obj == null) {
                missesHDDCache++;
//                System.out.print("Cache miss, ");
//                repository.logger.info("Cache miss, ");
                // if RAM cache is not full,
                if (ramCache.getSize() < repository.getLevel1CacheSize()) {
                    // downloading a requested data from a mock source,
                    obj = cacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    ramCache.addObject(key, obj);
//                    System.out.println("entry with key=" + key + " is added to a RAM cache.\n");
                    repository.logger.info("Cache miss, entry with key=" + key + " is added to a RAM cache.");
                    repository.logger.info("");
                    return obj;
                } else {    // RAM cache is full, it needs an eviction.
                    /*
                     * Find the least used entry in a RAM cache and move it to 
                     * an HDD cache and if HDD cache is full, remove the least 
                     * used one. Then write to a RAM cache a new entry.
                     */
//                    System.out.print("\nRAM cache is full, performing an eviction.");
//                    repository.logger.info("");
                    repository.logger.info("Cache miss, RAM cache is full, performing an eviction.");
                    if (hddCache.size < repository.getLevel2CacheSize()) {
                        // Getting the least used entry in a RAM cache.
                        Object key_ = ramCache.getLeastUsed(repository.cacheKind);
                        // Moving a least used RAM entry to an HDD cache.
                        try {
                            hddCache.addObject(key_, ramCache.getObject(key_));
                            hddCache.mapFrequency.put(key_, 0);
//                            System.out.println(" An entry with key=" + key_ + " is moved to an HDD cache.\n");
                            repository.logger.info("An entry with key=" + key_ + " is moved to an HDD cache.");
//                            Cache miss, entry with key=567838060 is added to a RAM cache.
                            repository.logger.info("Entry with key=" + key + " is added to a RAM cache.");
                            repository.logger.info("");
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
                         * downloaded, remove least used entry from an HDD
                         * cache, then move least used RAM cache entry to an HDD
                         * cache and write a new entry to RAM cache.
                         */
//                        System.out.print("\nHDD cache is full, removing a least used entry.");
                        repository.logger.info("");
                        repository.logger.info("HDD cache is full, removing a least used entry.");
                        // Getting the least used entry in an HDD cache 
                        Object key_ = hddCache.getLeastUsed(repository.cacheKind);
                        try {
                            // and removing this entry.
                            hddCache.removeObject(key_);
//                            System.out.println("Entry with key=" + key_ + " is removed from an HDD cache. ");
                            repository.logger.info("Entry with key=" + key_ + " is removed from an HDD cache. ");
                        } catch (NotPresentException ex) {
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = ramCache.getLeastUsed(repository.cacheKind);
                        try {
                            // Moving a least used RAM entry to a HDD cache.
                            hddCache.addObject(key_, ramCache.getObject(key_));
                            hddCache.mapFrequency.put(key_, 0);
//                            System.out.println("Least used entry in RAM cache with key="
//                                    + key_ + " is moved to an HDD cache. ");
                            repository.logger.info("Least used entry in RAM cache with key="
                                    + key_ + " is moved to an HDD cache. ");
                        } catch (IOException ex) {
//                            System.out.println("Cannot move to HDD cache !");
                            repository.logger.info("Cannot move to HDD cache !");
                            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // Removing a least used entry from a RAM cache.
                        ramCache.removeObject(key_);
//                        System.out.println("Least used in RAM cache entry with key="
//                                + key_ + " is removed.");
                        repository.logger.info("Least used in RAM cache entry with key="
                                + key_ + " is removed.");
                        // Adding a newly downloaded entry to a RAM cache.
                        ramCache.addObject(key, cacheFeeder.deliverObject(key));
//                        System.out.println("New entry with key="
//                                + key + " is added to a RAM cache.\n");
                        repository.logger.info("New entry with key="
                                + key + " is added to a RAM cache.");
                        repository.logger.info("");
                    }
                }
            }
        }

        // Retrieving a requested entry to a CPU.
        return obj;
    }

    private void recache(Object key) {
        switch (repository.getCacheKind()) {
            case LFU: {
                /**
                 * If there was an HDD cache hit, then check if there is any
                 * object that was requested more times than any of the RAM
                 * cache object. If so, replace them.
                 *
                 * RAM cache - defined key. HDD cache - defined key.
                 */
                break;
            }
            case LRR: {
                /**
                 * Same as LRU
                 *
                 * RAM cache - first key in a map; HDD cache - requested key;
                 */
                replaceEntries(ramCache.mapObjects.entrySet().iterator().
                        next().getKey(), key);
                break;
            }
            case LRU: {
                /**
                 * If there was an HDD cache hit, then move this entry to a RAM
                 * cache, and before that, define the least used one in a RAM
                 * cache and move it back to HDD cache.
                 *
                 * RAM cache - first key in a map; HDD cache - requested key;
                 */
                replaceEntries(ramCache.mapObjects.entrySet().iterator().
                        next().getKey(), key);
                break;
            }
            case MRU: {
                /**
                 * If there was an HDD cache hit, then move this entry to a RAM
                 * cache, and before that, define the least used one in a RAM
                 * cache and move it back to HDD cache.
                 *
                 * RAM cache - keyLastAccessed; HDD cache - requested key;
                 */
                replaceEntries(ramCache.keyLastAccessed, key);
                break;
            }
        }
    }

    /**
     * Swaps least used object in a RAM cache with a most used object in an HDD
     * cache.
     */
    private void replaceEntries(Object keyRAMCache, Object keyHDDCache) {

        // If ram cache object is absent, no sense in replacement.
        if (!ramCache.hasObject(keyRAMCache)) {
//            System.out.println("RAM cache has no such object. Cache integrity is broken.");
            repository.logger.info("RAM cache has no such object. Cache integrity is broken.");
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!hddCache.hasObject(keyHDDCache)) {
//            System.out.println("HDD cache has no such object. Cache integrity is broken.");
            repository.logger.info("HDD cache has no such object. Cache integrity is broken.");
            return;
        }

        try {
            hddCache.addObject(keyRAMCache, ramCache.getObject(keyRAMCache));
            ramCache.removeObject(keyRAMCache);
            ramCache.addObject(keyHDDCache, hddCache.getObject(keyHDDCache));
            hddCache.removeObject(keyHDDCache);
        } catch (NotPresentException ex) {
//            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
            repository.logger.info("Cannot recache, such entry is absent. Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            repository.logger.info("Cannot recache, file or class not found. Cache integrity is broken.");
//            Logger.getLogger(CacheProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            System.out.println("Cannot recache ! Some IO problem. Cache integrity is broken.");
            repository.logger.info("Cannot recache, some IO problem. Cache integrity is broken.");
        }

//        System.out.println("Recaching has been done. Object in RAM cache key=" 
//                + keyRAMCache + " has been moved to an HDD cache. Object in HDD cache key=" 
//                + keyHDDCache + " has been moved to a RAM cache.");
        repository.logger.info("Recaching has been done. Object in RAM cache key="
                + keyRAMCache + " has been moved to an HDD cache. Object in HDD cache key="
                + keyHDDCache + " has been moved to a RAM cache.");
        repository.logger.info("");
    }

    private void printCaches() {

        if (ramCache.mapObjects instanceof LinkedHashMap) {

        }

//        System.out.print("--- RAM cache: ");
        repository.logger.info("--- RAM cache: ");
        if (ramCache.mapObjects != null) {
            for (Map.Entry<Object, Object> entrySet : ramCache.mapObjects.entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
//                System.out.print("key=" + key + "(" + (int) ramCache.frequency.get(key) + "), ");
                repository.logger.info("key=" + key + "(" + (int) ramCache.frequency.get(key) + "), ");
            }
//            System.out.println("");
//            System.out.print("--- HDD cache: ");
            repository.logger.info("--- HDD cache: ");
            if (hddCache.mapFiles instanceof LinkedHashMap) {

            } else {
                for (Map.Entry<Object, Object> entrySet : hddCache.mapFiles.entrySet()) {
                    Object key = entrySet.getKey();
                    Object value = entrySet.getValue();
//                    System.out.print("file=" + ((String) value).split("[\\\\]+")[1] 
//                            + "(" + (int) hddCache.mapFrequency.get(key) + "), ");
                    repository.logger.info("file=" + ((String) value).
                            split("[\\\\]+")[1] + "("
                            + (int) hddCache.mapFrequency.get(key) + "), ");
                }
            }
//            System.out.println("");
//            repository.logger.info("");
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
