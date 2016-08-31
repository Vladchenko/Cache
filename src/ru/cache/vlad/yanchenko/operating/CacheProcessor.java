/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.operating;

import ru.cache.vlad.yanchenko.caches.NotPresentException;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.caches.RAMCache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Class is in charge of an operations done while a cahign process is running.
 *
 * @author v.yanchenko
 */
public class CacheProcessor {

    private ru.cache.vlad.yanchenko.caches.RAMCache ramCache;
    private ru.cache.vlad.yanchenko.caches.HDDCache hddCache;
    private static Repository repository = Repository.getInstance();
    private static CacheProcessor cacheProcessor = CacheProcessor.getInstance();
    private ru.cache.vlad.yanchenko.operating.CacheFeeder cacheFeeder;

    private CacheProcessor(Repository repository) {
        ramCache = new ru.cache.vlad.yanchenko.caches.RAMCache();
        hddCache = new ru.cache.vlad.yanchenko.caches.HDDCache();
        this.repository = repository;
        /**
         * Setting how many entries will a cacheFeeder have to request from a 
         * cacheProcessor.
         */
        cacheFeeder = new ru.cache.vlad.yanchenko.operating.CacheFeeder(repository.getEntriesNumber());
        populateCaches();
    }
    
    // Public method that always returns the same instance of repository.
    public static CacheProcessor getInstance() {
        if (cacheProcessor == null) {
            cacheProcessor = new CacheProcessor(repository);
        }
        return cacheProcessor;
    }

