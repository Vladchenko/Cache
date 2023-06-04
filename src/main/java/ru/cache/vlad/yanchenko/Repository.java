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

    // Number of hits to a RAM cache done during one caching process.
    private int mHitsRAMCache = 0;
    // Number of misses to a RAM cache done during one caching process.
    private int mMissesRAMCache = 0;
    private int mHitsHDDCache = 0;
    private int mMissesHDDCache = 0;
    // How many times the caching process is run.
    private int mPipelineRunTimes = 100;
    // Number of entries to be fed to a cache processor.
    private int mEntriesNumber = 100;

    // Number of entries to be present in a RAM cache.
    private int mRAMCacheEntriesNumber;
    // Number of entries to be present in a HDD cache.
    private int mHDDCacheEntriesNumber;

    // Memory size for a RAM cache, in kilobytes.
    private int mRAMCacheSize;
    // Memory size for an HDD cache, in kilobytes.
    private int mHDDCacheSize;

    // Checks if a detailed report to be shown in a log file.
    private boolean mDetailedReport = true;

    // Defines if a testing of all the testing algorithms is done.
    private boolean mShouldPerformTesting = false;

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

    /**
     * Resetting caching to a default state.
     */
    public void resetCachingInfo() {
        setHitsRAMCache(0);
        setMissesRAMCache(0);
        setHitsHDDCache(0);
        setMissesHDDCache(0);
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">

    public int getEntriesNumber() {
        return mEntriesNumber;
    }

    public void setEntriesNumber(int entriesNumber) {
        mEntriesNumber = entriesNumber;
    }

    /**
     * @return the testing
     */
    public boolean isTesting() {
        return mShouldPerformTesting;
    }

    /**
     * @param testing the testing to set
     */
    public void setTesting(boolean testing) {
        mShouldPerformTesting = testing;
    }

    /**
     * @return the hitsRAMCache
     */
    public int getHitsRAMCache() {
        return mHitsRAMCache;
    }

    /**
     * @param hitsRAMCache the hitsRAMCache to set
     */
    public void setHitsRAMCache(int hitsRAMCache) {
        mHitsRAMCache = hitsRAMCache;
    }

    /**
     * @return the missesRAMCache
     */
    public int getMissesRAMCache() {
        return mMissesRAMCache;
    }

    /**
     * @param missesRAMCache the missesRAMCache to set
     */
    public void setMissesRAMCache(int missesRAMCache) {
        mMissesRAMCache = missesRAMCache;
    }

    /**
     * @return the hitsHDDCache
     */
    public int getHitsHDDCache() {
        return mHitsHDDCache;
    }

    /**
     * @param hitsHDDCache the hitsHDDCache to set
     */
    public void setHitsHDDCache(int hitsHDDCache) {
        mHitsHDDCache = hitsHDDCache;
    }

    /**
     * @return the missesHDDCache
     */
    public int getMissesHDDCache() {
        return mMissesHDDCache;
    }

    /**
     * @param missesHDDCache the missesHDDCache to set
     */
    public void setMissesHDDCache(int missesHDDCache) {
        mMissesHDDCache = missesHDDCache;
    }

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
     * @return the detailedReport
     */
    public boolean isDetailedReport() {
        return mDetailedReport;
    }

    /**
     * @param detailedReport the detailedReport to set
     */
    public void setDetailedReport(boolean detailedReport) {
        mDetailedReport = detailedReport;
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

    public int getRAMCacheEntriesNumber() {
        return mRAMCacheEntriesNumber;
    }

    public void setRAMCacheEntriesNumber(int ramCacheEntriesNumber) {
        mRAMCacheEntriesNumber = ramCacheEntriesNumber;
    }

    public int getHDDCacheEntriesNumber() {
        return mHDDCacheEntriesNumber;
    }

    public void setHDDCacheEntriesNumber(int hddCacheEntriesNumber) {
        mHDDCacheEntriesNumber = hddCacheEntriesNumber;
    }
//</editor-fold>
}
