package ru.cache.vlad.yanchenko.operating;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.*;

/**
 * Class is in charge of an operations done while a caching process is running.
 *
 * @author v.yanchenko
 */
public class CacheProcessor {

    private final Logger mLogger;
    private final ICache mRamCache;
    private final ICache mHddCache;
    private final CacheFeeder mCacheFeeder;
    private final Map<String, String> mArguments;

    private static CacheProcessor sCacheProcessor;

    private CacheProcessor(@NonNull Logger logger,
                           @NonNull ICache ramCache,
                           @NonNull ICache hddCache,
                           @NonNull CacheFeeder cacheFeeder,
                           @NonNull Map<String, String> arguments) {
        mLogger = logger;
        mRamCache = ramCache;
        mHddCache = hddCache;
        mArguments = arguments;
        mCacheFeeder = cacheFeeder;
    }

    // Public method that always returns the same instance of repository.
    public static CacheProcessor getInstance(
            @NonNull Logger logger,
            @NonNull ICache ramCache,
            @NonNull ICache hddCache,
            @NonNull CacheFeeder cacheFeeder,
            @NonNull Map<String, String> arguments) {
        if (sCacheProcessor == null) {
            sCacheProcessor = new CacheProcessor(logger, ramCache, hddCache, cacheFeeder, arguments);
        }
        return sCacheProcessor;
    }

    /**
     * Retrieving an entry from a cache or mock source, to allegedly pass it to a requester.
     *
     * @param key to get cached-object by
     * @return cached-object
     */
    public Object processRequest(@NonNull String key) throws IOException, ClassNotFoundException, NotPresentException {

        // Data that is going to be retrieved on an alleged request.
        Object obj = null;

        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            printCaches();
            mLogger.info(">>> Requested key=" + key);
        }

