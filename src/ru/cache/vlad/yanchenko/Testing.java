/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.util.HashMap;
import java.util.Map;

/**
 * Class runs a test on all the present cache algorithms.
 * 
 * @author v.yanchenko
 */
public class Testing {

    // Map of objects that is going to be fed to a caching algorithm.
    private Map<Object, Object> mapTesting;
    private Repository repository;
    private CacheProcessor cacheProcessor;

    Testing() {
        mapTesting = new HashMap<>();
        repository = Repository.getInstance();
        cacheProcessor = new CacheProcessor(repository);
        /**
         * Populating a map for further using it as a template entry set for 
         * all caching the algorithms.
         */
        mapTesting = cacheProcessor.getCacheFeeder().populateData();
    }

    /**
     * Runs a test on a several test algorythms. Process information is written 
     * to a log file.
     */
    public void runTesting() {
        
        /** 
         * Putting all the entries from a testing map to a msp that's gonna be 
         * fed to a caching algorithm.
         */
        cacheProcessor.getCacheFeeder().setMapObjectsFed(
                cacheProcessor.getCacheFeeder().copyData(
                mapTesting));
        
        /** 
         * Putting all the entries from a testing msp to a map that's gonna be 
         * fed to a caching algorithm.
         */
        cacheProcessor.getCacheFeeder().setMapObjectsFed(
                cacheProcessor.getCacheFeeder().copyData(
                mapTesting));
        
        // Setting a cache kind.
        repository.setCacheKind(Repository.cacheKindEnum.LRU);
        for (int i = 0; i < repository.getPipelineRunTimes(); i++) {
            cacheProcessor.processRequest(
                    cacheProcessor.getCacheFeeder().requestObject());
        }
        // Printing a summary for a current caching process.
        repository.printSummary();
        cacheProcessor.getHddCache().clearCache();
        cacheProcessor.getRamCache().clearCache();
        repository.resetCachingInfo();
        repository.getLogger().info("");
        repository.getLogger().info("");
        
        /** 
         * Putting all the entries from a testing msp to a map that's gonna be 
         * fed to a caching algorithm.
         */
        cacheProcessor.getCacheFeeder().setMapObjectsFed(
                cacheProcessor.getCacheFeeder().copyData(
                mapTesting));
        
        // Setting a cache kind.
        repository.setCacheKind(Repository.cacheKindEnum.MRU);
        for (int i = 0; i < repository.getPipelineRunTimes(); i++) {
            cacheProcessor.processRequest(
                    cacheProcessor.getCacheFeeder().requestObject());
        }
        // Printing a summary for a current caching process.
        repository.printSummary();
    }

    //<editor-fold defaultstate="collapsed" desc="getters and setters">
    /**
     * @return the mapTesting
     */
    public Map<Object, Object> getMapTesting() {
        return mapTesting;
    }
    
    /**
     * @param mapTesting the mapTesting to set
     */
    public void setMapTesting(Map<Object, Object> mapTesting) {
        this.mapTesting = mapTesting;
    }
    
    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }
    
    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
    
    /**
     * @return the cacheProcessor
     */
    public CacheProcessor getCacheProcessor() {
        return cacheProcessor;
    }
    
    /**
     * @param cacheProcessor the cacheProcessor to set
     */
    public void setCacheProcessor(CacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
    }
    
//</editor-fold>
    
}
