package ru.cache.vlad.yanchenko;

import android.support.annotation.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.arguments.ArgumentsUtils;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParser;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParserImpl;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsValidatorImpl;
import ru.cache.vlad.yanchenko.caches.CacheType;
import ru.cache.vlad.yanchenko.caches.CachesFactory;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.test.Testing;
import ru.cache.vlad.yanchenko.utils.CachePopulationUtils;
import ru.cache.vlad.yanchenko.utils.FileUtils;
import ru.cache.vlad.yanchenko.utils.ValidationUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Entry point class.
 *
 * @author v.yanchenko
 */
public class TwoLayerCache<T, V> {

    private final Logger logger;
    private CacheFeeder<T, V> cacheFeeder;
    private CacheProcessor<T, V> cacheProcessor;
    private final CacheLoggingUtils<T, V> cacheLoggingUtils;
    private Map<String, String> commandLineArguments = null;

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        Testing test;
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments say so.
        if (Boolean.parseBoolean((String) twoLayerCache.commandLineArguments.get(CACHE_TEST_ARGUMENT_KEY))) {
            test = new Testing(
                    twoLayerCache.logger,
                    twoLayerCache.cacheFeeder,
                    twoLayerCache.commandLineArguments,
                    twoLayerCache.cacheProcessor,
                    twoLayerCache.cacheLoggingUtils);
            test.runTesting();
        } else { // Else run a single cache algorithm.
            twoLayerCache.cacheProcessor.performCachingProcess();
        }
    }

    private TwoLayerCache(@NonNull String[] args) {
        logger = CacheLoggingUtils.getLogger();
        cacheLoggingUtils = new CacheLoggingUtils<>();
        // Validating file constants
        ValidationUtils.validateFileConstants(logger);
        CachesFactory<T, V> cachesFactory = new CachesFactory<>();
        CacheArgumentsParser argumentsParser = new CacheArgumentsParserImpl();
        CacheArgumentsValidatorImpl argumentsValidator = new CacheArgumentsValidatorImpl(logger);

        try {
            processCommandLineArguments(args, argumentsParser, argumentsValidator);

            // Creating RAM cache
            ICache<T, V> ramCache = cachesFactory.createCache(CacheType.RAM, commandLineArguments);

            // Creating HDD cache folder, if needed
            try {
                FileUtils.createHddCacheFolder(logger);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            // Creating HDD cache
            ICache<T, V> hddCache = cachesFactory.createCache(CacheType.HDD, commandLineArguments);

            // Creating cache feeder to fetch cache data to caches
            cacheFeeder = new CacheFeeder<>(
                    Integer.parseInt(
                            commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)
                    )
            );

            populateCachesWithData(ramCache, hddCache);

            // Run caching process
            cacheProcessor = new CacheProcessor<>(
                    logger,
                    ramCache,
                    hddCache,
                    cacheFeeder,
                    commandLineArguments,
                    new CacheLoggingUtils<>());

        } catch (ParseException e) {
            logger.error("Some trouble with arguments: {}", e.getMessage());
        }
    }

    private void populateCachesWithData(ICache<T, V> ramCache, ICache<T, V> hddCache) {
        try {
            // Populating caches with data from cacheFeeder
            new CachePopulationUtils<T, V>().populateCaches(ramCache, hddCache, cacheFeeder);
            logger.info("Caches have been populated");
        } catch (IOException exception) {
            logger.error("Cannot populate HDD cache, some IO problem.");
            logger.error(exception);
        }
    }

    private void processCommandLineArguments(String[] args, CacheArgumentsParser argumentsParser, CacheArgumentsValidatorImpl argumentsValidator) throws ParseException {
        Optional<CommandLine> commandLineOpt = argumentsParser.parse(args);
        if (commandLineOpt.isPresent()) {
            CommandLine commandLine = commandLineOpt.get();
            // Validating command line arguments
            commandLineArguments = argumentsValidator.validateCommandLineArguments(commandLine);
        }

        // Printing command line arguments if detailed report argument is on
        if (commandLineArguments.get(CACHE_DETAILED_REPORT_ARGUMENT_KEY).equals("true")) {
            ArgumentsUtils.printArgs(logger, commandLineArguments);
        }
    }

}
