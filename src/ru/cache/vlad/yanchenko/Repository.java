/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.IOException;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * Keeps a data that is used throught the application. Made to have only 
 * one instance.
 * 
 * @author v.yanchenko
 */
public class Repository {

    /**
     * Number of an entries that a cache can hold. This value is set, when a 
     * command line number is set in a wrong way.
     */
    public static final int RAM_CACHE_ENTRIES_DEFAULT = 10;
    public static final int HDD_CACHE_ENTRIES_DEFAULT = 10;
    // Minimum cache entries present in a cache.
    public static final int RAM_CACHE_ENTRIES_MINIMUM = 1;
    public static final int HDD_CACHE_ENTRIES_MINIMUM = 1;
//    public static final int RAM_CACHE_SIZE_DEFAULT = ;
//    public static final int HDD_CACHE_SIZE_DEFAULT;
    // Number of entries to be fed to a cache processor.
    public static final int ENTRIES_NUMBER_DEFAULT = 25;
    public static final String FILE_PREFIX = "cache_file_";
    // File extention.
    public static final String FILE_EXTENTION = ".cache";
    // Folder for a file that represents a level 2 cache (HDD cache)
    public static final String FILES_FOLDER = "Cached Data\\";

    // Number of hits to a RAM cache done during one caching process.
    private int hitsRAMCache = 0;
    // Number of missess to a RAM cache done during one caching process.
    private int missesRAMCache = 0;
    private int hitsHDDCache = 0;
    private int missesHDDCache = 0;
    // How many times the caching process is run.
    private int pipelineRunTimes = 100;
    // Number of entries to be fed to a cache processor.
    private int entriesNumber = 100;

    // Number of entries to be present in a RAM cache.
    private int RAMCacheEntriesNumber;
    // Number of entries to be present in a HDD cache.
    private int HDDCacheEntriesNumber;
    
    // Memory size for a RAM cache, in kilobytes.
    private int RAMCacheSize;
    // Memory size for an HDD cache, in kilobytes.
    private int HDDCacheSize;

    // Checks if a detailed report to be shown in a log file.
    private boolean detailedReport = false;
    
    // Defines if a testing of all the testing algorythms is done.
    private boolean testing = false;
    
    // Singleton.
    public static Repository repository = Repository.getInstance();

    // Defines a cache kind - mru / lru.
    public enum cacheKindEnum {

        NONE, LFU, LRU, MRU
    };
    private cacheKindEnum cacheKind;

    // Establishing a log for a cache process.
    private Logger logger = Logger.getLogger("myLog");
    FileHandler fileHandler;

    // Hidden constructor, for no one and nowhere in a program could make 
    // another instance of this class.
    private Repository() {

        cacheKind = cacheKindEnum.NONE;

        // Setting up a logging
        try {
            fileHandler = new FileHandler("Cache_log.log", 1000000, 1);
            MyFormatter formatter = new MyFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

        // Public method that always returns the same instance of repository.
        public static Repository getInstance() {
            if (repository == null) {
                repository = new Repository();
            }
            return repository;
        }

    // Writing a summary about a current caching process to a log file.
    public void printSummary() {
        logger.info("--- Summary ---------------------------------------");
        logger.info("| Cache algorithm : " + getCacheKind());
        logger.info("| Pipeline ran for: " + getPipelineRunTimes() + " times");
        logger.info("| RAM cache hits  : " + getHitsRAMCache() + " times");
        logger.info("| HDD cache hits  : " + getHitsHDDCache() + " times");
        logger.info("| RAM cache misses: " + getMissesRAMCache() + " times");
        logger.info("| HDD cache misses: " + getMissesHDDCache() + " times");
        logger.info("---------------------------------------------------");
    }

    // Resetting an info about a current caching process.
    public void resetCachingInfo() {
        setHitsRAMCache(0);
        setMissesRAMCache(0);
        setHitsHDDCache(0);
        setMissesHDDCache(0);
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">

    public int getEntriesNumber() {
        return entriesNumber;
    }

    public void setEntriesNumber(int entriesNumber) {
        this.entriesNumber = entriesNumber;
    }

    /**
     * @return the testing
     */
    public boolean isTesting() {
        return testing;
    }

    /**
     * @param testing the testing to set
     */
    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * @return the hitsRAMCache
     */
    public int getHitsRAMCache() {
        return hitsRAMCache;
    }

    /**
     * @param hitsRAMCache the hitsRAMCache to set
     */
    public void setHitsRAMCache(int hitsRAMCache) {
        this.hitsRAMCache = hitsRAMCache;
    }

    /**
     * @return the missesRAMCache
     */
    public int getMissesRAMCache() {
        return missesRAMCache;
    }

    /**
     * @param missesRAMCache the missesRAMCache to set
     */
    public void setMissesRAMCache(int missesRAMCache) {
        this.missesRAMCache = missesRAMCache;
    }

    /**
     * @return the hitsHDDCache
     */
    public int getHitsHDDCache() {
        return hitsHDDCache;
    }

    /**
     * @param hitsHDDCache the hitsHDDCache to set
     */
    public void setHitsHDDCache(int hitsHDDCache) {
        this.hitsHDDCache = hitsHDDCache;
    }

    /**
     * @return the missesHDDCache
     */
    public int getMissesHDDCache() {
        return missesHDDCache;
    }

    /**
     * @param missesHDDCache the missesHDDCache to set
     */
    public void setMissesHDDCache(int missesHDDCache) {
        this.missesHDDCache = missesHDDCache;
    }

    /**
     * @return the pipelineRunTimes
     */
    public int getPipelineRunTimes() {
        return pipelineRunTimes;
    }

    /**
     * @param pipelineRunTimes the pipelineRunTimes to set
     */
    public void setPipelineRunTimes(int pipelineRunTimes) {
        this.pipelineRunTimes = pipelineRunTimes;
    }

    /**
     * @return the detailedReport
     */
    public boolean isDetailedReport() {
        return detailedReport;
    }

    /**
     * @param detailedReport the detailedReport to set
     */
    public void setDetailedReport(boolean detailedReport) {
        this.detailedReport = detailedReport;
    }    
    
    /**
     * @return the cacheKind
     */
    public cacheKindEnum getCacheKind() {
        return cacheKind;
    }

    /**
     * @param cacheKind the cacheKind to set
     */
    public void setCacheKind(cacheKindEnum cacheKind) {
        this.cacheKind = cacheKind;
    }

    public int getRAMCacheEntriesNumber() {
        return RAMCacheEntriesNumber;
    }

    public void setRAMCacheEntriesNumber(int RAMCacheEntriesNumber) {
        this.RAMCacheEntriesNumber = RAMCacheEntriesNumber;
    }

    public int getHDDCacheEntriesNumber() {
        return HDDCacheEntriesNumber;
    }

    public void setHDDCacheEntriesNumber(int HDDCacheEntriesNumber) {
        this.HDDCacheEntriesNumber = HDDCacheEntriesNumber;
    }
//</editor-fold>

}

    // Overriding an implementation of a standard formatter.
    // It's done to remove an excess information that is put to log.
class MyFormatter extends SimpleFormatter {

    // Changing a string that is going to be put to a log file.
    @Override
    public synchronized String format(LogRecord record) {
        return record.getMessage() + System.getProperty("line.separator");
    }

}
