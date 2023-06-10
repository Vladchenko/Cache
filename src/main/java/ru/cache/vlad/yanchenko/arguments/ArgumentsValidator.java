package ru.cache.vlad.yanchenko.arguments;

import org.apache.commons.cli.CommandLine;

import java.util.Map;

public interface ArgumentsValidator {
    Map<String, String> validateCommandLineArguments(CommandLine commandLine);
}
