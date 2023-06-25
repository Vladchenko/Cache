package ru.cache.vlad.yanchenko.arguments;

import org.apache.commons.cli.CommandLine;

import java.util.Map;

/**
 * Command line arguments validator.
 */
public interface CacheArgumentsValidator {

    /**
     * Validate command line arguments
     *
     * @param commandLine arguments
     * @return map that holds a params for application
     */
    Map<String, String> validateCommandLineArguments(CommandLine commandLine);
}
