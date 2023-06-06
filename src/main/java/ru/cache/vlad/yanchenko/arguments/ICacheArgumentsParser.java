package ru.cache.vlad.yanchenko.arguments;

import java.util.Map;

/**
 * Command line arguments parser
 */
public interface ICacheArgumentsParser {


    /**
     * Reading arguments entered from command line.
     *
     * @param args command line arguments
     * @return parsed arguments map
     */
    Map<String, String> parseCommandLineArguments(String[] args);
}