    /**
     * Retrieving an entry from a cache or mock source, to allegedly pass it to
     * a requester.
     */
    public Object processRequest(String key) {

        // Data that is going to be retrieved on a CPU alleged request.
        Object obj = null;

        if (repository.isDetailedReport()) {
            printCaches();
            repository.getLogger().info(">>> Requested key=" + key);
        }

        // If RAM cache has a requested entry,
        if (ramCache.hasObject(key)) {
            if (repository.isDetailedReport()) {
                repository.getLogger().info("RAM cache hit, key=" + key);
                repository.getLogger().info("");
            }
            // return it to CPU.
            repository.setHitsRAMCache(repository.getHitsRAMCache() + 1);
            return ramCache.getObject(key);
        } else {
            // RAM cache miss, 
            repository.setMissesRAMCache(repository.getMissesRAMCache() + 1);
            try {
                // trying to retrieve an entry from an HDD cache.
                obj = hddCache.getObject(key);
                repository.setHitsHDDCache(repository.getHitsHDDCache() + 1);
                if (repository.isDetailedReport()) {
                    repository.getLogger().info("HDD cache hit, key=" + key);
                    repository.getLogger().info("");
                }
                recache(key);
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ignored) {
                repository.getLogger().info("!!! No such entry in HDD cache (" + key + ")");
            }
            // When both caches miss,
            if (obj == null) {
                repository.setMissesHDDCache(repository.getMissesHDDCache() + 1);
                // if RAM cache is not full,
                if (ramCache.getSize() < repository.getRAMCacheEntriesNumber()) {
                    // downloading a requested data from a mock source,
                    obj = cacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    ramCache.addObject(key, obj);
                    if (repository.isDetailedReport()) {
                        repository.getLogger().info("Cache miss. Entry with key="
                                + key + " is added to a RAM cache.");
                        repository.getLogger().info("");
                    }
                    return obj;
                } else {    // RAM cache is full, it needs an eviction.
                    /*
                     * Find the least used entry in a RAM cache and move it to 
                     * an HDD cache and if HDD cache is full, remove the least 
                     * used one. Then write to a RAM cache a new entry.
                     */
                    if (repository.isDetailedReport()) {
                        repository.getLogger().info("Cache miss. RAM cache is full, "
                                + "performing an eviction.");
                    }
                    if (hddCache.getSize() < repository.getHDDCacheEntriesNumber()) {
                        // Getting the least used entry in a RAM cache.
                        Object key_ = ramCache.getLeastUsed(repository.getCacheKind());
                        // Moving a least used RAM entry to an HDD cache.
                        try {
                            hddCache.addObject(key_, ramCache.getObject(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (repository.isDetailedReport()) {
                                repository.getLogger().info("An entry with key="
                                        + key_ + " is moved to an HDD cache.");
                                repository.getLogger().info("Entry with key=" + key
                                        + " is moved to a RAM cache.");
                                repository.getLogger().info("");
                            }
                            // Removing such entry from a RAM cache.
                            ramCache.removeObject(key_);
                            // Downloading a requested data from a mock source.
                            obj = cacheFeeder.deliverObject(key);
                            // Adding a newly downloaded entry to a RAM cache.
                            ramCache.addObject(key, obj);
                            return obj;
                        } catch (IOException ex) {
                            repository.getLogger().info("!!! Some major trouble with " +
                                    "displacement a least used and addition of a new entry.");
                        }
                    } else {
                        /**
                         * When all the caches are full and new entry is
                         * downloaded, remove least used entry from an HDD
                         * cache, then move least used RAM cache entry to an HDD
                         * cache and write a new entry to RAM cache.
                         */
                        if (repository.isDetailedReport()) {
                            repository.getLogger().info("");
                            repository.getLogger().info("HDD cache is full, "
                                    + "removing a least used entry.");
                        }
                        // Getting the least used entry in an HDD cache 
                        Object key_ = hddCache.getLeastUsed(repository.getCacheKind());
                        try {
                            // and removing this entry.
                            hddCache.removeObject(key_);
                            if (repository.isDetailedReport()) {
                                repository.getLogger().info("Entry with key=" + key_
                                        + " is removed from an HDD cache. ");
                            }
                        } catch (NotPresentException ex) {
                            repository.getLogger().info("!!! HDD cache entry failed to be removed, it is absent.");
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = ramCache.getLeastUsed(repository.getCacheKind());
                        try {
                            // Moving a least used RAM entry to a HDD cache.
                            hddCache.addObject(key_, ramCache.getObject(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (repository.isDetailedReport()) {
                                repository.getLogger().info("Least used entry in RAM"
                                        + " cache with key=" + key_
                                        + " is moved to an HDD cache. ");
                            }
                        } catch (IOException ex) {
                            if (repository.isDetailedReport()) {
                                repository.getLogger().info("!!! Cannot move to HDD cache"
                                        + " ! Disk drive might be corrupt");
                            }
                        }
                        // Removing a least used entry from a RAM cache.
                        ramCache.removeObject(key_);
                        if (repository.isDetailedReport()) {
                            repository.getLogger().info("Least used in RAM cache "
                                    + "entry with key=" + key_ + " is removed.");
                        }
                        // Adding a newly downloaded entry to a RAM cache.
                        ramCache.addObject(key, cacheFeeder.deliverObject(key));
                        if (repository.isDetailedReport()) {
                            repository.getLogger().info("New entry with key="
                                    + key + " is added to a RAM cache.");
                            repository.getLogger().info("");
                        }
                    }
                }
            }
        }

        // Retrieving a requested entry to a CPU.
        return obj;
    }

    private void removeCacheEntry() {

    }

    /**
     * Recaching the data in a caches. When data should be moved from an HDD
     * cache, to a RAM cache.
     */
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
            case LRU: {
                /**
                 * If there was an HDD cache hit, then move this entry to a RAM
                 * cache, and before that, define the least used one in a RAM
                 * cache and move it back to HDD cache.
                 *
                 * RAM cache - first key in a map; HDD cache - requested key;
                 */
                replaceEntries(ramCache.getMapEntries().entrySet().iterator().
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
                replaceEntries(ramCache.getKeyLastAccessed(), key);
                break;
            }
        }
    }

    /**
     * Swapping least used object in a RAM cache with a most used object in an
     * HDD cache.
     */
    private void replaceEntries(Object keyRAMCache, Object keyHDDCache) {

        // If ram cache object is absent, no sense in replacement.
        if (!ramCache.hasObject(keyRAMCache)) {
            repository.getLogger().info("RAM cache has no such object. Cache integrity is broken.");
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!hddCache.hasObject(keyHDDCache)) {
            repository.getLogger().info("HDD cache has no such object. Cache integrity is broken.");
            return;
        }

        try {
            hddCache.addObject(keyRAMCache, ramCache.getObject(keyRAMCache));
            ramCache.removeObject(keyRAMCache);
            ramCache.addObject(keyHDDCache, hddCache.getObject(keyHDDCache));
            hddCache.removeObject(keyHDDCache);
        } catch (NotPresentException ex) {
            repository.getLogger().info("Cannot recache, such entry is absent. "
                    + "Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            repository.getLogger().info("Cannot recache, file or class not found. "
                    + "Cache integrity is broken.");
        } catch (IOException ex) {
            repository.getLogger().info("Cannot recache, some IO problem. Cache "
                    + "integrity is broken.");
        }

        if (repository.isDetailedReport()) {
            repository.getLogger().info("Recaching has been done. Object in RAM "
                    + "cache key=" + keyRAMCache + " has been moved to an HDD "
                    + "cache. Object in HDD cache key=" + keyHDDCache + " has "
                    + "been moved to a RAM cache.");
            repository.getLogger().info("");
        }
    }

    // Writing a contents of both the caches to the log file.
    private void printCaches() {

//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        repository.getLogger().info("--- RAM cache contents ---");
        if (ramCache.getMapEntries() != null) {
            if (ramCache.getMapEntries().size() == 0) {
                repository.getLogger().info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : ramCache.getMapEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                repository.getLogger().info("\tkey=" + key + ", value="
                        + "Object@" + Integer.toHexString(
                                System.identityHashCode(
                                        ramCache.getMapEntries().get(key))) );
            }
            repository.getLogger().info("--- HDD cache contents ---");
            if (hddCache.getMapEntries().size() == 0) {
                repository.getLogger().info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : hddCache.getMapEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                repository.getLogger().info("\tfile=" + ((String) value).
                        split("[\\\\]+")[1] );
            }
        }
    }

    // Running a loop that simulates a process of retrieving / caching the data.
    public void performCachingProcess() {
        Object obj;
        if (repository.isDetailedReport()) {
            repository.getLogger().info("\n\n<<<--- Data retrieval/caching "
                    + "loop begun --->>>\n");
        }
        for (int i = 0; i < repository.getPipelineRunTimes(); i++) {
            obj = processRequest(cacheFeeder.requestObject());
        }
        repository.printSummary();
    }
    
    // Populating caches before running a caching-retrieval process.
    private void populateCaches() {
        while (ramCache.getSize() < repository.getRAMCacheEntriesNumber()) {
            ramCache.addObject(cacheFeeder.requestObject(),
                    cacheFeeder.deliverObject(cacheFeeder.requestObject()));
        }
        while (hddCache.getSize() < repository.getHDDCacheEntriesNumber()) {
            try {
                hddCache.addObject(cacheFeeder.requestObject(),
                        cacheFeeder.deliverObject(cacheFeeder.requestObject()));
            } catch (IOException ex) {
                repository.getLogger().info("Cannot populate HDD cache, some IO "
                        + "problem.");
            }
        }
        repository.getLogger().info("Caches have been populated.");
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    /**
     * @return the ramCache
     */
    public ru.cache.vlad.yanchenko.caches.RAMCache getRamCache() {
        return ramCache;
    }

    /**
     * @param ramCache the ramCache to set
     */
    public void setRamCache(RAMCache ramCache) {
        this.ramCache = ramCache;
    }

    /**
     * @return the hddCache
     */
    public ru.cache.vlad.yanchenko.caches.HDDCache getHddCache() {
        return hddCache;
    }

    /**
     * @param hddCache the hddCache to set
     */
    public void setHddCache(ru.cache.vlad.yanchenko.caches.HDDCache hddCache) {
        this.hddCache = hddCache;
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * @return the cacheFeeder
     */
    public ru.cache.vlad.yanchenko.operating.CacheFeeder getCacheFeeder() {
        return cacheFeeder;
    }

    /**
     * @param cacheFeeder the cacheFeeder to set
     */
    public void setCacheFeeder(ru.cache.vlad.yanchenko.operating.CacheFeeder cacheFeeder) {
        this.cacheFeeder = cacheFeeder;
    }
    //</editor-fold>
}
