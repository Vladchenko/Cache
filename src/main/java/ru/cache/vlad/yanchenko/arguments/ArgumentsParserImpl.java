package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Logger;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Cache arguments parser
 */
public class ArgumentsParserImpl implements ArgumentsParser {

    private final Logger mLogger;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger logger to log the events
     */
    public ArgumentsParserImpl(@NonNull Logger logger) {
        mLogger = logger;
    }

    @Override
    public CommandLine parseCommandLineArguments(String[] args) {
        Options options = new Options();
        defineCommandLineOptions(options);
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            mLogger.error(e.getMessage());
            System.exit(1);
        }
        return commandLine;
    }

    private void defineCommandLineOptions(Options options) {
        options.addOption(CACHE_ENTRIES_FED_ARGUMENT_KEY, true, "Number of entries to be fed to a cache processor");
        options.addOption(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, true, "Number of times cache pipeline is to run");
        options.addOption(CACHE_KIND_ARGUMENT_KEY, true, "Cache kind - LRU/MRU/LFU");
        options.addOption(CACHE_DETAILED_REPORT_ARGUMENT_KEY, false, "If detailed report on cache operating should be provided");
        options.addOption(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY, true, "RAM cache size");
        options.addOption(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY, true, "HDD cache size");
        options.addOption(CACHE_TEST_ARGUMENT_KEY, false, "If cache test run to be performed");
    }
}
