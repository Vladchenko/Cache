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
    private final Map<String, String> commandLineArguments;
    private final CacheLoggingUtils<T, V> cacheLoggingUtils;

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
                          @NonNull Map<String, String> arguments,
                          @NonNull CacheLoggingUtils<T, V> cacheLoggingUtils) {
        this.logger = logger;
        this.ramCache = ramCache;
        this.hddCache = hddCache;
        this.cacheFeeder = cacheFeeder;
        commandLineArguments = arguments;
        this.cacheLoggingUtils = cacheLoggingUtils;
    }

    /**
     * Retrieve an entry from a cache or mock source, to allegedly pass it to a requester.
     *
     * @param key to get cached-object by
     * @return cached-object
     */
    public V processRequest(@NonNull T key) throws IOException, ClassNotFoundException, NotPresentException {

        // Data that is going to be retrieved on an alleged request.
        V cacheEntry = null;

        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            cacheLoggingUtils.printCachesContents(logger, ramCache, hddCache);
            logger.info("");
            logger.info(">>> Requested key = {}", key);
        }

        // If RAM cache has a requested entry,
        if (ramCache.hasCacheEntry(key)) {
            logger.info("RAM cache hit, key = {}", key);
            // return it to CPU.
            ramCache.setCacheHits(ramCache.getCacheHits() + 1);
            return ramCache.getEntry(key);
        } else {
            // RAM cache miss, 
            ramCache.setCacheMisses(ramCache.getCacheMisses() + 1);
            logger.info("! RAM cache miss, key = {}", key);
            try {
                // Retrieve an entry from an HDD cache.
                cacheEntry = hddCache.getEntry(key);
                hddCache.setCacheHits(hddCache.getCacheHits() + 1);
                logger.info("HDD cache hit, key = {}", key);
                reCache(key);
                return cacheEntry;
            } catch (NullPointerException | IOException | ClassNotFoundException ignored) {
                logger.info("! HDD cache miss, key = {}", key);
            }
            // When both caches miss,
            if (cacheEntry == null) {
                hddCache.setCacheMisses(hddCache.getCacheMisses() + 1);
                // If RAM cache is not full,
                if (ramCache.getSize() < ramCache.getEntriesNumber()) {
                    // Download a requested data from a mock source.
                    cacheEntry = cacheFeeder.deliverCacheEntry(key);
                    // Add a newly downloaded entry to a RAM cache.
                    ramCache.putEntry(key, cacheEntry);
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Entry with key = {}, is added to a RAM cache.", key);
                    }
                    return cacheEntry;
                } else {    // RAM cache is full, it needs an eviction.
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Both caches miss. RAM cache is full, performing an eviction.");
                    }
                    /*
                     * Find the least used entry in a RAM cache and move it to an HDD cache and if HDD cache is full,
                     * remove the least used one. Then write to a RAM cache a new entry.
                     */
                    if (hddCache.getSize() < hddCache.getEntriesNumber()) {
                        // Get the least used entry in a RAM cache.
                        T leastUsedEntryKey = ramCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        // Move the least used RAM entry to an HDD cache.
                        try {
                            moveLeastUsedFromRamToHdd(key, leastUsedEntryKey);
                            // Download a requested data from a mock source.
                            cacheEntry = cacheFeeder.deliverCacheEntry(key);
                            // Add a newly downloaded entry to a RAM cache.
                            ramCache.putEntry(key, cacheEntry);
                            return cacheEntry;
                        } catch (IOException | NotPresentException ex) {
                            logger.info("!!! Some major trouble with moving a least used entry and appending a new one.");
                        }
                    } else {
                        /*
                         * When all the caches are full and new entry is downloaded, remove the least used entry
                         * from an HDD cache, then move the least used RAM cache entry to an HDD cache and
                         * write a new entry to RAM cache.
                         */
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("HDD cache is full, removing a least used entry.");
                        }
                        // Get the least used entry in an HDD cache
                        T leastUsedEntryKey = hddCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        removeLeastUsedFromHddCache(leastUsedEntryKey);
                        // Get the least used entry in a RAM cache.
                        leastUsedEntryKey = ramCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        moveLeastUsedRamEntryToHdd(leastUsedEntryKey);
                        // Download a requested data from a mock source.
                        cacheEntry = cacheFeeder.deliverCacheEntry(key);
                        // Add a newly downloaded entry to a RAM cache.
                        ramCache.putEntry(key, cacheEntry);
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("New entry with key = {}, is added to a RAM cache.", key);
                        }
                    }
                }
            }
        }

        // Fetch a requested entry.
        return cacheEntry;
    }

    private void moveLeastUsedRamEntryToHdd(T leastUsedEntryKey) throws ClassNotFoundException, NotPresentException {
        try {
            // Move the least used RAM entry to HDD cache.
            hddCache.putEntry(leastUsedEntryKey, ramCache.getEntry(leastUsedEntryKey));
//                            hddCache.getMapFrequency().put(leastUsedEntryKey, 0);
            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                logger.info("Least used RAM cache entry with key = {}, is moved to an HDD cache. ",
                        leastUsedEntryKey);
            }
        } catch (IOException ex) {
            logger.info("!!! Cannot move to HDD cache ! Disk drive might be corrupt.");
            logger.error(ex.getMessage());
        }
        // Remove the least used entry from a RAM cache.
        ramCache.removeEntry(leastUsedEntryKey);
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("Least used RAM cache entry with key = {}, is removed.", leastUsedEntryKey);
        }
    }

    private void removeLeastUsedFromHddCache(T leastUsedEntryKey) {
        try {
            // Remove this entry from HDD cache.
            hddCache.removeEntry(leastUsedEntryKey);
            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                logger.info("Entry with key = {} is removed from an HDD cache. ", leastUsedEntryKey);
            }
        } catch (NotPresentException ex) {
            logger.error("!!! HDD cache entry cannot be removed");
            logger.error(ex.getMessage());
        }
    }

    private void moveLeastUsedFromRamToHdd(T key, T leastUsedEntryKey) throws IOException, ClassNotFoundException,
            NotPresentException {
        hddCache.putEntry(leastUsedEntryKey, ramCache.getEntry(leastUsedEntryKey));
//                            hddCache.getMapFrequency().put(leastUsedEntryKey, 0);
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("An entry with key = {} is moved to an HDD cache.", leastUsedEntryKey);
            logger.info("Entry with key = {} is moved to a RAM cache.", key);
            logger.info("");
        }
        // Remove such entry from a RAM cache.
        ramCache.removeEntry(leastUsedEntryKey);
    }

    /**
     * Recache the data in a caches. When data should be moved from an HDD cache, to a RAM cache.
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
     * Swap the least used entry in a RAM cache with a most used entry in an HDD cache.
     *
     * @param keyRAMCache memory cache
     * @param keyHDDCache disk cache
     */
    private void replaceEntries(@NonNull T keyRAMCache, @NonNull T keyHDDCache) {

        // If ram cache object is absent, no sense in replacement.
        if (!ramCache.hasCacheEntry(keyRAMCache)) {
            logger.info("RAM cache has no entry by key = {}. Cache integrity is broken.", keyRAMCache);
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!hddCache.hasCacheEntry(keyHDDCache)) {
            logger.info("HDD cache has no entry by key = {}. Cache integrity is broken.", keyHDDCache);
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
            logger.info("Entry in RAM cache key = {} has been moved to an HDD cache.", keyRAMCache);
            logger.info("Entry in HDD cache key = {} has been moved to a RAM cache.", keyHDDCache);
            logger.info("");
        }
    }

    /**
     * Run a loop that simulates a process of retrieving / caching the data.
     */
    public void performCachingProcess() {
        Object cacheEntry;
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("\n\n<<<--- Data retrieval/caching loop has begun --->>>\n");
        }
        for (int i = 0; i < Integer.parseInt(commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            try {
                cacheEntry = processRequest(cacheFeeder.fetchKey());
                logger.info("Requested entry = {} delivered to a requester.", cacheEntry);
                logger.info("");
            } catch (NotPresentException | ClassNotFoundException | IOException npex) {
                logger.error(npex);
            }
        }
        cacheLoggingUtils.printSummary(ramCache, hddCache, commandLineArguments);
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
