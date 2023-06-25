package ru.cache.vlad.yanchenko.arguments;

import org.apache.commons.cli.CommandLine;

/**
 * Command line arguments parser
 */
public interface CacheArgumentsParser {


    /**
     * Reading arguments entered from command line.
     *
     * @param args command line arguments
     * @return parsed arguments map
     */
    CommandLine parseCommandLineArguments(String[] args);
}
