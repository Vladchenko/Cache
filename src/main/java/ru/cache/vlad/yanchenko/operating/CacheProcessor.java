/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.HDDCache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.caches.RAMCache;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 * Class is in charge of an operations done while a caching process is running.
 *
 * @author v.yanchenko
 */
public class CacheProcessor {

    private final Logger mLogger;
    private final RAMCache mRamCache;
    private final HDDCache mHddCache;
    private final Repository mRepository;
    private final CacheFeeder mCacheFeeder;

    private static CacheProcessor sCacheProcessor;

    private CacheProcessor(@NonNull Logger logger, @NonNull Repository repository, @NonNull CacheFeeder cacheFeeder) {
        mLogger = logger;
        mRepository = repository;
        mRamCache = new RAMCache(logger, repository);
        mHddCache = new HDDCache(logger, repository);
        // Setting how many entries will a cacheFeeder have to request from a cacheProcessor.
        mCacheFeeder = cacheFeeder;
        populateCaches();
    }

    // Public method that always returns the same instance of repository.
    public static CacheProcessor getInstance(
            @NonNull Logger logger,
            @NonNull Repository repository,
            @NonNull CacheFeeder cacheFeeder) {
        if (sCacheProcessor == null) {
            sCacheProcessor = new CacheProcessor(logger, repository, cacheFeeder);
        }
        return sCacheProcessor;
    }

