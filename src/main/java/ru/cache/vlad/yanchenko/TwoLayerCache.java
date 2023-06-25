package ru.cache.vlad.yanchenko;

import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.arguments.ArgumentsUtils;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParserImpl;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsValidatorImpl;
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
import ru.cache.vlad.yanchenko.utils.ValidationUtils;

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

    private Testing test;
    private final Logger logger;
    private final CacheFeeder cacheFeeder;
    private final CacheProcessor cacheProcessor;
    private final Map<String, String> commandLineArguments;

    private TwoLayerCache(String[] args) {
        logger = CacheLoggingUtils.getLogger();

        // Validating file constants
        ValidationUtils.validateFileConstants(logger);

        // Processing command line arguments
        CacheArgumentsValidatorImpl argumentsValidator = new CacheArgumentsValidatorImpl(logger);

        // Validating command line arguments
        commandLineArguments = argumentsValidator.validateCommandLineArguments(
                new CacheArgumentsParserImpl(logger).parseCommandLineArguments(args)
        );

        // Printing command line arguments if detailed report argument is on
        if (commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY).equals("true")) {
            ArgumentsUtils.printArgs(logger, commandLineArguments);
        }

        // Creating RAM cache
        ICache ramCache = new RAMCache(commandLineArguments);

        // Creating HDD cache folder, if needed
        try {
            FileUtils.createFilesFolder(logger);
        } catch (DirectoryException e) {
            e.printStackTrace();
        }

        // Creating HDD cache
        ICache hddCache = new HDDCache(commandLineArguments);

        // Creating cache feeder to fetch cache data to caches
        cacheFeeder = new CacheFeeder(
                Integer.parseInt(
                        commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)
                )
        );

        try {
            // Populating caches with data from cacheFeeder
            populateCaches(ramCache, hddCache, cacheFeeder);
            logger.info("Caches have been populated");
        } catch (IOException exception) {
            logger.error("Cannot populate HDD cache, some IO problem.");
            logger.error(exception);
        }

        // Run caching process
        cacheProcessor = CacheProcessor.getInstance(logger, ramCache, hddCache, cacheFeeder, commandLineArguments);
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments says so.
        if (Boolean.parseBoolean(twoLayerCache.commandLineArguments.get(CACHE_TEST_ARGUMENT_KEY))) {
            twoLayerCache.test = new Testing(
                    twoLayerCache.logger,
                    twoLayerCache.cacheFeeder,
                    twoLayerCache.commandLineArguments,
                    twoLayerCache.cacheProcessor);
            twoLayerCache.test.runTesting();
        } else { // Else run a single cache algorithm.
            twoLayerCache.cacheProcessor.performCachingProcess();
        }
    }
}
