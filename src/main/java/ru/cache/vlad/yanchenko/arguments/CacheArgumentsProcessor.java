package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;

import java.util.Locale;
import java.util.Map;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_ENTRIES_FED_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.CacheConstants.DEFAULT_PIPELINE_RUNS_NUMBER;
import static ru.cache.vlad.yanchenko.caches.CacheKind.*;

/**
 * Command line arguments processor.
 *
 * @author v.yanchenko
 */
public class CacheArgumentsProcessor {

    private final Logger mLogger;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger to log the events
     */
    public CacheArgumentsProcessor(@NonNull Logger logger) {
        mLogger = logger;
    }

    /**
     * Putting an args and couples of "key=value" to a map
     *
     * @param arguments from command line
     */
    public Map<String, String> processArguments(@NonNull Map<String, String> arguments) {
        processDetailedReportArgument(arguments);
        processRamCacheSizeArgument(arguments);
        processHddCacheSize(arguments);
        processCacheKind(arguments);
        processAccompanyingArguments(arguments);
        return arguments;
    }

    private void processDetailedReportArgument(Map<String, String> arguments) {
        if (arguments.containsKey("dr")) {
            mLogger.info(">>> Caching process report is set to be detailed.");
            CacheLoggingUtils.printArgs(arguments);
        } else {
            mLogger.info(">>> Caching process report is set to be not detailed.");
        }
    }

    private void processRamCacheSizeArgument(Map<String, String> arguments) {
        if (arguments.containsKey("l1s")) {
            try {
                int cacheSize = Integer.parseInt(arguments.get("l1s"));
                if (cacheSize < CacheConstants.RAM_CACHE_ENTRIES_MINIMUM) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfex) {
                mLogger.info("Level 1 cache size is wrong, using default - "
                        + CacheConstants.DEFAULT_RAM_CACHE_ENTRIES);
                arguments.put("l1s", Integer.toString(CacheConstants.DEFAULT_RAM_CACHE_ENTRIES));
            }
        } else {
            mLogger.info("Level 1 cache size is not set, using default - "
                    + CacheConstants.DEFAULT_RAM_CACHE_ENTRIES);
            arguments.put("l1s", Integer.toString(CacheConstants.DEFAULT_RAM_CACHE_ENTRIES));
        }
    }

    private void processHddCacheSize(Map<String, String> arguments) {
        if (arguments.containsKey("l2s")) {
            try {
                int cacheSize = Integer.parseInt(arguments.get("l2s"));
                if (cacheSize < CacheConstants.HDD_CACHE_ENTRIES_MINIMUM) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfex) {
                mLogger.info("Level 2 cache size is wrong, using default - "
                        + CacheConstants.DEFAULT_HDD_CACHE_ENTRIES);
                arguments.put("l2s", Integer.toString(CacheConstants.DEFAULT_HDD_CACHE_ENTRIES));
            }
        } else {
            mLogger.info("Level 2 cache size is not set, using default - "
                    + CacheConstants.DEFAULT_HDD_CACHE_ENTRIES);
            arguments.put("l2s", Integer.toString(CacheConstants.DEFAULT_HDD_CACHE_ENTRIES));
        }
    }

    private void processCacheKind(@NonNull Map<String, String> arguments) {
        if (arguments.get("cachekind") == null
                && arguments.get("ck") == null) {
            mLogger.info("Cache kind is not set, used default - Most Recently Used.");
            arguments.put("cachekind", String.valueOf(MRU));
        } else {
            String ck = arguments.get("cachekind");
            if (ck == null) {
                ck = arguments.get("ck");
            }
            switch (valueOf(ck.toUpperCase(Locale.ROOT))) {
                case LFU -> mLogger.info("cachekind is set to - " + LFU);
                case LRU -> mLogger.info("cachekind is set to - " + LRU);
                case MRU -> mLogger.info("cachekind is set to - " + MRU);
                default -> mLogger.info("cachekind is set to - " + MRU);
            }
        }
    }

    private void processAccompanyingArguments(@NonNull Map<String, String> arguments) {
        // Defining how many entries will be fed to a caching process.
        try {
            mLogger.info("Entries number is set to " + Integer.parseInt(arguments.get(CACHE_ENTRIES_FED_ARGUMENT_KEY)));
        } catch (Exception nfe) {
            mLogger.info("Entries number is not set, using default - " + CacheConstants.DEFAULT_CACHE_ENTRIES_NUMBER);
            arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, Integer.toString(CacheConstants.DEFAULT_CACHE_ENTRIES_NUMBER));
        }

        // Defining a cache process running times, i.e. how many times a caching process is to run.
        try {
            String number = arguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY);
            Integer.parseInt(number);
            arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, number);
            mLogger.info("Cache process will run for " + number + " times");
        } catch (NumberFormatException nfex) {
            mLogger.info("Cache process run times is not set, using default - " + DEFAULT_PIPELINE_RUNS_NUMBER);
            arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, Integer.toString(DEFAULT_PIPELINE_RUNS_NUMBER));
        }
        mLogger.info("");
    }
}
