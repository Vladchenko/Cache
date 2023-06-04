package ru.cache.vlad.yanchenko;

/** Constants for the program. */
public class CacheConstants {
    private CacheConstants() { }
    /** Number of entries that RAM cache can hold. */
    public static final int RAM_CACHE_ENTRIES_DEFAULT = 10;
    /** Number of entries that HDD cache can hold. */
    public static final int HDD_CACHE_ENTRIES_DEFAULT = 10;
    /** Minimum number of entries that RAM cache can hold. */
    public static final int RAM_CACHE_ENTRIES_MINIMUM = 1;
    /** Minimum number of entries that HDD cache can hold. */
    public static final int HDD_CACHE_ENTRIES_MINIMUM = 1;
    /** Number of entries to be fed to a cache processor. */
    public static final int ENTRIES_NUMBER_DEFAULT = 25;
    /** Number of runs, cache pipeline is run. */
    public static final int PIPELINE_RUNS_NUMBER_DEFAULT = 100;
    /** File prefix for files to be cached */
    public static final String FILE_PREFIX = "cache_file_";
    /** File extension for files to be cached */
    public static final String FILE_EXTENSION = ".cache";
    /** Folder for a files that represents a 2nd level cache (HDD cache) */
    public static final String FILES_FOLDER = "Cache Data\\";
}
