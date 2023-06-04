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

import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.Logger;

import static ru.cache.vlad.yanchenko.Repository.cacheKindEnum.*;

/**
 * Command line arguments processor.
 *
 * @author v.yanchenko
 */
public class CacheArgumentsProcessor {

    private final Logger mLogger;
    private final Repository mRepository;
    private final CacheArgumentsReader mArgumentsReader;

    /**
     * Public constructor - creates an instance of class
     *
     * @param logger     logger to log the events
     * @param repository that holds a settings for program.
     * @param argumentsReader   reads command line argument
     */
    public CacheArgumentsProcessor(@NonNull Logger logger,
                                   @NonNull Repository repository,
                                   @NonNull CacheArgumentsReader argumentsReader) {
        mLogger = logger;
        mRepository = repository;
        mArgumentsReader = argumentsReader;
    }

    /**
     * Putting an args and couples of "key=value" to a map
     *
     * @param args command line arguments
     */
    public void processArguments(@NonNull String[] args) {
        Map<String, String> arguments = mArgumentsReader.readArguments(args);
        if (mRepository.isDetailedReport()) {
            CacheLoggingUtils.printArgs(arguments);
        }
        processRamCacheSizeArgument(arguments);
        processHddCacheSize(arguments);
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

    private void processRamCacheSizeArgument(Map<String, String> arguments) {
        String cacheSize;
        if (arguments.containsKey("level1size")
                || arguments.containsKey("l1s")) {
            cacheSize = arguments.get("level1size");
        } else {
            mRepository.setRAMCacheEntriesNumber(CacheConstants.RAM_CACHE_ENTRIES_DEFAULT);
            return;
        }
        try {
            mRepository.setRAMCacheEntriesNumber(Integer.parseInt(cacheSize));
            if (mRepository.getRAMCacheEntriesNumber() < CacheConstants.RAM_CACHE_ENTRIES_MINIMUM) {
                throw new NumberFormatException();
            }
            mLogger.info("Level 1 cache size is set to " + mRepository.getRAMCacheEntriesNumber());
        } catch (NumberFormatException nfe) {
            mLogger.info("Level 1 cache size is not set, using default - " + CacheConstants.RAM_CACHE_ENTRIES_DEFAULT);
            mRepository.setRAMCacheEntriesNumber(CacheConstants.RAM_CACHE_ENTRIES_DEFAULT);
        }
    }

    private void processHddCacheSize(Map<String, String> arguments) {
        String cacheSize;
        if (arguments.containsKey("level2size")
                || arguments.containsKey("l2s")) {
            cacheSize = arguments.get("level2size");
        } else {
            mRepository.setHDDCacheEntriesNumber(CacheConstants.HDD_CACHE_ENTRIES_DEFAULT);
            return;
        }
        try {
            mRepository.setHDDCacheEntriesNumber(Integer.parseInt(cacheSize));
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
            mRepository.setCacheKind(MRU);
        } else {
            String ck = arguments.get("cachekind");
            if (ck == null) {
                ck = arguments.get("ck");
            }
            switch (valueOf(ck.toUpperCase(Locale.ROOT))) {
                case LFU -> mRepository.setCacheKind(LFU);
                case LRU -> mRepository.setCacheKind(LRU);
                case MRU -> mRepository.setCacheKind(MRU);
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
            mRepository.setCacheKind(MRU);
        }

        mLogger.info("");
    }
}
