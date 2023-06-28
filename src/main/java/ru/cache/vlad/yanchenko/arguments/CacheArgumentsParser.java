package ru.cache.vlad.yanchenko.arguments;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.util.Optional;

/**
 * Command line arguments parser
 */
public interface CacheArgumentsParser {

    /**
     * Reading arguments entered from command line.
     *
     * @param args command line arguments
     * @return {@link CommandLine} wrapped in {@link Optional}
     */
    Optional<CommandLine> parse(String[] args) throws ParseException;
}
