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

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Class is in charge of an operations done while a caching process is running.
 *
 * @author v.yanchenko
 */
public class CacheProcessor<T, V> {

    private final Logger logger;
    private final ICache<T, V> ramCache;
    private final ICache<T, V> hddCache;
    private final CacheFeeder<T, V> cacheFeeder;
    private final CacheUtils<T, V> cacheUtils;
    private final Map<String, String> commandLineArguments;

    /**
     * Public constructor - creates an instance of class and provides dependencies.
     *
     * @param logger      to log the events
     * @param ramCache    memory cache
     * @param hddCache    disk cache
     * @param cacheFeeder cache data feeder
     * @param arguments   command line arguments
     */
    public CacheProcessor(@NonNull Logger logger,
                          @NonNull ICache<T, V> ramCache,
                          @NonNull ICache<T, V> hddCache,
                          @NonNull CacheFeeder<T, V> cacheFeeder,
                          @NonNull CacheUtils<T, V> cacheUtils,
                          @NonNull Map<String, String> arguments) {
        this.logger = logger;
        this.ramCache = ramCache;
        this.hddCache = hddCache;
        this.cacheUtils = cacheUtils;
        commandLineArguments = arguments;
        this.cacheFeeder = cacheFeeder;
    }

    /**
     * Retrieving an entry from a cache or mock source, to allegedly pass it to a requester.
     *
     * @param key to get cached-object by
     * @return cached-object
     */
    public V processRequest(@NonNull T key) throws IOException, ClassNotFoundException, NotPresentException {

        // Data that is going to be retrieved on an alleged request.
        V obj = null;

        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            cacheUtils.logCaches(logger, ramCache, hddCache);
            logger.info("");
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
                    obj = cacheFeeder.deliverCacheEntry(key);
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
                        T leastUsedEntryKey = ramCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        // Moving least used RAM entry to an HDD cache.
                        try {
                            hddCache.putEntry(leastUsedEntryKey, ramCache.getEntry(leastUsedEntryKey));
//                            hddCache.getMapFrequency().put(leastUsedEntryKey, 0);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("An entry with key=" + leastUsedEntryKey + " is moved to an HDD cache.");
                                logger.info("Entry with key=" + key + " is moved to a RAM cache.");
                                logger.info("");
                            }
                            // Removing such entry from a RAM cache.
                            ramCache.removeEntry(leastUsedEntryKey);
                            // Downloading a requested data from a mock source.
                            obj = cacheFeeder.deliverCacheEntry(key);
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
                        T leastUsedEntryKey = hddCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // and removing this entry.
                            hddCache.removeEntry(leastUsedEntryKey);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("Entry with key=" + leastUsedEntryKey + " is removed from an HDD cache. ");
                            }
                        } catch (NotPresentException ex) {
                            logger.error("!!! HDD cache entry cannot be removed");
                            logger.error(ex.getMessage());
                        }
                        // Getting the least used entry in a RAM cache.
                        leastUsedEntryKey = ramCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        try {
                            // Moving least used RAM entry to HDD cache.
                            hddCache.putEntry(leastUsedEntryKey, ramCache.getEntry(leastUsedEntryKey));
//                            hddCache.getMapFrequency().put(leastUsedEntryKey, 0);
                            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                                logger.info("Least used RAM cache entry with key="
                                        + leastUsedEntryKey + " is moved to an HDD cache. ");
                            }
                        } catch (IOException ex) {
                            logger.info("!!! Cannot move to HDD cache ! Disk drive might be corrupt.");
                        }
                        // Removing least used entry from a RAM cache.
                        ramCache.removeEntry(leastUsedEntryKey);
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("Least used RAM cache entry with key=" + leastUsedEntryKey + " is removed.");
                        }
                        obj = cacheFeeder.deliverCacheEntry(key);
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
     *
     * @param key to get an object by
     */
    private void reCache(@NonNull T key) {
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
            default -> {
                // TODO
            }
        }
    }

    /**
     * Swapping the least used object in a RAM cache with a most used object in an HDD cache.
     *
     * @param keyRAMCache memory cache
     * @param keyHDDCache disk cache
     */
    private void replaceEntries(@NonNull T keyRAMCache, @NonNull T keyHDDCache) {

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
                obj = processRequest(cacheFeeder.fetchKey());
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
    public ICache<T, V> getRamCache() {
        return ramCache;
    }

    /**
     * @return the hddCache
     */
    public ICache<T, V> getHddCache() {
        return hddCache;
    }

    /**
     * @return the cacheFeeder
     */
    public CacheFeeder<T, V> getCacheFeeder() {
        return cacheFeeder;
    }
    //</editor-fold>
}