        // If RAM cache has a requested entry,
        if (mRamCache.hasCacheEntry(key)) {
            mLogger.info("RAM cache hit, key=" + key);
            // return it to CPU.
            mRamCache.setCacheHits(mRamCache.getCacheHits() + 1);
            return mRamCache.getEntry(key);
        } else {
            // RAM cache miss, 
            mRamCache.setCacheMisses(mRamCache.getCacheMisses() + 1);
            mLogger.info("! RAM cache miss, key=" + key);
            try {
                // trying to retrieve an entry from an HDD cache.
                obj = mHddCache.getEntry(key);
                mHddCache.setCacheHits(mHddCache.getCacheHits() + 1);
                mLogger.info("HDD cache hit, key=" + key);
                reCache(key);
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ignored) {
                mLogger.info("! HDD cache miss, key=" + key);
            }
            // When both caches miss,
            if (obj == null) {
                mHddCache.setCacheMisses(mHddCache.getCacheMisses() + 1);
                // if RAM cache is not full,
                if (mRamCache.getSize() < mRamCache.getEntriesNumber()) {
                    // downloading a requested data from a mock source,
                    obj = mCacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    mRamCache.putEntry(key, obj);
                    if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        mLogger.info("Entry with key=" + key + " is added to a RAM cache.");
                    }
                    return obj;
                } else {    // RAM cache is full, it needs an eviction.
                    if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        mLogger.info("Both caches miss. RAM cache is full, performing an eviction.");
                    }
                    /*
                     * Find the least used entry in a RAM cache and move it to an HDD cache and if HDD cache is full,
                     * remove the least used one. Then write to a RAM cache a new entry.
                     */
                    if (mHddCache.getSize() < mHddCache.getEntriesNumber()) {
                        // Getting the least used entry in a RAM cache.
                        Object key_ = mRamCache.getLeastUsedEntry(CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        // Moving least used RAM entry to an HDD cache.
                        try {
                            mHddCache.putEntry(key_, mRamCache.getEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                mLogger.info("An entry with key=" + key_ + " is moved to an HDD cache.");
                                mLogger.info("Entry with key=" + key + " is moved to a RAM cache.");
                                mLogger.info("");
                            }
                            // Removing such entry from a RAM cache.
                            mRamCache.removeEntry(key_);
                            // Downloading a requested data from a mock source.
                            obj = mCacheFeeder.deliverObject(key);
                            // Adding a newly downloaded entry to a RAM cache.
                            mRamCache.putEntry(key, obj);
                            return obj;
                        } catch (IOException | NotPresentException ex) {
                            mLogger.info("!!! Some major trouble with displacement a least used and addition of a new entry.");
                        }
                    } else {
                        /*
                         * When all the caches are full and new entry is
                         * downloaded, remove the least used entry from an HDD
                         * cache, then move the least used RAM cache entry to an HDD
                         * cache and write a new entry to RAM cache.
                         */
                        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            mLogger.info("HDD cache is full, removing a least used entry.");
                        }
                        // Getting the least used entry in an HDD cache 
                        Object key_ = mHddCache.getLeastUsedEntry(CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // and removing this entry.
                            mHddCache.removeEntry(key_);
                            if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                mLogger.info("Entry with key=" + key_ + " is removed from an HDD cache. ");
                            }
                        } catch (NotPresentException ex) {
                            mLogger.error("!!! HDD cache error");
                            mLogger.error(ex.getMessage());
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = mRamCache.getLeastUsedEntry(CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // Moving least used RAM entry to HDD cache.
                            mHddCache.putEntry(key_, mRamCache.getEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                mLogger.info("Least used RAM cache entry with key="
                                        + key_ + " is moved to an HDD cache. ");
                            }
                        } catch (IOException ex) {
                            mLogger.info("!!! Cannot move to HDD cache ! Disk drive might be corrupt.");
                        }
                        // Removing least used entry from a RAM cache.
                        mRamCache.removeEntry(key_);
                        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            mLogger.info("Least used RAM cache entry with key=" + key_ + " is removed.");
                        }
                        obj = mCacheFeeder.deliverObject(key);
                        // Adding a newly downloaded entry to a RAM cache.
                        mRamCache.putEntry(key, obj);
                        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            mLogger.info("New entry with key=" + key + " is added to a RAM cache.");
                        }
                    }
                }
            }
        }

        // Fetching a requested entry.
        return obj;
    }

    /**
     * Recaching the data in a caches. When data should be moved from an HDD cache, to a RAM cache.
     */
    private void reCache(@NonNull Object key) {
        switch (CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
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
                     * If there was an HDD cache hit, then move this entry to a RAM cache, and before that,
                     * define the least used one in a RAM cache and move it back to HDD cache.
                     *
                     * RAM cache - first key in a map; HDD cache - requested key;
                     */
                    replaceEntries(mRamCache.getCacheEntries().entrySet().iterator().next().getKey(), key);
            case MRU ->
                    /*
                     * If there was an HDD cache hit, then move this entry to a RAM cache, and before that,
                     * define the least used one in a RAM cache and move it back to HDD cache.
                     *
                     * RAM cache - keyLastAccessed; HDD cache - requested key;
                     */
                    replaceEntries(mRamCache.getKeyLastAccessed(), key);
        }
    }

    /**
     * Swapping the least used object in a RAM cache with a most used object in an HDD cache.
     *
     * @param keyRAMCache memory cache
     * @param keyHDDCache disk cache
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
            mHddCache.putEntry(keyRAMCache, mRamCache.getEntry(keyRAMCache));
            mRamCache.removeEntry(keyRAMCache);
            mRamCache.putEntry(keyHDDCache, mHddCache.getEntry(keyHDDCache));
            mHddCache.removeEntry(keyHDDCache);
        } catch (NotPresentException ex) {
            mLogger.info("Cannot recache, such entry is absent. Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            mLogger.info("Cannot recache, file or class not found. Cache integrity is broken.");
        } catch (IOException ex) {
            mLogger.info("Cannot recache, some IO problem. Cache integrity is broken.");
        }

        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            mLogger.info("Recaching has been done.");
            mLogger.info("Object in RAM cache key=" + keyRAMCache + " has been moved to an HDD cache.");
            mLogger.info("Object in HDD cache key=" + keyHDDCache + " has been moved to a RAM cache.");
            mLogger.info("");
        }
    }

    // Logging a contents of both the caches.
    private void printCaches() {
//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        mLogger.info("********    RAM cache contents    ********");
        if (mRamCache.getCacheEntries() != null) {
            if (mRamCache.getCacheEntries().size() == 0) {
                mLogger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : mRamCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue().toString();
                mLogger.info("\tkey=" + key + ", value=" + value);
            }
            mLogger.info("********    HDD cache contents    ********");
            if (mHddCache.getCacheEntries().size() == 0) {
                mLogger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : mHddCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                mLogger.info("\tkey=" + key + ", value=" + ((String) value).split("[\\\\]+")[1]);
            }
        }
    }

    /**
     * Running a loop that simulates a process of retrieving / caching the data.
     */
    public void performCachingProcess() {
        Object obj;
        if (Boolean.parseBoolean(mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            mLogger.info("\n\n<<<--- Data retrieval/caching loop has begun --->>>\n");
        }
        for (int i = 0; i < Integer.parseInt(mArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            try {
                obj = processRequest(mCacheFeeder.requestObject());
                mLogger.info("Requested entry=" + obj + " delivered to a requester.");
                mLogger.info("");
            } catch (NotPresentException npex) {
                mLogger.error(npex);
            } catch (ClassNotFoundException cnfex) {
                mLogger.error(cnfex);
            } catch (IOException ioex) {
                mLogger.error(ioex);
            }
        }
        CacheLoggingUtils.printSummary(mRamCache, mHddCache, mArguments);
    }

    //<editor-fold defaultstate="collapsed" desc="getters">

    /**
     * @return the ramCache
     */
    public ICache getRamCache() {
        return mRamCache;
    }

    /**
     * @return the hddCache
     */
    public ICache getHddCache() {
        return mHddCache;
    }

    /**
     * @return the cacheFeeder
     */
    public CacheFeeder getCacheFeeder() {
        return mCacheFeeder;
    }
    //</editor-fold>
}
