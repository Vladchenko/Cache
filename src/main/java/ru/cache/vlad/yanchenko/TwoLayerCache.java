package ru.cache.vlad.yanchenko;

import android.support.annotation.NonNull;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import ru.cache.vlad.yanchenko.arguments.ArgumentsUtils;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.di.CacheComponent;
import ru.cache.vlad.yanchenko.di.CacheModule;
import ru.cache.vlad.yanchenko.di.DaggerCacheComponent;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.utils.CachePopulationUtils;
import ru.cache.vlad.yanchenko.utils.FileUtils;
import ru.cache.vlad.yanchenko.utils.ValidationUtils;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_DETAILED_REPORT_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_TEST_ARGUMENT_KEY;

/**
 * Entry point class.
 *
 * @author v.yanchenko
 */
public class TwoLayerCache<T, V> {

    private final Logger logger;
    private final ICache<T, V> diskCache;
    private final ICache<T, V> memoryCache;
    private final CacheFeeder<T, V> cacheFeeder;
    private final CacheProcessor<T, V> cacheProcessor;
    private final Map<String, String> commandLineArguments;
    private final CachePopulationUtils<T, V> cachePopulationUtils;

    private static CacheComponent daggerCacheComponent;

    /**
     * Public constructor - creates an instance of class and provides dependencies.
     *
     * @param logger    to log events during caches pipeline running
     * @param diskCache     cache for files on a disk
     * @param memoryCache   cache for objects in a RAM
     * @param cacheFeeder   entity that feeds different data to cahces
     * @param cacheProcessor    entity that runs a caches pipeline
     * @param commandLineArguments  arguments form a command line
     * @param cachePopulationUtils  utils to populate caches
     */
    public TwoLayerCache(@NonNull Logger logger,
                         @NonNull ICache<T, V> diskCache,
                         @NonNull ICache<T, V> memoryCache,
                         @NonNull CacheFeeder<T, V> cacheFeeder,
                         @NonNull CacheProcessor<T, V> cacheProcessor,
                         @NonNull Map<String, String> commandLineArguments,
                         @NonNull CachePopulationUtils<T, V> cachePopulationUtils) {
        this.logger = logger;
        this.diskCache = diskCache;
        this.memoryCache = memoryCache;
        this.cacheFeeder = cacheFeeder;
        this.cacheProcessor = cacheProcessor;
        this.commandLineArguments = commandLineArguments;
        this.cachePopulationUtils = cachePopulationUtils;
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        daggerCacheComponent = DaggerCacheComponent
                .builder()
                .cacheModule(new CacheModule(args))
                .build();
        TwoLayerCache twoLayerCache = daggerCacheComponent.getTwoLayerCache();
        twoLayerCache.initCachePipeline();
        twoLayerCache.runCachePipeline();
    }

    private void initCachePipeline() throws NotPresentException, IOException, ClassNotFoundException {
        FileUtils.createDiskCacheFolder(logger);
        ValidationUtils.validateFileConstants(logger);
        populateCachesWithData(logger,
                memoryCache,
                diskCache,
                cacheFeeder,
                cachePopulationUtils);
        // Printing command line arguments if detailed report argument is on
        if (commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY).equals("true")) {
            ArgumentsUtils.printArgs(logger, commandLineArguments);
        }

        runCachePipeline();
    }

    private void runCachePipeline() throws NotPresentException, IOException, ClassNotFoundException {
        // Run a test, if a specific command line arguments say so.
        if (Boolean.parseBoolean(commandLineArguments.get(CACHE_TEST_ARGUMENT_KEY))) {
            daggerCacheComponent.getTesting().runTesting();
        } else { // Else run a single cache algorithm.
            cacheProcessor.performCachingProcess();
        }
    }

    //TODO Move to other class
    private void populateCachesWithData(@NonNull Logger logger,
                                        @NonNull ICache<T, V> memoryCache,
                                        @NonNull ICache<T, V> diskCache,
                                        @NonNull CacheFeeder<T, V> cacheFeeder,
                                        @NonNull CachePopulationUtils<T, V> cachePopulationUtils) {
        try {
            // Populating caches with data from cacheFeeder
            cachePopulationUtils.populateCaches(memoryCache, diskCache, cacheFeeder);
            logger.info("Caches have been populated");
        } catch (IOException exception) {
            logger.error("Cannot populate Disk cache, some IO problem.");
            logger.error(exception);
        }
    }
}
