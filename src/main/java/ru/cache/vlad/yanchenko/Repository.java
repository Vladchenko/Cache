/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

/**
 * Keeps a data that is used throughout the application. Made to have only one instance.
 *
 * @author v.yanchenko
 */
public class Repository {

    // How many times the caching process is run.
    private int mPipelineRunTimes = 100;
    // Number of entries to be fed to a cache processor.
    private int mEntriesNumber = 100;

    // Memory size for a RAM cache, in kilobytes.
    private int mRAMCacheSize;
    // Memory size for an HDD cache, in kilobytes.
    private int mHDDCacheSize;

    private cacheKindEnum mCacheKind;
    private static Repository mRepository;

    // Defines a cache kind - mru / lru.
    public enum cacheKindEnum {
        NONE, LFU, LRU, MRU
    }

    private Repository() {
        mCacheKind = cacheKindEnum.NONE;
    }

    /** Public method that always returns the same instance of repository. */
    public static Repository getInstance() {
        if (mRepository == null) {
            mRepository = new Repository();
        }
        return mRepository;
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">

    public int getEntriesNumber() {
        return mEntriesNumber;
    }

    public void setEntriesNumber(int entriesNumber) {
        mEntriesNumber = entriesNumber;
    }

//    /**
//     * @return the testing
//     */
//    public boolean isTesting() {
//        return mShouldPerformTesting;
//    }
//
//    /**
//     * @param testing the testing to set
//     */
//    public void setTesting(boolean testing) {
//        mShouldPerformTesting = testing;
//    }

    /**
     * @return the pipelineRunTimes
     */
    public int getPipelineRunTimes() {
        return mPipelineRunTimes;
    }

    /**
     * @param pipelineRunTimes the pipelineRunTimes to set
     */
    public void setPipelineRunTimes(int pipelineRunTimes) {
        mPipelineRunTimes = pipelineRunTimes;
    }

    /**
     * @return the cacheKind
     */
    public cacheKindEnum getCacheKind() {
        return mCacheKind;
    }

    /**
     * @param cacheKind the cacheKind to set
     */
    public void setCacheKind(cacheKindEnum cacheKind) {
        mCacheKind = cacheKind;
    }
//</editor-fold>
}
