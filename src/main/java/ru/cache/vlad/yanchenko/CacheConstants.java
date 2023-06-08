package ru.cache.vlad.yanchenko;

import ru.cache.vlad.yanchenko.caches.CacheKind;

/**
 * Constants for the program.
 */
public final class CacheConstants {

    private CacheConstants() {
    }

    /**
     * Number of entries that RAM cache can hold.
     */
    public static final int DEFAULT_RAM_CACHE_ENTRIES = 10;
    /**
     * Number of entries that HDD cache can hold.
     */
    public static final int DEFAULT_HDD_CACHE_ENTRIES = 10;
    /**
     * Minimum number of entries that RAM cache can hold.
     */
    public static final int MINIMUM_RAM_CACHE_ENTRIES = 1;
    /**
     * Minimum number of entries that HDD cache can hold.
     */
    public static final int MINIMUM_HDD_CACHE_ENTRIES = 1;
    /**
     * Number of entries to be fed to a cache processor.
     */
    public static final int DEFAULT_CACHE_ENTRIES_NUMBER = 25;
    /**
     * Number of runs, cache pipeline is to perform.
     */
    public static final int DEFAULT_PIPELINE_RUNS_NUMBER = 100;
    /**
     * Number of runs, cache pipeline is to perform.
     */
    public static final CacheKind DEFAULT_CACHE_KIND = CacheKind.LRU;
}
