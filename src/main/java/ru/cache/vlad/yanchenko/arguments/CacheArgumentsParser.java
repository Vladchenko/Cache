package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.caches.CacheKind;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.*;
import static ru.cache.vlad.yanchenko.CacheConstants.*;

/**
 * Cache arguments parser
 */
public class CacheArgumentsParser implements ICacheArgumentsParser {

    private final Logger mLogger;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger logger to log the events
     */
    public CacheArgumentsParser(@NonNull Logger logger) {
        mLogger = logger;
    }

    @Override
    public Map<String, String> parseCommandLineArguments(String[] args) {
        Options options = new Options();
        defineCommandLineOptions(options);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            mLogger.error(e.getMessage());
            System.exit(1);
        }
        Map<String, String> arguments = new HashMap<>();

        parseTestArgument(cmd, arguments);
        parseDetailedReportArgument(cmd, arguments);
        parseMaximumCacheEntriesArgument(cmd, arguments);
        parseCachingProcessRunTimesArgument(cmd, arguments);
        parseCacheKindArgument(cmd, arguments);
        if (arguments.get("dr").equals("true")) {
            printArguments(arguments);
        }
        return arguments;
    }

    private void printArguments(Map<String, String> arguments) {
        mLogger.info("Command line arguments are: ");
        mLogger.info(arguments);
    }

    private void defineCommandLineOptions(Options options) {
        options.addOption(CACHE_ENTRIES_FED_ARGUMENT_KEY, true, "Number of entries to be fed to a cache processor");
        options.addOption(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, true, "Number of times cache pipeline is to run");
        options.addOption(CACHE_KIND_ARGUMENT_KEY, true, "Cache kind - LRU/MRU");
        options.addOption("dr", false, "If detailed report on cache operating should be provided");
        options.addOption("l1s", true, "RAM cache maximum size");
        options.addOption("l2s", true, "HDD cache maximum size");
        options.addOption("test", false, "If cache test run to be performed");
    }

    private void parseCacheKindArgument(CommandLine cmd, Map<String, String> arguments) {
        String cacheKind = "";
        if (cmd.hasOption(CACHE_KIND_ARGUMENT_KEY)) {
            if (EnumUtils.isValidEnum(CacheKind.class, cmd.getOptionValue(CACHE_KIND_ARGUMENT_KEY).toUpperCase(Locale.ROOT))) {
                cacheKind = cmd.getOptionValue(CACHE_KIND_ARGUMENT_KEY);
            } else {
                mLogger.info("Command line argument for cache kind is wrong, using default = " + DEFAULT_CACHE_KIND);
                cacheKind = DEFAULT_CACHE_KIND.toString();
            }
        } else {
            mLogger.info("Command line argument for cache kind is not set, using default = " + DEFAULT_CACHE_KIND);
            cacheKind = DEFAULT_CACHE_KIND.toString();
        }
        arguments.put(CACHE_KIND_ARGUMENT_KEY, cacheKind.toUpperCase(Locale.ROOT));
    }

    private void parseCachingProcessRunTimesArgument(CommandLine cmd, Map<String, String> arguments) {
        if (cmd.hasOption(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)) {
            try {
                if (Integer.parseInt(cmd.getOptionValue(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)) < DEFAULT_PIPELINE_RUNS_NUMBER) {
                    mLogger.error("Cache process run times command line argument is small, using default = "
                            + DEFAULT_PIPELINE_RUNS_NUMBER);
                    arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, String.valueOf(DEFAULT_PIPELINE_RUNS_NUMBER));
                } else {
                    arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, cmd.getOptionValue(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY));
                }
            } catch (NumberFormatException mfex) {
                mLogger.error("Command line argument for cache process run times is wrong, using default = "
                        + DEFAULT_PIPELINE_RUNS_NUMBER);
                arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, String.valueOf(DEFAULT_PIPELINE_RUNS_NUMBER));
            }
        } else {
            mLogger.info("Command line argument for cache process run times is not stated, using default = "
                    + DEFAULT_PIPELINE_RUNS_NUMBER);
            arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, String.valueOf(DEFAULT_PIPELINE_RUNS_NUMBER));
        }
    }

    private void parseMaximumCacheEntriesArgument(CommandLine cmd, Map<String, String> arguments) {
        if (cmd.hasOption(CACHE_ENTRIES_FED_ARGUMENT_KEY)) {
            try {
                if (Integer.parseInt(cmd.getOptionValue(CACHE_ENTRIES_FED_ARGUMENT_KEY)) < DEFAULT_CACHE_ENTRIES_NUMBER) {
                    mLogger.error("Command line argument for entries number for cache to get data from is small, " +
                            "using default = " + DEFAULT_PIPELINE_RUNS_NUMBER);
                    arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, String.valueOf(DEFAULT_CACHE_ENTRIES_NUMBER));
                } else {
                    arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, cmd.getOptionValue(CACHE_ENTRIES_FED_ARGUMENT_KEY));
                }
            } catch (NumberFormatException mfex) {
                mLogger.error("Command line argument for entries number for cache to get data from is wrong, " +
                        "using default = " + DEFAULT_CACHE_ENTRIES_NUMBER);
                arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, String.valueOf(DEFAULT_CACHE_ENTRIES_NUMBER));
            }
        } else {
            mLogger.info("Command line argument for entries number for cache to get data from is not stated, " +
                    "using default = " + DEFAULT_CACHE_ENTRIES_NUMBER);
            arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, String.valueOf(DEFAULT_CACHE_ENTRIES_NUMBER));
        }
    }

    private void parseDetailedReportArgument(CommandLine cmd, Map<String, String> arguments) {
        if (cmd.hasOption("dr")) {
            arguments.put("dr", "true");
            mLogger.info("Command line argument for detailed report is stated");
        } else {
            arguments.put("dr", "false");
        }
    }

    private void parseTestArgument(CommandLine cmd, Map<String, String> arguments) {
        if (cmd.hasOption("test")) {
            mLogger.info("Command line argument for test is stated");
            arguments.put("test", "true");
        } else {
            arguments.put("test", "false");
        }
    }
}
