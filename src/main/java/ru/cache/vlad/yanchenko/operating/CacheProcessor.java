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

    private final Logger logger;
    private final ICache ramCache;
    private final ICache hddCache;
    private final CacheFeeder cacheFeeder;
    private final Map<String, String> commandLineArguments;

    private static CacheProcessor sCacheProcessor;

    private CacheProcessor(@NonNull Logger logger,
                           @NonNull ICache ramCache,
                           @NonNull ICache hddCache,
                           @NonNull CacheFeeder cacheFeeder,
                           @NonNull Map<String, String> arguments) {
        this.logger = logger;
        this.ramCache = ramCache;
        this.hddCache = hddCache;
        commandLineArguments = arguments;
        this.cacheFeeder = cacheFeeder;
    }

    /**
     * Get singleton instance of this class.
     *
     * @param logger        to log the events
     * @param ramCache      memory cache
     * @param hddCache      disk cache
     * @param cacheFeeder   cache data feeder
     * @param arguments     command line arguments
     * @return              processor for cache
     */
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

        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            printCaches();
            logger.info(">>> Requested key=" + key);
        }

        // If RAM cache has a requested entry,
        if (ramCache.hasCacheEntry(key)) {
            logger.info("RAM cache hit, key=" + key);
            // return it to CPU.
            ramCache.setCacheHits(ramCache.getCacheHits() + 1);
            return ramCache.getEntry(key);
        } else {
            // RAM cache miss, 
            ramCache.setCacheMisses(ramCache.getCacheMisses() + 1);
            logger.info("! RAM cache miss, key=" + key);
            try {
                // trying to retrieve an entry from an HDD cache.
                obj = hddCache.getEntry(key);
                hddCache.setCacheHits(hddCache.getCacheHits() + 1);
                logger.info("HDD cache hit, key=" + key);
                reCache(key);
                return obj;
            } catch (NullPointerException | IOException | ClassNotFoundException ignored) {
                logger.info("! HDD cache miss, key=" + key);
            }
            // When both caches miss,
            if (obj == null) {
                hddCache.setCacheMisses(hddCache.getCacheMisses() + 1);
                // if RAM cache is not full,
                if (ramCache.getSize() < ramCache.getEntriesNumber()) {
                    // downloading a requested data from a mock source,
                    obj = cacheFeeder.deliverObject(key);
                    // try adding a newly downloaded entry to a RAM cache.
                    ramCache.putEntry(key, obj);
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Entry with key=" + key + " is added to a RAM cache.");
                    }
                    return obj;
                } else {    // RAM cache is full, it needs an eviction.
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Both caches miss. RAM cache is full, performing an eviction.");
                    }
                    /*
                     * Find the least used entry in a RAM cache and move it to an HDD cache and if HDD cache is full,
                     * remove the least used one. Then write to a RAM cache a new entry.
                     */
                    if (hddCache.getSize() < hddCache.getEntriesNumber()) {
                        // Getting the least used entry in a RAM cache.
                        Object key_ = ramCache.getLeastUsedEntry(CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        // Moving least used RAM entry to an HDD cache.
                        try {
                            hddCache.putEntry(key_, ramCache.getEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("An entry with key=" + key_ + " is moved to an HDD cache.");
                                logger.info("Entry with key=" + key + " is moved to a RAM cache.");
                                logger.info("");
                            }
                            // Removing such entry from a RAM cache.
                            ramCache.removeEntry(key_);
                            // Downloading a requested data from a mock source.
                            obj = cacheFeeder.deliverObject(key);
                            // Adding a newly downloaded entry to a RAM cache.
                            ramCache.putEntry(key, obj);
                            return obj;
                        } catch (IOException | NotPresentException ex) {
                            logger.info("!!! Some major trouble with displacement a least used and addition of a new entry.");
                        }
                    } else {
                        /*
                         * When all the caches are full and new entry is
                         * downloaded, remove the least used entry from an HDD
                         * cache, then move the least used RAM cache entry to an HDD
                         * cache and write a new entry to RAM cache.
                         */
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("HDD cache is full, removing a least used entry.");
                        }
                        // Getting the least used entry in an HDD cache 
                        Object key_ = hddCache.getLeastUsedEntry(CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // and removing this entry.
                            hddCache.removeEntry(key_);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("Entry with key=" + key_ + " is removed from an HDD cache. ");
                            }
                        } catch (NotPresentException ex) {
                            logger.error("!!! HDD cache error");
                            logger.error(ex.getMessage());
                        }
                        // Getting the least used entry in a RAM cache.
                        key_ = ramCache.getLeastUsedEntry(CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // Moving least used RAM entry to HDD cache.
                            hddCache.putEntry(key_, ramCache.getEntry(key_));
//                            hddCache.getMapFrequency().put(key_, 0);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("Least used RAM cache entry with key="
                                        + key_ + " is moved to an HDD cache. ");
                            }
                        } catch (IOException ex) {
                            logger.info("!!! Cannot move to HDD cache ! Disk drive might be corrupt.");
                        }
                        // Removing least used entry from a RAM cache.
                        ramCache.removeEntry(key_);
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("Least used RAM cache entry with key=" + key_ + " is removed.");
                        }
                        obj = cacheFeeder.deliverObject(key);
                        // Adding a newly downloaded entry to a RAM cache.
                        ramCache.putEntry(key, obj);
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("New entry with key=" + key + " is added to a RAM cache.");
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
        switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
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
                    replaceEntries(ramCache.getCacheEntries().entrySet().iterator().next().getKey(), key);
            case MRU ->
                /*
                 * If there was an HDD cache hit, then move this entry to a RAM cache, and before that,
                 * define the least used one in a RAM cache and move it back to HDD cache.
                 *
                 * RAM cache - keyLastAccessed; HDD cache - requested key;
                 */
                    replaceEntries(ramCache.getKeyLastAccessed(), key);
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
        if (!ramCache.hasCacheEntry(keyRAMCache)) {
            logger.info("RAM cache has no such object. Cache integrity is broken.");
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!hddCache.hasCacheEntry(keyHDDCache)) {
            logger.info("HDD cache has no such object. Cache integrity is broken.");
            return;
        }

        try {
            hddCache.putEntry(keyRAMCache, ramCache.getEntry(keyRAMCache));
            ramCache.removeEntry(keyRAMCache);
            ramCache.putEntry(keyHDDCache, hddCache.getEntry(keyHDDCache));
            hddCache.removeEntry(keyHDDCache);
        } catch (NotPresentException ex) {
            logger.info("Cannot recache, such entry is absent. Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            logger.info("Cannot recache, file or class not found. Cache integrity is broken.");
        } catch (IOException ex) {
            logger.info("Cannot recache, some IO problem. Cache integrity is broken.");
        }

        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("Recaching has been done.");
            logger.info("Object in RAM cache key=" + keyRAMCache + " has been moved to an HDD cache.");
            logger.info("Object in HDD cache key=" + keyHDDCache + " has been moved to a RAM cache.");
            logger.info("");
        }
    }

    // Logging a contents of both the caches.
    private void printCaches() {
//        if (ramCache.mapObjects instanceof LinkedHashMap) {
//
//        }
        logger.info("********    RAM cache contents    ********");
        if (ramCache.getCacheEntries() != null) {
            if (ramCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : ramCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue().toString();
                logger.info("\tkey=" + key + ", value=" + value);
            }
            logger.info("********    HDD cache contents    ********");
            if (hddCache.getCacheEntries().size() == 0) {
                logger.info("\tCache is empty");
            }
            for (Map.Entry<Object, Object> entrySet : hddCache.getCacheEntries().entrySet()) {
                Object key = entrySet.getKey();
                Object value = entrySet.getValue();
                logger.info("\tkey=" + key + ", value=" + ((String) value).split("[\\\\]+")[1]);
            }
        }
    }

    /**
     * Running a loop that simulates a process of retrieving / caching the data.
     */
    public void performCachingProcess() {
        Object obj;
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("\n\n<<<--- Data retrieval/caching loop has begun --->>>\n");
        }
        for (int i = 0; i < Integer.parseInt(commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            try {
                obj = processRequest(cacheFeeder.fetchObject());
                logger.info("Requested entry=" + obj + " delivered to a requester.");
                logger.info("");
            } catch (NotPresentException npex) {
                logger.error(npex);
            } catch (ClassNotFoundException cnfex) {
                logger.error(cnfex);
            } catch (IOException ioex) {
                logger.error(ioex);
            }
        }
        CacheLoggingUtils.printSummary(ramCache, hddCache, commandLineArguments);
    }

    //<editor-fold defaultstate="collapsed" desc="getters">

    /**
     * @return the ramCache
     */
    public ICache getRamCache() {
        return ramCache;
    }

    /**
     * @return the hddCache
     */
    public ICache getHddCache() {
        return hddCache;
    }

    /**
     * @return the cacheFeeder
     */
    public CacheFeeder getCacheFeeder() {
        return cacheFeeder;
    }
    //</editor-fold>
}
