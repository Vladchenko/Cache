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
public class Testing<T, V> {

    private final Logger logger;
    // Map of objects that is going to be fed to a caching algorithm.
    private Map<T, V> testingObjects;
    private final CacheProcessor<T, V> cacheProcessor;
    private final CacheLoggingUtils<T, V> cacheLoggingUtils;
    private final Map<String, String> commandLineArguments;

    /**
     * Public constructor. Provides dependencies and creates an instance of a class.
     *
     * @param logger            to log a caching events
     * @param cacheFeeder       cache data feeder
     * @param arguments         from command line
     * @param cacheProcessor    that operates the caches
     * @param cacheLoggingUtils to print summary
     */
    public Testing(@NonNull Logger logger,
                   @NonNull CacheFeeder<T, V> cacheFeeder,
                   @NonNull Map<String, String> arguments,
                   @NonNull CacheProcessor<T, V> cacheProcessor,
                   @NonNull CacheLoggingUtils<T, V> cacheLoggingUtils) {
        this.logger = logger;
        commandLineArguments = arguments;
        testingObjects = new HashMap<>();
        this.cacheProcessor = cacheProcessor;
        // Populating a map for further using it as a template entry set for all the caching algorithms.
        testingObjects = cacheFeeder.populateMap();
        this.cacheLoggingUtils = cacheLoggingUtils;
    }

    /**
     * Runs a test on a several test algorithms. Process information is written to a log file.
     */
    public void runTesting() throws NotPresentException, IOException, ClassNotFoundException {

        // Putting all the entries from a testing map to a msp that's going to be fed to a caching algorithm.
        cacheProcessor.getCacheFeeder().setKeysToObjectsMap(
                cacheProcessor.getCacheFeeder().copyData(testingObjects));

        // Putting all the entries from a testing map to a map that's going to be fed to a caching pipeline.
        cacheProcessor.getCacheFeeder().setKeysToObjectsMap(
                cacheProcessor.getCacheFeeder().copyData(testingObjects)
        );

        // Setting a cache kind.
        commandLineArguments.put(CACHE_KIND_ARGUMENT_KEY, CacheKind.LRU.toString());
        for (int i = 0; i < Integer.parseInt(commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            cacheProcessor.processRequest(
                    cacheProcessor.getCacheFeeder().fetchKey()
            );
        }
        // Printing a summary for a current caching process.
        cacheLoggingUtils.printSummary(
                logger,
                cacheProcessor.getRamCache(),
                cacheProcessor.getHddCache(),
                commandLineArguments);
        try {
            cacheProcessor.getRamCache().clearCache();
        } catch (IOException ioex) {
            logger.error(ioex.getMessage());
        }
        try {
            cacheProcessor.getHddCache().clearCache();
        } catch (IOException ioex) {
            logger.error(ioex.getMessage());
        }

        cacheProcessor.getRamCache().resetCacheStatistics();
        cacheProcessor.getHddCache().resetCacheStatistics();
        logger.info("");
        logger.info("");

        // Putting all the entries from a testing msp to a map that's going to be fed to a caching algorithm.
        cacheProcessor.getCacheFeeder().setKeysToObjectsMap(
                cacheProcessor.getCacheFeeder().copyData(testingObjects));

        // Setting a cache kind.
        commandLineArguments.put(CACHE_KIND_ARGUMENT_KEY, CacheKind.MRU.toString());

        // Run cache pipeline.
        for (int i = 0; i < Integer.parseInt(commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)); i++) {
            cacheProcessor.processRequest(cacheProcessor.getCacheFeeder().fetchKey());
        }

        // Printing a summary for a current caching process.
        cacheLoggingUtils.printSummary(
                logger,
                cacheProcessor.getRamCache(),
                cacheProcessor.getHddCache(),
                commandLineArguments);
    }
}
