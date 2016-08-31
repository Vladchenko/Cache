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
 * Processing of a command line arguments.
 * 
 * @author v.yanchenko
 */
public class ProcessArguments {

    private Repository repository = Repository.getInstance();

    public ProcessArguments(Repository repository) {
        this.repository = repository;
    }

    // Putting an args and couples of "value=key" to a map
    public void processArgs(String[] args) {
        String number = "";
        String delims = "[=]+";    // Use = as a delimiter
//        String[] tokens1 = new String[args.length];
        Map<String, String> arguments = new HashMap();
        for (String arg : args) {
            // Checking if a detailed report is enabled.
            if (arg.equals("dr")) {
                repository.setDetailedReport(true);
            }
            // Checking if a testing is enabled.
            if (arg.equals("test")) {
                repository.setTesting(true);
            }
            String[] map = arg.split(delims);
            try {
                if (!map[0].isEmpty()) {
                    arguments.put(map[0].toLowerCase(), map[1].toLowerCase());
                }
            } catch (Exception ex) {
                repository.getLogger().info("Some bad argument present, check it.");
            }
        }

        /**
         * Defining how many entries will be fed to a caching process.
         */
        try {
            repository.setEntriesNumber(Integer.parseInt(arguments.get("m")));
            repository.getLogger().info("Entries number is set to "
                    + repository.getEntriesNumber());
        } catch (Exception nfe) {
            repository.getLogger().info("Entries number is not set, using default - "
                    + Repository.ENTRIES_NUMBER_DEFAULT);
            repository.setEntriesNumber(Repository.ENTRIES_NUMBER_DEFAULT);
        }

        // Processing arguments for level1 cache.
        if (arguments.containsKey("level1size")
                || arguments.containsKey("l1s")) {
            number = arguments.get("level1size");
        }
        try {
            repository.setRAMCacheEntriesNumber(Integer.parseInt(number));
            if (repository.getRAMCacheEntriesNumber() < Repository.RAM_CACHE_ENTRIES_MINIMUM) {
                throw new NumberFormatException();
            }
            repository.getLogger().info("Level 1 cache size is set to "
                    + repository.getRAMCacheEntriesNumber());
        } catch (NumberFormatException nfe) {
            repository.getLogger().info("Level 1 cache size is not set, using default - "
                    + Repository.RAM_CACHE_ENTRIES_DEFAULT);
            repository.setRAMCacheEntriesNumber(Repository.RAM_CACHE_ENTRIES_DEFAULT);
        }

        // Processing arguments for level2 cache.
        if (arguments.containsKey("level2size")
                || arguments.containsKey("l2s")) {
            number = arguments.get("level2size");
        }
        try {
            repository.setHDDCacheEntriesNumber(Integer.parseInt(number));
            if (repository.getHDDCacheEntriesNumber() < Repository.HDD_CACHE_ENTRIES_MINIMUM) {
                throw new NumberFormatException();
            }
            repository.getLogger().info("Level 2 cache size is set to "
                    + repository.getHDDCacheEntriesNumber());
        } catch (NumberFormatException nfe) {
            repository.getLogger().info("Level 2 cache size is not set, using default - "
                    + Repository.HDD_CACHE_ENTRIES_DEFAULT);
            repository.setHDDCacheEntriesNumber(Repository.HDD_CACHE_ENTRIES_DEFAULT);
        }
        
        if (repository.isDetailedReport()) {
            repository.getLogger().info("Caching process report is set to be "
                    + "detailed.");
        } else {
            repository.getLogger().info("Caching process report is set to be "
                    + "not detailed.");
        }
        
        if (arguments.get("cachekind") == null
                && arguments.get("ck") == null) {
            repository.getLogger().info("Cache kind is not set, used default - "
                    + "Most Recently Used.");
            repository.setCacheKind(Repository.cacheKindEnum.MRU);
//            repository.getLogger().info("");
        } else {
            String ck = arguments.get("cachekind");
            if (ck == null) {
                ck = arguments.get("ck");
            }
            switch (ck) {
                case "lfu": {
                }
                case "LFU": {
                    repository.setCacheKind(Repository.cacheKindEnum.LFU);
                    break;
                }
                case "lru": {
                }
                case "LRU": {
                    repository.setCacheKind(Repository.cacheKindEnum.LRU);
                    break;
                }
                case "mru": {
                }
                case "MRU": {
                    repository.setCacheKind(Repository.cacheKindEnum.MRU);
                    break;
                }
            }
            repository.getLogger().info("cachekind is set to - "
                    + repository.getCacheKind());
//            repository.getLogger().info("");
        }
        
        /**
         * Defining a cache process run times, i.e. how many times a caching 
         * process is to run.
         */
        try {
            number = arguments.get("n");
            repository.setPipelineRunTimes(Integer.parseInt(number));
            repository.getLogger().info("Cache process will run for " 
                    + repository.getPipelineRunTimes() + " times");
        } catch (Exception ex) {
            repository.getLogger().info("Cache process run times is not set, "
                    + "using default - " + repository.getPipelineRunTimes());
            repository.setCacheKind(Repository.cacheKindEnum.MRU);
        }

        
        repository.getLogger().info("");
        
        // Uncomment in case of willing to see a full list of key-value pairs.
//        printArgs(arguments);
    }

    // Printing the command line arguments
    private void printArgs(Map<String, String> map) {
        if (map != null) {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                System.out.println(key + "=" + value);
            }
        } else {
            System.out.println("Command line is empty !");
            System.exit(1);
        }
    }

}
