package ru.cache.vlad.yanchenko.arguments;

/**
 * Command line argument keys
 */
public final class ArgumentsConstants {

    private ArgumentsConstants() {
    }

    /**
     * Argument key for a number of entries to be fed to a cache processor
     */
    public static final String CACHE_ENTRIES_FED_ARGUMENT_KEY = "m";

    /**
     * Argument key for a number of times cache pipeline is to run
     */
    public static final String CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY = "n";

    /**
     * Argument key for a cache kind to be used
     */
    public static final String CACHE_KIND_ARGUMENT_KEY = "ck";

    /**
     * Argument key for a cache process detailed report availability
     */
    public static final String CACHE_DETAILED_REPORT_ARGUMENT_KEY = "dr";

    /**
     * Argument key for a size of level 1 cache
     */
    public static final String LEVEL_1_CACHE_SIZE_ARGUMENT_KEY = "l1s";

    /**
     * Argument key for a size of level 2 cache
     */
    public static final String LEVEL_2_CACHE_SIZE_ARGUMENT_KEY = "l2s";

    /**
     * Argument key for a cache testing
     */
    public static final String CACHE_TEST_ARGUMENT_KEY = "test";
}
