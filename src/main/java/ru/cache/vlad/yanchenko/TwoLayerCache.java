package ru.cache.vlad.yanchenko;

import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.arguments.ArgumentsParserImpl;
import ru.cache.vlad.yanchenko.arguments.ArgumentsUtils;
import ru.cache.vlad.yanchenko.arguments.ArgumentsValidatorImpl;
import ru.cache.vlad.yanchenko.caches.HDDCache;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.caches.RAMCache;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.test.Testing;
import ru.cache.vlad.yanchenko.utils.FileUtils;
import ru.cache.vlad.yanchenko.utils.ValidatingUtils;

import java.io.IOException;
import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;
import static ru.cache.vlad.yanchenko.operating.CacheUtils.populateCaches;

/**
 * Entry point class.
 *
 * @author v.yanchenko
 */
public class TwoLayerCache {

    private Testing mTest;
    private final Logger mLogger;
    private final CacheFeeder mCacheFeeder;
    private final CacheProcessor mCacheProcessor;
    private final Map<String, String> mArguments;

    private TwoLayerCache(String[] args) {
        mLogger = CacheLoggingUtils.getLogger();

        // Validating file constants
        ValidatingUtils.validateFileConstants(mLogger);

        // Processing command line arguments
        ArgumentsValidatorImpl argumentsValidator = new ArgumentsValidatorImpl(mLogger);

        // Validating command line arguments
        mArguments = argumentsValidator.validateCommandLineArguments(
                new ArgumentsParserImpl(mLogger).parseCommandLineArguments(args)
        );

        // Printing command line arguments if detailed report argument is on
        if (mArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY).equals("true")) {
            ArgumentsUtils.printArgs(mLogger, mArguments);
        }

        // Creating RAM cache
        ICache ramCache = new RAMCache(mArguments);

        // Creating HDD cache folder, if needed
        try {
            FileUtils.createFilesFolder(mLogger);
        } catch (DirectoryException e) {
            e.printStackTrace();
        }

        // Creating HDD cache
        ICache hddCache = new HDDCache(mArguments);

        // Creating cache feeder to fetch cache data to caches
        mCacheFeeder = new CacheFeeder(
                Integer.parseInt(
                        mArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)
                )
        );

        try {
            // Populating caches with data from cacheFeeder
            populateCaches(ramCache, hddCache, mCacheFeeder);
            mLogger.info("Caches have been populated");
        } catch (IOException exception) {
            mLogger.error("Cannot populate HDD cache, some IO problem.");
            mLogger.error(exception);
        }

        // Run caching process
        mCacheProcessor = CacheProcessor.getInstance(mLogger, ramCache, hddCache, mCacheFeeder, mArguments);
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments says so.
        if (Boolean.parseBoolean(twoLayerCache.mArguments.get(CACHE_TEST_ARGUMENT_KEY))) {
            twoLayerCache.mTest = new Testing(
                    twoLayerCache.mLogger,
                    twoLayerCache.mCacheFeeder,
                    twoLayerCache.mArguments,
                    twoLayerCache.mCacheProcessor);
            twoLayerCache.mTest.runTesting();
        } else { // Else run a single cache algorithm.
            twoLayerCache.mCacheProcessor.performCachingProcess();
        }
    }
}
