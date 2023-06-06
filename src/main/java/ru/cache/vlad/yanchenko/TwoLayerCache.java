package ru.cache.vlad.yanchenko;

import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsProcessor;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParser;
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

import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_ENTRIES_FED_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.utils.CachePopulationUtils.populateCaches;

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
        CacheArgumentsProcessor argumentsProcessor = new CacheArgumentsProcessor(mLogger);
        mArguments = argumentsProcessor.processArguments(new CacheArgumentsParser(mLogger).parseCommandLineArguments(args));
        ICache ramCache = new RAMCache(mLogger, mArguments);
        try {
            FileUtils.createFilesFolder(mLogger);
        } catch (DirectoryException e) {
            e.printStackTrace();
        }
        ICache hddCache = new HDDCache(mLogger, mArguments);
        CacheFeeder cacheFeeder = new CacheFeeder(Integer.parseInt(mArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)));
        populateCaches(mLogger, ramCache, hddCache, cacheFeeder);
        mCacheProcessor = CacheProcessor.getInstance(mLogger, ramCache, hddCache, cacheFeeder, mArguments);
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
