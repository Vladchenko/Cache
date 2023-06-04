package ru.cache.vlad.yanchenko;

import ru.cache.vlad.yanchenko.arguments.ArgumentsProcessor;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.test.Testing;

import java.util.logging.Logger;

/**
 * Entry point class.
 *
 * @author v.yanchenko
 */
public class TwoLayerCache {

    private Testing mTest;
    private final Logger mLogger;
    private final Repository mRepository;
    private final CacheProcessor mCacheProcessor;

    private TwoLayerCache(String[] args) {
        mLogger = CacheLoggingUtils.getLogger();
        mRepository = Repository.getInstance();
        // Processing command line arguments.
        ArgumentsProcessor argumentsProcessor = new ArgumentsProcessor(mLogger, mRepository);
        argumentsProcessor.processArguments(args);
        argumentsProcessor.validateArguments();
        mCacheProcessor = CacheProcessor.getInstance(mLogger, mRepository, new CacheFeeder(mRepository.getEntriesNumber()));
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments says so.
        if (twoLayerCache.mRepository.isTesting()) {
            twoLayerCache.mTest = new Testing(
                    twoLayerCache.mLogger,
                    twoLayerCache.mRepository,
                    twoLayerCache.mCacheProcessor);
            twoLayerCache.mTest.runTesting();
            // Else run a single cache algorithm.
        } else {
            twoLayerCache.mCacheProcessor.performCachingProcess();
        }
    }
}
