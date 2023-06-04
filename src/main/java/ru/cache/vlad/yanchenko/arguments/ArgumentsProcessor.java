/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.arguments;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.CacheConstants;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.exceptions.DirectoryException;
import ru.cache.vlad.yanchenko.exceptions.FileExtensionException;
import ru.cache.vlad.yanchenko.exceptions.FilePrefixException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Command line arguments processor.
 *
 * @author v.yanchenko
 */
public class ArgumentsProcessor {

    private final Logger mLogger;
    private final Repository mRepository;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger     logger to log the events
     * @param repository that holds a settings for program.
     */
    public ArgumentsProcessor(@NonNull Logger logger, @NonNull Repository repository) {
        mLogger = logger;
        mRepository = repository;
    }

    /**
     * Putting an args and couples of "key=value" to a map
     *
     * @param args command line arguments
     */
    public void processArguments(@NonNull String[] args) {
        Map<String, String> arguments = readArguments(args);
        if (mRepository.isDetailedReport()) {
            CacheLoggingUtils.printArgs(arguments);
        }
        processCachesSizes(arguments);
        processCacheKind(arguments);
        processAccompanyingArguments(arguments);
    }

    /**
     * Validate some command line arguments.
     * TODO Maybe validate some more ?
     */
    public void validateArguments() {
        FileUtils fileUtils = new FileUtils(mLogger);
        try {
            fileUtils.validateFilePath(CacheConstants.FILES_FOLDER);
        } catch (DirectoryException e) {
            mLogger.info(CacheConstants.FILES_FOLDER + " is not a valid folder. Program exits.");
            System.exit(1);
        }
        try {
            fileUtils.validateFilePrefix(CacheConstants.FILE_PREFIX);
        } catch (FilePrefixException e) {
            mLogger.info(CacheConstants.FILE_PREFIX + " is not a valid file prefix. Program exits.");
            System.exit(1);
        }
        try {
            fileUtils.validateFileExtension(CacheConstants.FILE_EXTENSION);
        } catch (FileExtensionException e) {
            mLogger.info(CacheConstants.FILE_EXTENSION + " is not a valid file extension. Program exits.");
            System.exit(1);
        }
    }

    // Reading arguments entered from command line.
    private Map<String, String> readArguments(@NonNull String[] args) {
        // Delimiter that separates key from value in an entry.
        String delimiters = "[=]+";    // Use = as a delimiter
        // Map that represents "key=argument" entries, like (n=50).
        Map<String, String> arguments = new HashMap<>();
        for (String arg : args) {
            // Checking if a detailed report is enabled.
            if (arg.equals("dr")) {
                mRepository.setDetailedReport(true);
            }
            // Checking if a testing is enabled.
            if (arg.equals("test")) {
                mRepository.setTesting(true);
            }
            String[] map = arg.split(delimiters);
            try {
                if (!map[0].isEmpty()) {
                    arguments.put(map[0].toLowerCase(), map[1].toLowerCase());
                }
            } catch (Exception ex) {
                mLogger.info("Some wrong argument present, check it.");
            }
        }
        return arguments;
    }

    private void processCachesSizes(@NonNull Map<String, String> arguments) {

        String number = "";

        // Processing arguments for level1 cache.
        if (arguments.containsKey("level1size")
                || arguments.containsKey("l1s")) {
            number = arguments.get("level1size");
        }
        try {
            mRepository.setRAMCacheEntriesNumber(Integer.parseInt(number));
            if (mRepository.getRAMCacheEntriesNumber() < CacheConstants.RAM_CACHE_ENTRIES_MINIMUM) {
                throw new NumberFormatException();
            }
            mLogger.info("Level 1 cache size is set to " + mRepository.getRAMCacheEntriesNumber());
        } catch (NumberFormatException nfe) {
            mLogger.info("Level 1 cache size is not set, using default - " + CacheConstants.RAM_CACHE_ENTRIES_DEFAULT);
            mRepository.setRAMCacheEntriesNumber(CacheConstants.RAM_CACHE_ENTRIES_DEFAULT);
        }

        // Processing arguments for level2 cache.
        if (arguments.containsKey("level2size")
                || arguments.containsKey("l2s")) {
            number = arguments.get("level2size");
        }
        try {
            mRepository.setHDDCacheEntriesNumber(Integer.parseInt(number));
            if (mRepository.getHDDCacheEntriesNumber() < CacheConstants.HDD_CACHE_ENTRIES_MINIMUM) {
                throw new NumberFormatException();
            }
            mLogger.info("Level 2 cache size is set to " + mRepository.getHDDCacheEntriesNumber());
        } catch (NumberFormatException nfe) {
            mLogger.info("Level 2 cache size is not set, using default - " + CacheConstants.HDD_CACHE_ENTRIES_DEFAULT);
            mRepository.setHDDCacheEntriesNumber(CacheConstants.HDD_CACHE_ENTRIES_DEFAULT);
        }
    }

    private void processCacheKind(@NonNull Map<String, String> arguments) {
        if (arguments.get("cachekind") == null
                && arguments.get("ck") == null) {
            mLogger.info("Cache kind is not set, used default - Most Recently Used.");
            mRepository.setCacheKind(Repository.cacheKindEnum.MRU);
        } else {
            String ck = arguments.get("cachekind");
            if (ck == null) {
                ck = arguments.get("ck");
            }
            switch (ck) {
                case "lfu": { }
                case "LFU": {
                    mRepository.setCacheKind(Repository.cacheKindEnum.LFU);
                    break;
                }
                case "lru": { }
                case "LRU": {
                    mRepository.setCacheKind(Repository.cacheKindEnum.LRU);
                    break;
                }
                case "mru": { }
                case "MRU": {
                    mRepository.setCacheKind(Repository.cacheKindEnum.MRU);
                    break;
                }
            }
            mLogger.info("cacheKind is set to - " + mRepository.getCacheKind());
        }
    }

    private void processAccompanyingArguments(@NonNull Map<String, String> arguments) {

        String number;

        // Defining how many entries will be fed to a caching process.
        try {
            mRepository.setEntriesNumber(Integer.parseInt(arguments.get("m")));
            mLogger.info("Entries number is set to " + mRepository.getEntriesNumber());
        } catch (Exception nfe) {
            mLogger.info("Entries number is not set, using default - " + CacheConstants.ENTRIES_NUMBER_DEFAULT);
            mRepository.setEntriesNumber(CacheConstants.ENTRIES_NUMBER_DEFAULT);
        }

        if (mRepository.isDetailedReport()) {
            mLogger.info("Caching process report is set to be detailed.");
        } else {
            mLogger.info("Caching process report is set to be not detailed.");
        }

        // Defining a cache process running times, i.e. how many times a caching process is to run.
        try {
            number = arguments.get("n");
            mRepository.setPipelineRunTimes(Integer.parseInt(number));
            mLogger.info("Cache process will run for " + mRepository.getPipelineRunTimes() + " times");
        } catch (Exception ex) {
            mLogger.info("Cache process run times is not set, " + "using default - "
                    + mRepository.getPipelineRunTimes());
            mRepository.setCacheKind(Repository.cacheKindEnum.MRU);
        }

        mLogger.info("");
    }
}
