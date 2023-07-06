package ru.cache.vlad.yanchenko.arguments;

import org.apache.commons.cli.*;

import java.util.Optional;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.*;

/**
 * Cache arguments parser
 */
public class CacheArgumentsParserImpl implements CacheArgumentsParser {

    @Override
    public Optional<CommandLine> parse(String[] args) throws ParseException {
        Options options = createCommandLineOptions();
        CommandLineParser parser = new DefaultParser();
        return Optional.of(parser.parse(options, args));
    }

    private Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt(CACHE_ENTRIES_FED_ARGUMENT_KEY)
                .hasArg()
                .argName("entries")
                .desc("Number of entries to be fed to a cache processor")
                .build());
        options.addOption(Option.builder()
                .longOpt(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY)
                .hasArg()
                .argName("times")
                .desc("Number of times cache pipeline is to run")
                .build());
        options.addOption(Option.builder()
                .longOpt(CACHE_KIND_ARGUMENT_KEY)
                .hasArg()
                .argName("kind")
                .desc("Cache kind - LRU/MRU/LFU")
                .build());
        options.addOption(Option.builder()
                .longOpt(CACHE_DETAILED_REPORT_ARGUMENT_KEY)
                .desc("If detailed report on cache operating should be provided")
                .build());
        options.addOption(Option.builder()
                .longOpt(LEVEL_1_CACHE_SIZE_ARGUMENT_KEY)
                .hasArg()
                .argName("size")
                .desc("Memory cache size")
                .build());
        options.addOption(Option.builder()
                .longOpt(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY)
                .hasArg()
                .argName("size")
                .desc("Disk cache size")
                .build());
        options.addOption(Option.builder()
                .longOpt(CACHE_TEST_ARGUMENT_KEY)
                .desc("If cache test run to be performed")
                .build());
        return options;
    }
}