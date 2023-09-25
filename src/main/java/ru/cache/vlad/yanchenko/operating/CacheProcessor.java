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
import java.util.NoSuchElementException;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Class is in charge of an operations done while a caching process is running.
 *
 * @author v.yanchenko
 */
public class CacheProcessor<T, V> {

    private final Logger logger;
    private final ICache<T, V> memoryCache;
    private final ICache<T, V> diskCache;
    private final CacheFeeder<T, V> cacheFeeder;
    private final Map<String, String> commandLineArguments;
    private final CacheLoggingUtils<T, V> cacheLoggingUtils;

    /**
     * Public constructor - creates an instance of class and provides dependencies.
     *
     * @param logger      to log the events
     * @param memoryCache    memory cache
     * @param diskCache    disk cache
     * @param cacheFeeder cache data feeder
     * @param arguments   command line arguments
     */
    public CacheProcessor(@NonNull Logger logger,
                          @NonNull ICache<T, V> memoryCache,
                          @NonNull ICache<T, V> diskCache,
                          @NonNull CacheFeeder<T, V> cacheFeeder,
                          @NonNull Map<String, String> arguments,
                          @NonNull CacheLoggingUtils<T, V> cacheLoggingUtils) {
        this.logger = logger;
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
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
            cacheLoggingUtils.printCachesContents(logger, memoryCache, diskCache);
            logger.info("");
            logger.info(">>> Requested key = {}", key);
        }

