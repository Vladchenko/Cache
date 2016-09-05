/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import ru.cache.vlad.yanchenko.arguments.ValidateLayout;
import ru.cache.vlad.yanchenko.caches.DirectoryException;
import ru.cache.vlad.yanchenko.caches.FileExtensionException;
import ru.cache.vlad.yanchenko.caches.FilePrefixException;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
/**
 * Initial class.
 *
 * @author v.yanchenko
 */
public class Cache {

    private ru.cache.vlad.yanchenko.arguments.ProcessArguments processArguments;
    private CacheProcessor cacheProcessor;
    private ru.cache.vlad.yanchenko.test.Testing test;
    private Repository repository = Repository.getInstance();

    private Cache(String[] args) {
        // Processing command line arguments.
        new ru.cache.vlad.yanchenko.arguments.ProcessArguments(repository).processArguments(args);
        // Validating some command line arguments.
        runValidation();
        cacheProcessor = CacheProcessor.getInstance();
    }

    // Validating some command line arguments.
    private void runValidation() {
        ValidateLayout validateLayout = new ValidateLayout(repository.getLogger());
        try {
            validateLayout.validatePath(Repository.FILES_FOLDER);
        } catch (DirectoryException e) {
            repository.getLogger().info(Repository.FILES_FOLDER
                    + " is not a valid folder. Program exits.");
            System.exit(1);
        }
        try {
            validateLayout.validateFilePrefix(Repository.FILE_PREFIX);
        } catch (FilePrefixException e) {
            repository.getLogger().info(Repository.FILE_PREFIX
                    + " is not a valid file prefix. Program exits.");
            System.exit(1);
        }
        try {
            validateLayout.validateFileExtension(Repository.FILE_EXTENTION);
        } catch (FileExtensionException e) {
            repository.getLogger().info(Repository.FILE_EXTENTION
                    + " is not a valid file extension. Program exits.");
            System.exit(1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Cache cache = new Cache(args);
        
        // Run a test, if a specific command line arguments says so.
        if (cache.repository.isTesting()) {
            cache.test = new ru.cache.vlad.yanchenko.test.Testing();
            cache.test.runTesting();
        // Else run a single cache algorithm.
        } else {
            cache.cacheProcessor.performCachingProcess();
        }

    }

}
