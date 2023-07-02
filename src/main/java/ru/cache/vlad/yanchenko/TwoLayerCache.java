package ru.cache.vlad.yanchenko;

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
import ru.cache.vlad.yanchenko.operating.CacheUtils;
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
    private Map<String, String> commandLineArguments = null;

    private TwoLayerCache(String[] args) {
        logger = CacheLoggingUtils.getLogger();
        // Validating file constants
        ValidationUtils.validateFileConstants(logger);
        CacheArgumentsValidatorImpl argumentsValidator = new CacheArgumentsValidatorImpl(logger);
        CacheArgumentsParser argumentsParser = new CacheArgumentsParserImpl();
        CachesFactory<T, V> cachesFactory = new CachesFactory<>();

        try {
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

            try {
                // Populating caches with data from cacheFeeder
                new CachePopulationUtils<T, V>().populateCaches(ramCache, hddCache, cacheFeeder);
                logger.info("Caches have been populated");
            } catch (IOException exception) {
                logger.error("Cannot populate HDD cache, some IO problem.");
                logger.error(exception);
            }

            // Run caching process
            cacheProcessor = new CacheProcessor<>(logger, ramCache, hddCache, cacheFeeder, new CacheUtils<>(), commandLineArguments);

        } catch (ParseException e) {
            logger.error("Some trouble with arguments: " + e.getMessage());
        }
    }

    /**
     * Program entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotPresentException, IOException, ClassNotFoundException {
        Testing test;
        TwoLayerCache twoLayerCache = new TwoLayerCache(args);
        // Run a test, if a specific command line arguments says so.
        if (Boolean.parseBoolean((String) twoLayerCache.commandLineArguments.get(CACHE_TEST_ARGUMENT_KEY))) {
            test = new Testing(
                    twoLayerCache.logger,
                    twoLayerCache.cacheFeeder,
                    twoLayerCache.commandLineArguments,
                    twoLayerCache.cacheProcessor);
            test.runTesting();
        } else { // Else run a single cache algorithm.
            twoLayerCache.cacheProcessor.performCachingProcess();
        }
    }
}