        // If Memory cache has a requested entry,
        if (memoryCache.hasCacheEntry(key)) {
            logger.info("Memory cache hit, key = {}", key);
            // return it to CPU.
            memoryCache.setCacheHits(memoryCache.getCacheHits() + 1);
            return memoryCache.getEntry(key);
        } else {
            // Memory cache miss, 
            memoryCache.setCacheMisses(memoryCache.getCacheMisses() + 1);
            logger.info("! Memory cache miss, key = {}", key);
            try {
                // Retrieve an entry from a disk cache.
                cacheEntry = diskCache.getEntry(key);
                diskCache.setCacheHits(diskCache.getCacheHits() + 1);
                logger.info("Disk cache hit, key = {}", key);
                moveEntryFromDiskToMemoryCacheByKey(key);
                return cacheEntry;
            } catch (NullPointerException | IOException | ClassNotFoundException | NotPresentException ex) {
                logger.info("! Disk cache miss, key = {}", key);
            }
            // When both caches miss,
            if (cacheEntry == null) {
                diskCache.setCacheMisses(diskCache.getCacheMisses() + 1);
                // If Memory cache is not full,
                if (memoryCache.getSize() < memoryCache.getEntriesNumber()) {
                    // Download a requested data from a mock source.
                    cacheEntry = cacheFeeder.deliverCacheEntry(key);
                    // Add a newly downloaded entry to a Memory cache.
                    memoryCache.putEntry(key, cacheEntry);
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Entry with key = {}, is added to a Memory cache.", key);
                    }
                    return cacheEntry;
                } else {    // Memory cache is full, it needs an eviction.
                    if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                        logger.info("Both caches miss. Memory cache is full, performing an eviction.");
                    }
                    /*
                     * Find the least used entry in a Memory cache and move it to a disk cache and if Disk cache is full,
                     * remove the least used one. Then write to a Memory cache a new entry.
                     */
                    if (diskCache.getSize() < diskCache.getEntriesNumber()) {
                        // Get the least used entry in a Memory cache.
                        T leastUsedEntryKey = memoryCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        // Move the least used Memory entry to a disk cache.
                        try {
                            moveLeastUsedFromMemoryToDisk(key, leastUsedEntryKey);
                            // Download a requested data from a mock source.
                            cacheEntry = cacheFeeder.deliverCacheEntry(key);
                            // Add a newly downloaded entry to a Memory cache.
                            memoryCache.putEntry(key, cacheEntry);
                            return cacheEntry;
                        } catch (IOException | NotPresentException ex) {
                            logger.info("!!! Some major trouble with moving a least used entry and appending a new one.");
                        }
                    } else {
                        /*
                         * When all the caches are full and new entry is downloaded, remove the least used entry
                         * from a disk cache, then move the least used Memory cache entry to a disk cache and
                         * write a new entry to Memory cache.
                         */
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("Disk cache is full, removing a least used entry.");
                        }
                        // Get the least used entry in a disk cache
                        T leastUsedEntryKey = diskCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        removeLeastUsedFromDiskCache(leastUsedEntryKey);
                        // Get the least used entry in a Memory cache.
                        leastUsedEntryKey = memoryCache.getLeastUsedEntryKey(
                                CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY)));
                        moveLeastUsedMemoryEntryToDisk(leastUsedEntryKey);
                        // Download a requested data from a mock source.
                        cacheEntry = cacheFeeder.deliverCacheEntry(key);
                        // Add a newly downloaded entry to a Memory cache.
                        memoryCache.putEntry(key, cacheEntry);
                        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                            logger.info("New entry with key = {}, is added to a Memory cache.", key);
                        }
                    }
                }
            }
        }

        // Fetch a requested entry.
        return cacheEntry;
    }

    private void moveLeastUsedMemoryEntryToDisk(T leastUsedEntryKey) throws ClassNotFoundException, NotPresentException {
        try {
            // Move the least used Memory entry to Disk cache.
            diskCache.putEntry(leastUsedEntryKey, memoryCache.getEntry(leastUsedEntryKey));
//                            diskCache.getMapFrequency().put(leastUsedEntryKey, 0);
            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                logger.info("Least used Memory cache entry with key = {}, is moved to a disk cache. ",
                        leastUsedEntryKey);
            }
        } catch (IOException ex) {
            logger.info("!!! Cannot move to Disk cache ! Disk drive might be corrupt.");
            logger.error(ex.getMessage());
        }
        // Remove the least used entry from a Memory cache.
        memoryCache.removeEntry(leastUsedEntryKey);
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("Least used Memory cache entry with key = {}, is removed.", leastUsedEntryKey);
        }
    }

    private void removeLeastUsedFromDiskCache(T leastUsedEntryKey) {
        try {
            // Remove this entry from Disk cache.
            diskCache.removeEntry(leastUsedEntryKey);
            if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
                logger.info("Entry with key = {} is removed from a disk cache. ", leastUsedEntryKey);
            }
        } catch (NotPresentException ex) {
            logger.error("!!! Disk cache entry cannot be removed");
            logger.error(ex.getMessage());
        }
    }

    private void moveLeastUsedFromMemoryToDisk(T key, T leastUsedEntryKey) throws IOException, ClassNotFoundException,
            NotPresentException {
        diskCache.putEntry(leastUsedEntryKey, memoryCache.getEntry(leastUsedEntryKey));
//                            diskCache.getMapFrequency().put(leastUsedEntryKey, 0);
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("An entry with key = {} is moved to a disk cache.", leastUsedEntryKey);
            logger.info("Entry with key = {} is moved to a Memory cache.", key);
            logger.info("");
        }
        // Remove such entry from a Memory cache.
        memoryCache.removeEntry(leastUsedEntryKey);
    }

    /**
     * Recache the data in a caches. When data should be moved from a disk cache, to a Memory cache.
     *
     * @param key to get an object by
     */
    private void moveEntryFromDiskToMemoryCacheByKey(@NonNull T key) {
        switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
            case LFU -> {
                /*
                 * If there was a disk cache hit, then check if there is any
                 * object that was requested more times than any of the Memory
                 * cache object. If so, replace them.
                 *
                 * Memory cache - defined key. Disk cache - defined key.
                 */
            }
            case LRU ->
                    /*
                     * If there was a disk cache hit, then move this entry to a Memory cache, and before that,
                     * define the least used one in a Memory cache and move it back to Disk cache.
                     *
                     * Memory cache - first key in a map; Disk cache - requested key;
                     */
                    replaceEntries(memoryCache.getCacheEntries().entrySet().iterator().next().getKey(), key);
            case MRU ->
                    /*
                     * If there was a disk cache hit, then move this entry to a Memory cache, and before that,
                     * define the least used one in a Memory cache and move it back to Disk cache.
                     *
                     * Memory cache - keyLastAccessed; Disk cache - requested key;
                     */
                    replaceEntries(memoryCache.getKeyLastAccessed(), key);
            default -> {
                // TODO
            }
        }
    }

    /**
     * Swap the least used entry in a memory cache with a most used entry in a disk cache.
     *
     * @param memoryCacheKey memory cache
     * @param diskCacheKey disk cache
     */
    private void replaceEntries(@NonNull T memoryCacheKey, @NonNull T diskCacheKey) {

        // If ram cache object is absent, no sense in replacement.
        if (!memoryCache.hasCacheEntry(memoryCacheKey)) {
            logger.info("Memory cache has no entry with key = {}. Cache integrity is broken.", memoryCacheKey);
            return;
        }

        // If hdd cache object is absent, no sense in replacement.
        if (!diskCache.hasCacheEntry(diskCacheKey)) {
            logger.info("Disk cache has no entry with key = {}. Cache integrity is broken.", diskCacheKey);
            return;
        }

        try {
            diskCache.putEntry(memoryCacheKey, memoryCache.getEntry(memoryCacheKey));
            memoryCache.removeEntry(memoryCacheKey);
            memoryCache.putEntry(diskCacheKey, diskCache.getEntry(diskCacheKey));
            diskCache.removeEntry(diskCacheKey);
        } catch (NotPresentException ex) {
            logger.info("Cannot recache, such entry is absent. Cache integrity is broken.");
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            logger.info("Cannot recache, file or class not found. Cache integrity is broken.");
        } catch (IOException ex) {
            logger.info("Cannot recache, some IO problem. Cache integrity is broken.");
        }

        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY))) {
            logger.info("Recaching has been done.");
            logger.info("Entry in memory cache with key = {} has been moved to a disk cache.", memoryCacheKey);
            logger.info("Entry in disk cache with key = {} has been moved to a memory cache.", diskCacheKey);
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
            } catch (NoSuchElementException | NotPresentException | ClassNotFoundException | IOException npex) {
                logger.error(npex);
            }
        }
        cacheLoggingUtils.printSummary(logger, memoryCache, diskCache, commandLineArguments);
    }

    //<editor-fold defaultstate="collapsed" desc="getters">

    /**
     * @return the memoryCache
     */
    public ICache<T, V> getMemoryCache() {
        return memoryCache;
    }

    /**
     * @return the diskCache
     */
    public ICache<T, V> getDiskCache() {
        return diskCache;
    }

    /**
     * @return the cacheFeeder
     */
    public CacheFeeder<T, V> getCacheFeeder() {
        return cacheFeeder;
    }
    //</editor-fold>
}
