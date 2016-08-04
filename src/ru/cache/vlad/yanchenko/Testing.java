/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class Testing {

    Map<Object, Object> mapTesting;
    private Repository repository;
    CacheProcessor cacheProcessor;
    int cachingProcessRunTimes = 10000000;

    public Testing() {
        mapTesting = new HashMap<>();
        repository = Repository.getInstance();
        cacheProcessor = new CacheProcessor(repository);
    }

    private Map<Object, Object> populateMap(Map<Object, Object> map) {
        for (int i = 0; i < 100000; i++) {
            map.put(Integer.toString((int) (Math.random() * 1000000000)),
                    Integer.toString((int) (Math.random() * 1000000000)));
        }
        System.out.println("Data, that is to be fed to cacheProcessor is populated");
        return map;
    }

    private void runTesting() {
        repository.setCacheKind(Repository.cacheKindEnum.LRR);
        for (int i = 0; i < cachingProcessRunTimes; i++) {
            cacheProcessor.processRequest(null);
        }
    }
}
