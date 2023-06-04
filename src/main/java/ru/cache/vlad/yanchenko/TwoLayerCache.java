package ru.cache.vlad.yanchenko;

import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsProcessor;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsReader;
import ru.cache.vlad.yanchenko.caches.HDDCache;
import ru.cache.vlad.yanchenko.caches.RAMCache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.test.Testing;
import ru.cache.vlad.yanchenko.utils.ValidatingUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Entry point class.
 *
 * @author v.yanchenko
 */
public class TwoLayerCache {

    private Testing mTest;
    private final Logger mLogger;
    private final CacheProcessor mCacheProcessor;
    private final Map<String, String> mArguments;

    private TwoLayerCache(String[] args) throws IOException {
        mLogger = CacheLoggingUtils.getLogger();
        // Validating program constants
        ValidatingUtils.validateArguments(mLogger);
        // Processing command line arguments.
        CacheArgumentsProcessor argumentsProcessor =
                new CacheArgumentsProcessor(mLogger);
        mArguments = argumentsProcessor.processArguments(
                new CacheArgumentsReader(mLogger).readArguments(args)
        );
        mCacheProcessor = CacheProcessor.getInstance(
                mLogger,
                new RAMCache(mLogger, mArguments),
                new HDDCache(mLogger, mArguments),
                new CacheFeeder(Integer.parseInt(mArguments.get("n"))),
                mArguments
        );
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments says so.
        if (Boolean.parseBoolean(twoLayerCache.mArguments.get("test"))) {
            twoLayerCache.mTest = new Testing(
                    twoLayerCache.mLogger,
                    twoLayerCache.mArguments,
                    twoLayerCache.mCacheProcessor);
            twoLayerCache.mTest.runTesting();
            // Else run a single cache algorithm.
        } else {
            twoLayerCache.mCacheProcessor.performCachingProcess();
        }
    }
}
