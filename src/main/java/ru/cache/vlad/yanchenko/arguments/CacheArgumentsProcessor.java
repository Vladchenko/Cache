package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/** Processor that parses and validates command line arguments. */
public class CacheArgumentsProcessor {

    private final CacheArgumentsParser argumentsParser;
    private final CacheArgumentsValidatorImpl argumentsValidator;

    /**
     * Public constructor. Provides class's dependencies and instantiates it.
     *
     * @param argumentsParser   command line arguments parser
     * @param argumentsValidator command line arguments validator
     */
    public CacheArgumentsProcessor(@NonNull CacheArgumentsParser argumentsParser,
                                   @NonNull CacheArgumentsValidatorImpl argumentsValidator) {
        this.argumentsParser = argumentsParser;
        this.argumentsValidator = argumentsValidator;
    }

    /**
     * Parse and validate a command line arguments
     *
     * @param args arguments to process
     * @return  processed arguments
     */
    public Map<String, String> processArguments(@NonNull String[] args) {
        try {
            Optional<CommandLine> commandLineOpt = argumentsParser.parse(args);
            if (commandLineOpt.isPresent()) {
                CommandLine commandLine = commandLineOpt.get();
                // Validating command line arguments
                return argumentsValidator.validateCommandLineArguments(commandLine);
            } else {
                return Collections.emptyMap();
            }
        } catch (ParseException e) {
            // TODO Is it ok to throw RuntimeException here ?
            throw new RuntimeException(e);
        }
    }
}
