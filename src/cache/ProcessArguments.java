/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author v.yanchenko
 */
public class ProcessArguments {

    public static Repository repository = Repository.getInstance();
    
    public ProcessArguments(Repository repository) {
        this.repository = repository;
    }

    // Putting a couples of "value=key" to a map
    public void processArgs(String[] args) {
        String number;
        String delims = "[=]+";    // Use = as a delimiter
        String[] tokens1 = new String[args.length];
        Map<String, String> arguments = new HashMap();
        for (String arg : args) {
            String[] map = arg.split(delims);
            try {
                if (!map[0].isEmpty()) {
                    arguments.put(map[0], map[1]);
                }
            } catch (Exception ex) {
                System.out.println("Wrong argument present...");
            }
        }

        // Processing arguments for level1 cache
        number = arguments.get("level1Cache");
        try {
            repository.setLevel1CacheSize(Integer.parseInt(number));
            if (repository.getLevel1CacheSize() < Repository.LEVEL1CACHEMINIMUMVALUE) {
                throw new NumberFormatException();
            }
            System.out.println("Level 1 cache size is set to "
                    + repository.getLevel1CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 1 cache size is specified in a wrong way ! Used by default.");
            repository.setLevel1CacheSize(Repository.LEVEL1CACHECSIZEDEFAULT);
        }

        // Processing arguments for level2 cache
        number = arguments.get("level2Cache");
        try {
            repository.setLevel2CacheSize(Integer.parseInt(number));
            if (repository.getLevel2CacheSize() < Repository.LEVEL2CACHEMINIMUMVALUE) {
                throw new NumberFormatException();
            }
            System.out.println("Level 2 cache size is set to "
                    + repository.getLevel2CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 2 cache size is specified in a wrong way ! Used by default.");
            repository.setLevel2CacheSize(Repository.LEVEL2CACHECSIZEDEFAULT);
        }
        repository.setCacheKind(arguments.get("cachekind"));
        if (repository.getCacheKind() == null
                || repository.getCacheKind().isEmpty()) {
            System.out.println("cachekind is not set, used default - least recently used");
            repository.setCacheKind("lru");
        } else {
            System.out.println("cachekind is set to - " + repository.getCacheKind());
        }

        // Uncomment in case want to see a full list of key-value pairs
//        printArgs(arguments);
    }

    // Pringting the command line arguments
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
