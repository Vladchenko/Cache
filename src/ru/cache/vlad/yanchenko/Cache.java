/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

/**
 * Initial class.
 *
 * @author v.yanchenko
 */
public class Cache {

    private ProcessArguments processArguments;
    private CacheProcessor cacheProcessor;
    private Testing test;
    private Repository repository = Repository.getInstance();

    private Cache(String[] args) {
        processArguments = new ProcessArguments(repository);
        processArguments.processArgs(args);
        cacheProcessor = CacheProcessor.getInstance();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Cache cache = new Cache(args);
        
        // Run a test, if a specific command line arguments says so.
        if (cache.repository.isTesting()) {
            cache.test = new Testing();
            cache.test.runTesting();
        // Else run a single cache algorithm.
        } else {
            cache.cacheProcessor.performCachingProcess();
        }

    }

}