    /**
     * Retrieving an entry from a cache or mock source, to allegedly pass it to a requester.
     *
     * @param key to get cached-object by
     * @return cached-object
     */
    public Object processRequest(@NonNull String key) {

        // Data that is going to be retrieved on a CPU alleged request.
        Object obj = null;

        if (mRepository.isDetailedReport()) {
            printCaches();
            mLogger.info(">>> Requested key=" + key);
        }

        // If RAM cache has a requested entry,
        if (mRamCache.hasCacheEntry(key)) {
            if (mRepository.isDetailedReport()) {
                mLogger.info("RAM cache hit, key=" + key);
                mLogger.info("");
            }
            // return it to CPU.
            mRepository.setHitsRAMCache(mRepository.getHitsRAMCache() + 1);
            return mRamCache.getCacheEntry(key);
        } else {
            // RAM cache miss, 
            mRepository.setMissesRAMCache(mRepository.getMissesRAMCache() + 1);
            try {
                // trying to retrieve an entry from an HDD cache.
                obj = mHddCache.getCacheEntry(key);
                mRepository.setHitsHDDCache(mRepository.getHitsHDDCache() + 1);
                if (mRepository.isDetailedReport()) {
                    mLogger.info("HDD cache hit, key=" + key);
                    mLogger.info("");
                }
                reCache(key);
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ignored) {
                mLogger.info("!!! No such entry in HDD cache (" + key + ")");
            }
            // When both caches miss,
            if (obj == null) {
                mRepository.setMissesHDDCache(mRepository.getMissesHDDCache() + 1);
                // if RAM cache is not full,
                if (mRamCache.getSize() < mRepository.getRAMCacheEntriesNumber()) {
                    // downloading a requested data from a mock source,
                    obj = mCacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    mRamCache.addCacheEntry(key, obj);
                    if (mRepository.isDetailedReport()) {
                        mLogger.info("Cache miss. Entry with key="
                                + key + " is added to a RAM cache.");
                        mLogger.info("");
                    }
                    return obj;
                } else {    // RAM cache is full, it needs an eviction.
                    /*
                     * Find the least used entry in a RAM cache and move it to
                     * an HDD cache and if HDD cache is full, remove the least
                     * used one. Then write to a RAM cache a new entry.
                     */
                    if (mRepository.isDetailedReport()) {
                        mLogger.info("Cache miss. RAM cache is full, "
                                + "performing an eviction.");
                    }
                    if (mHddCache.getSize() < mRepository.getHDDCacheEntriesNumber()) {
                        // Getting the least used entry in a RAM cache.
                        Object key_ = mRamCache.getLeastUsed(mRepository.getCacheKind());
                        // Moving least used RAM entry to an HDD cache.
                        try {
                            mHddCache.addCacheEntry(key_, mRamCache.getCacheEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (mRepository.isDetailedReport()) {
                                mLogger.info("An entry with key="
                                        + key_ + " is moved to an HDD cache.");
                                mLogger.info("Entry with key=" + key
                                        + " is moved to a RAM cache.");
                                mLogger.info("");
                            }
                            // Removing such entry from a RAM cache.
                            mRamCache.removeCacheEntry(key_);
                            // Downloading a requested data from a mock source.
                            obj = mCacheFeeder.deliverObject(key);
                            // Adding a newly downloaded entry to a RAM cache.
                            mRamCache.addCacheEntry(key, obj);
                            return obj;
                        } catch (IOException ex) {
                            mLogger.info("!!! Some major trouble with " +
                                    "displacement a least used and addition of a new entry.");
                        }
                    } else {
                        /*
                         * When all the caches are full and new entry is
                         * downloaded, remove the least used entry from an HDD
                         * cache, then move the least used RAM cache entry to an HDD
                         * cache and write a new entry to RAM cache.
                         */
                        if (mRepository.isDetailedReport()) {
                            mLogger.info("");
                            mLogger.info("HDD cache is full, "
                                    + "removing a least used entry.");
                        }
                        // Getting the least used entry in an HDD cache 
                        Object key_ = mHddCache.getLeastUsed(mRepository.getCacheKind());
                        try {
                            // and removing this entry.
                            mHddCache.removeCacheEntry(key_);
                            if (mRepository.isDetailedReport()) {
                                mLogger.info("Entry with key=" + key_
                                        + " is removed from an HDD cache. ");
                            }
                        } catch (NotPresentException ex) {
                            mLogger.info("!!! HDD cache entry failed to be removed, it is absent.");
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = mRamCache.getLeastUsed(mRepository.getCacheKind());
                        try {
                            // Moving least used RAM entry to HDD cache.
                            mHddCache.addCacheEntry(key_, mRamCache.getCacheEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (mRepository.isDetailedReport()) {
                                mLogger.info("Least used entry in RAM"
                                        + " cache with key=" + key_
                                        + " is moved to an HDD cache. ");
                            }
                        } catch (IOException ex) {
                            if (mRepository.isDetailedReport()) {
                                mLogger.info("!!! Cannot move to HDD cache"
                                        + " ! Disk drive might be corrupt.");
                            }
                        }
                        // Removing least used entry from a RAM cache.
                        mRamCache.removeCacheEntry(key_);
                        if (mRepository.isDetailedReport()) {
                            mLogger.info("Least used in RAM cache "
                                    + "entry with key=" + key_ + " is removed.");
                        }
                        // Adding a newly downloaded entry to a RAM cache.
                        mRamCache.addCacheEntry(key, mCacheFeeder.deliverObject(key));
                        if (mRepository.isDetailedReport()) {
                            mLogger.info("New entry with key="
                                    + key + " is added to a RAM cache.");
                            mLogger.info("");
                        }
                    }
                }
            }
        }

        // Fetching a requested entry to a CPU.
        return obj;
    }

    private void removeCacheEntry() {

    }

    /**
     * Recaching the data in a caches. When data should be moved from an HDD
     * cache, to a RAM cache.
     */
    private void reCache(@NonNull Object key) {
        switch (mRepository.getCacheKind()) {
            case LFU -> {
                /*
                 * If there was an HDD cache hit, then check if there is any
                 * object that was requested more times than any of the RAM
                 * cache object. If so, replace them.
                 *
                 * RAM cache - defined key. HDD cache - defined key.
                 */
            }
            case LRU ->
                    /*
                     * If there was an HDD cache hit, then move this entry to a RAM
                     * cache, and before that, define the least used one in a RAM
                     * cache and move it back to HDD cache.
                     *
                     * RAM cache - first key in a map; HDD cache - requested key;
                     */
                    replaceEntries(mRamCache.getMapEntries().entrySet().iterator().
                            next().getKey(), key);
            case MRU ->
                    /*
                     * If there was an HDD cache hit, then move this entry to a RAM
                     * cache, and before that, define the least used one in a RAM
                     * cache and move it back to HDD cache.
                     *
                     * RAM cache - keyLastAccessed; HDD cache - requested key;
                     */
                    replaceEntries(mRamCache.getKeyLastAccessed(), key);
        }
    }

    /**
     * Swapping the least used object in a RAM cache with a most used object in an HDD cache.
     */
    private void replaceEntries(@NonNull Object keyRAMCache, @NonNull Object keyHDDCache) {

        // If ram cache object is absent, no sense in replacement.
        if (!mRamCache.hasCacheEntry(keyRAMCache)) {
            mLogger.info("RAM cache has no such object. Cache integrity is broken.");
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!mHddCache.hasCacheEntry(keyHDDCache)) {
            mLogger.info("HDD cache has no such object. Cache integrity is broken.");
            return;
        }

        try {
            mHddCache.addCacheEntry(keyRAMCache, mRamCache.getCacheEntry(keyRAMCache));
            mRamCache.removeCacheEntry(keyRAMCache);
            mRamCache.addCacheEntry(keyHDDCache, mHddCache.getCacheEntry(keyHDDCache));
            mHddCache.removeCacheEntry(keyHDDCache);
        } catch (NotPresentException ex) {
            mLogger.info("Cannot recache, such entry is absent. "
                    + "Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            mLogger.info("Cannot recache, file or class not found. "
                    + "Cache integrity is broken.");
        } catch (IOException ex) {
            mLogger.info("Cannot recache, some IO problem. Cache "
                    + "integrity is broken.");
        }

        if (mRepository.isDetailedReport()) {
            mLogger.info("Recaching has been done. Object in RAM "
                    + "cache key=" + keyRAMCache + " have been moved to an HDD "
                    + "cache. Object in HDD cache key=" + keyHDDCache + " has "
                    + "been moved to a RAM cache.");
            mLogger.info("");
        }
    }

    // Writing a contents of both the caches to the log file.
    private void printCaches() {

//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        mLogger.info("--- RAM cache contents ---");
        if (mRamCache.getMapEntries() != null) {
            if (mRamCache.getMapEntries().size() == 0) {
                mLogger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : mRamCache.getMapEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                mLogger.info("\tkey=" + key + ", value="
                        + "Object@" + Integer.toHexString(
                        System.identityHashCode(
                                mRamCache.getMapEntries().get(key))));
            }
            mLogger.info("--- HDD cache contents ---");
            if (mHddCache.getMapEntries().size() == 0) {
                mLogger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : mHddCache.getMapEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                mLogger.info("\tfile=" + ((String) value).
                        split("[\\\\]+")[1]);
            }
        }
    }

    /**
     * Running a loop that simulates a process of retrieving / caching the data.
     */
    public void performCachingProcess() {
        Object obj;
        if (mRepository.isDetailedReport()) {
            mLogger.info("\n\n<<<--- Data retrieval/caching "
                    + "loop begun --->>>\n");
        }
        for (int i = 0; i < mRepository.getPipelineRunTimes(); i++) {
            obj = processRequest(mCacheFeeder.requestObject());
        }
        CacheLoggingUtils.printSummary(mRepository);
    }

    // Populating caches before running a caching-retrieval process.
    private void populateCaches() {
        while (mRamCache.getSize() < mRepository.getRAMCacheEntriesNumber()) {
            mRamCache.addCacheEntry(mCacheFeeder.requestObject(),
                    mCacheFeeder.deliverObject(mCacheFeeder.requestObject()));
        }
        while (mHddCache.getSize() < mRepository.getHDDCacheEntriesNumber()) {
            try {
                mHddCache.addCacheEntry(mCacheFeeder.requestObject(),
                        mCacheFeeder.deliverObject(mCacheFeeder.requestObject()));
            } catch (IOException ex) {
                mLogger.info("Cannot populate HDD cache, some IO "
                        + "problem.");
            }
        }
        mLogger.info("Caches have been populated.");
    }

    //<editor-fold defaultstate="collapsed" desc="getters">

    /**
     * @return the ramCache
     */
    public ru.cache.vlad.yanchenko.caches.RAMCache getRamCache() {
        return mRamCache;
    }

    /**
     * @return the hddCache
     */
    public ru.cache.vlad.yanchenko.caches.HDDCache getHddCache() {
        return mHddCache;
    }

    /**
     * @return the cacheFeeder
     */
    public ru.cache.vlad.yanchenko.operating.CacheFeeder getCacheFeeder() {
        return mCacheFeeder;
    }
    //</editor-fold>
}
