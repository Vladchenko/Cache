/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko.test;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.Repository;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class runs a test on all the present cache algorithms.
 * 
 * @author v.yanchenko
 */
public class Testing {

    private final Logger mLogger;
    private final Repository mRepository;
    // Map of objects that is going to be fed to a caching algorithm.
    private Map<Object, Object> mTestingObjects;
    private final CacheProcessor mCacheProcessor;

    /**
     * Public constructor. Provides dependencies and creates an instance of a class.
     *
     * @param logger         to log a testing events
     * @param repository     that holds a settings for program.
     * @param cacheProcessor TODO
     */
    public Testing(@NonNull Logger logger, @NonNull Repository repository, @NonNull CacheProcessor cacheProcessor) {
        mLogger = logger;
        mRepository = repository;
        mTestingObjects = new HashMap<>();
        mCacheProcessor = cacheProcessor;
        // Populating a map for further using it as a template entry set for all the caching algorithms.
        mTestingObjects = cacheProcessor.getCacheFeeder().populateData();
    }

    /** Runs a test on a several test algorithms. Process information is written to a log file. */
    public void runTesting() {
        
        // Putting all the entries from a testing map to a msp that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));
        
        // Putting all the entries from a testing msp to a map that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));
        
        // Setting a cache kind.
        mRepository.setCacheKind(Repository.cacheKindEnum.LRU);
        for (int i = 0; i < mRepository.getPipelineRunTimes(); i++) {
            mCacheProcessor.processRequest(
                    mCacheProcessor.getCacheFeeder().requestObject());
        }
        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mRepository);
        mCacheProcessor.getHddCache().clearCache();
        mCacheProcessor.getRamCache().clearCache();
        mRepository.resetCachingInfo();
        mLogger.info("");
        mLogger.info("");
        
        // Putting all the entries from a testing msp to a map that's going to be fed to a caching algorithm.
        mCacheProcessor.getCacheFeeder().setMapObjectsFed(
                mCacheProcessor.getCacheFeeder().copyData(
                        mTestingObjects));
        
        // Setting a cache kind.
        mRepository.setCacheKind(Repository.cacheKindEnum.MRU);
        for (int i = 0; i < mRepository.getPipelineRunTimes(); i++) {
            mCacheProcessor.processRequest(
                    mCacheProcessor.getCacheFeeder().requestObject());
        }

        // Printing a summary for a current caching process.
        CacheLoggingUtils.printSummary(mRepository);
    }
}
