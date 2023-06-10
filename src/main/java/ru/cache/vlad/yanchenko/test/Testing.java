package ru.cache.vlad.yanchenko.test;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.CacheKind;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;

/**
 * Class runs a test on all the present cache algorithms.
 *
 * @author v.yanchenko
 */
public class Testing {

    private final Logger mLogger;
    private final Map<String, String> mArguments;
    // Map of objects that is going to be fed to a caching algorithm.
    private Map<Object, Object> mTestingObjects;
    private final CacheProcessor mCacheProcessor;

    /**
     * Public constructor. Provides dependencies and creates an instance of a class.
     *
     * @param logger         to log a testing events
     * @param cacheFeeder    cache data feeder
     * @param arguments      from command line
     * @param cacheProcessor operates the caches
     */
    public Testing(@NonNull Logger logger,
                   @NonNull CacheFeeder cacheFeeder,
                   @NonNull Map<String, String> arguments,
                   @NonNull CacheProcessor cacheProcessor) {
        mLogger = logger;
        mArguments = arguments;
        mTestingObjects = new HashMap<>();
        mCacheProcessor = cacheProcessor;
        // Populating a map for further using it as a template entry set for all the caching algorithms.
        mTestingObjects = cacheFeeder.populateData();
    }

    /**
     * Runs a test on a several test algorithms. Process information is written to a log file.
     */
    public void runTesting() throws NotPresentException, IOException, ClassNotFoundException {

        // Putting all the entries from a testing map to a msp that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));

        // Putting all the entries from a testing map to a map that's going to be fed to a caching pipeline.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(mTestingObjects)
        );

        // Setting a cache kind.
        mArguments.put(CACHE_KIND_ARGUMENT_KEY, CacheKind.LRU.toString());
        for (int i = 0; i < Integer.parseInt(mArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            mCacheProcessor.processRequest(
                    mCacheProcessor.getCacheFeeder().requestObject()
            );
        }
        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mCacheProcessor.getRamCache(), mCacheProcessor.getHddCache(), mArguments);
        try {
            mCacheProcessor.getRamCache().clearCache();
        } catch (IOException ioex) {
            mLogger.error(ioex.getMessage());
        }
        try {
            mCacheProcessor.getHddCache().clearCache();
        } catch (IOException ioex) {
            mLogger.error(ioex.getMessage());
        }

        mCacheProcessor.getRamCache().resetCacheStatistics();
        mCacheProcessor.getHddCache().resetCacheStatistics();
        mLogger.info("");
        mLogger.info("");

        // Putting all the entries from a testing msp to a map that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));

        // Setting a cache kind.
        mArguments.put(CACHE_KIND_ARGUMENT_KEY, CacheKind.MRU.toString());
        for (int i = 0; i < Integer.parseInt(mArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            mCacheProcessor.processRequest(mCacheProcessor.getCacheFeeder().requestObject());
        }

        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mCacheProcessor.getRamCache(), mCacheProcessor.getHddCache(), mArguments);
    }
}
