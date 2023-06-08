package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.Logger;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.caches.CacheKind;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.*;
import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_DETAILED_REPORT_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.CacheConstants.*;
import static ru.cache.vlad.yanchenko.CacheConstants.DEFAULT_CACHE_ENTRIES_NUMBER;

/**
 * Command line arguments validator
 */
public class CacheArgumentsValidatorImpl implements CacheArgumentsValidator {

    private final Logger mLogger;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger to log the events
     */
    public CacheArgumentsValidatorImpl(@NonNull Logger logger) {
        mLogger = logger;
    }

    @Override
    public Map<String, String> validateCommandLineArguments(CommandLine commandLine) {
        Map<String, String> arguments = new HashMap<>();
        parseTestArgument(commandLine, arguments);
        parseDetailedReportArgument(commandLine, arguments);
        parseMaximumEntriesToBeFedToCacheArgument(commandLine, arguments);
        parseCachingProcessRunTimesArgument(commandLine, arguments);
        parseCacheKindArgument(commandLine, arguments);
        processRamCacheSizeArgument(commandLine, arguments);
        processHddCacheSizeArgument(commandLine, arguments);
        return arguments;
    }
    
    private void parseCacheKindArgument(@NonNull CommandLine commandLine, @NonNull Map<String, String> arguments) {
        String cacheKind;
        if (commandLine.hasOption(CACHE_KIND_ARGUMENT_KEY)) {
            if (EnumUtils.isValidEnum(CacheKind.class, commandLine.getOptionValue(CACHE_KIND_ARGUMENT_KEY).toUpperCase(Locale.ROOT))) {
                cacheKind = commandLine.getOptionValue(CACHE_KIND_ARGUMENT_KEY);
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

    private void parseCachingProcessRunTimesArgument(@NonNull CommandLine commandLine,
                                                     @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)) {
            try {
                if (Integer.parseInt(commandLine.getOptionValue(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)) < DEFAULT_PIPELINE_RUNS_NUMBER) {
                    mLogger.error("Cache process run times command line argument is small, using default = "
                            + DEFAULT_PIPELINE_RUNS_NUMBER);
                    arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, String.valueOf(DEFAULT_PIPELINE_RUNS_NUMBER));
                } else {
                    arguments.put(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY, commandLine.getOptionValue(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY));
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

    private void parseMaximumEntriesToBeFedToCacheArgument(@NonNull CommandLine commandLine,
                                                           @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption(CACHE_ENTRIES_FED_ARGUMENT_KEY)) {
            try {
                if (Integer.parseInt(commandLine.getOptionValue(CACHE_ENTRIES_FED_ARGUMENT_KEY)) < DEFAULT_CACHE_ENTRIES_NUMBER) {
                    mLogger.error("Command line argument for entries number for cache to get data from is small, " +
                            "using default = " + DEFAULT_CACHE_ENTRIES_NUMBER);
                    arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, String.valueOf(DEFAULT_CACHE_ENTRIES_NUMBER));
                } else {
                    arguments.put(CACHE_ENTRIES_FED_ARGUMENT_KEY, commandLine.getOptionValue(CACHE_ENTRIES_FED_ARGUMENT_KEY));
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

    private void parseDetailedReportArgument(@NonNull CommandLine commandLine, @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption(CACHE_DETAILED_REPORT_ARGUMENT_KEY)) {
            arguments.put(CACHE_DETAILED_REPORT_ARGUMENT_KEY, "true");
            mLogger.info("Detailed report is ON");
        } else {
            arguments.put(CACHE_DETAILED_REPORT_ARGUMENT_KEY, "false");
            mLogger.info("Detailed report is OFF");
        }
    }

    private void parseTestArgument(@NonNull CommandLine commandLine, @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption("test")) {
            mLogger.info("Command line argument for test is stated");
            arguments.put("test", "true");
        } else {
            arguments.put("test", "false");
        }
    }

    private void processRamCacheSizeArgument(@NonNull CommandLine commandLine, @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY)) {
            try {
                int cacheSize = Integer.parseInt(commandLine.getOptionValue(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY));
                if (cacheSize < CacheConstants.MINIMUM_RAM_CACHE_ENTRIES) {
                    mLogger.info("Level 1 cache size is small, using default - "
                            + CacheConstants.DEFAULT_RAM_CACHE_ENTRIES);
                } else {
                    arguments.put(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY, String.valueOf(cacheSize));
                }
            } catch (NumberFormatException nfex) {
                mLogger.info("Level 1 cache size is wrong, using default - " + CacheConstants.DEFAULT_RAM_CACHE_ENTRIES);
                arguments.put(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY, Integer.toString(CacheConstants.DEFAULT_RAM_CACHE_ENTRIES));
            }
        } else {
            mLogger.info("Level 1 cache size is not set, using default - " + CacheConstants.DEFAULT_RAM_CACHE_ENTRIES);
            arguments.put(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY, Integer.toString(CacheConstants.DEFAULT_RAM_CACHE_ENTRIES));
        }
    }

    private void processHddCacheSizeArgument(@NonNull CommandLine commandLine, @NonNull Map<String, String> arguments) {
        if (commandLine.hasOption(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY)) {
            try {
                int cacheSize = Integer.parseInt(commandLine.getOptionValue(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY));
                if (cacheSize < CacheConstants.MINIMUM_HDD_CACHE_ENTRIES) {
                    mLogger.info("Level 2 cache size is small, using default - "
                            + CacheConstants.DEFAULT_HDD_CACHE_ENTRIES);
                } else {
                    arguments.put(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY, String.valueOf(cacheSize));
                }
            } catch (NumberFormatException nfex) {
                mLogger.info("Level 2 cache size is wrong, using default - " + CacheConstants.DEFAULT_HDD_CACHE_ENTRIES);
                arguments.put(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY, Integer.toString(CacheConstants.DEFAULT_HDD_CACHE_ENTRIES));
            }
        } else {
            mLogger.info("Level 2 cache size is not set, using default - " + CacheConstants.DEFAULT_HDD_CACHE_ENTRIES);
            arguments.put(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY, Integer.toString(CacheConstants.DEFAULT_HDD_CACHE_ENTRIES));
        }
    }
}
