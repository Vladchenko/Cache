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

    public static Repository oRepository = Repository.getInstance();
    
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

        // ProcessArguments a level1 cache size value
        number = arguments.get("level1Cache");
        try {
            oRepository.setLevel1CacheSize(Integer.parseInt(number));
            if (oRepository.getLevel1CacheSize() < oRepository.getLEVEL1CACHEMINIMUMVALUE()) {
                throw new NumberFormatException();
            }
            System.out.println("Level 1 cache size is set to "
                    + oRepository.getLevel1CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 1 cache size is specified in a wrong way ! Used by default.");
            oRepository.setLevel1CacheSize(oRepository.getLEVEL1CACHECSIZEDEFAULT());
        }

        // ProcessArguments a level2 cache size value
        number = arguments.get("level2Cache");
        try {
            oRepository.setLevel2CacheSize(Integer.parseInt(number));
            if (oRepository.getLevel2CacheSize() < oRepository.getLEVEL2CACHEMINIMUMVALUE()) {
                throw new NumberFormatException();
            }
            System.out.println("Level 2 cache size is set to "
                    + oRepository.getLevel2CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 2 cache size is specified in a wrong way ! Used by default.");
            oRepository.setLevel2CacheSize(oRepository.getLEVEL2CACHECSIZEDEFAULT());
        }
        oRepository.setCacheKind(arguments.get("cachekind"));
        if (oRepository.getCacheKind().isEmpty()) {
            System.out.println("cachekind is not set, used default - least recently used");
            oRepository.setCacheKind("lru");
        } else {
            System.out.println("cachekind is set to - " + oRepository.getCacheKind());
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
