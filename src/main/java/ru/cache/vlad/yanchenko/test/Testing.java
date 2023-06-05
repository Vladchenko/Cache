/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.test;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

/**
 * Class runs a test on all the present cache algorithms.
 *
 * @author v.yanchenko
 */
public class Testing {

    private final Logger mLogger;
    private final Map<String, String> mArguments;
    // Map of objects that is going to be fed to a caching algorithm.
    private Map<Object, Object> mTestingObjects;
    private final CacheProcessor mCacheProcessor;

    /**
     * Public constructor. Provides dependencies and creates an instance of a class.
     *
     * @param logger         to log a testing events
     * @param arguments      from command line
     * @param cacheProcessor TODO
     */
    public Testing(@NonNull Logger logger, @NonNull Map<String, String> arguments, @NonNull CacheProcessor cacheProcessor) {
        mLogger = logger;
        mArguments = arguments;
        mTestingObjects = new HashMap<>();
        mCacheProcessor = cacheProcessor;
        // Populating a map for further using it as a template entry set for all the caching algorithms.
        mTestingObjects = cacheProcessor.getCacheFeeder().populateData();
    }

    /**
     * Runs a test on a several test algorithms. Process information is written to a log file.
     */
    public void runTesting() throws NotPresentException, IOException, ClassNotFoundException {

        // Putting all the entries from a testing map to a msp that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));

        // Putting all the entries from a testing map to a map that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(mTestingObjects)
        );

        // Setting a cache kind.
        mArguments.put("cachekind", Repository.cacheKindEnum.LRU.toString());
        for (int i = 0; i < Integer.parseInt(mArguments.get("n")); i++) {
            mCacheProcessor.processRequest(
                    mCacheProcessor.getCacheFeeder().requestObject()
            );
        }
        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mCacheProcessor.getRamCache(), mCacheProcessor.getHddCache(), mArguments);
        mCacheProcessor.getRamCache().clearCache();
        mCacheProcessor.getHddCache().clearCache();
        mCacheProcessor.getRamCache().resetCacheStatistics();
        mCacheProcessor.getHddCache().resetCacheStatistics();
        mLogger.info("");
        mLogger.info("");

        // Putting all the entries from a testing msp to a map that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));

        // Setting a cache kind.
        mArguments.put("cachekind", Repository.cacheKindEnum.MRU.toString());
        for (int i = 0; i < Integer.parseInt(mArguments.get("n")); i++) {
            mCacheProcessor.processRequest(mCacheProcessor.getCacheFeeder().requestObject());
        }

        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mCacheProcessor.getRamCache(), mCacheProcessor.getHddCache(), mArguments);
    }
}
