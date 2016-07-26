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
public class ProcessArgumetns {

    // Keeps a couples of "value=key" arguments
    Map<String, String> arguments = new HashMap();
    // Default sizes for cache (in case defined ones are wrong)
    private static final int LEVEL1CACHECSIZEDEFAULT = 10;
    private static final int LEVEL2CACHECSIZEDEFAULT = 10;
    // Minimum size that cache is alowed to have 
    private static final int LEVEL1CACHEMINIMUMVALUE = 1;
    private static final int LEVEL2CACHEMINIMUMVALUE = 1;
    private int level1CacheSize = 0;
    private int level2CacheSize = 0;

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
        
        // ProcessArgumetns a level1 cache size value
        number = arguments.get("level1Cache");
        try {
            level1CacheSize = Integer.parseInt(number);
            if (level1CacheSize < LEVEL1CACHEMINIMUMVALUE) {
                throw new NumberFormatException();
            }
            System.out.println("Level 1 cache size has been set to " 
                    + level1CacheSize);
        } catch (NumberFormatException nfe) {
            System.out.println("Level 1 cache size is specified in a wrong way ! Used by default.");
            level1CacheSize = LEVEL1CACHECSIZEDEFAULT;
        }
        
        // ProcessArgumetns a level2 cache size value
        number = arguments.get("level2Cache");
        try {
            level2CacheSize = Integer.parseInt(number);
            if (level2CacheSize < LEVEL2CACHEMINIMUMVALUE) {
                throw new NumberFormatException();
            }
            System.out.println("Level 2 cache size has been set to " 
                    + level2CacheSize);
        } catch (NumberFormatException nfe) {
            System.out.println("Level 2 cache size is specified in a wrong way ! Used by default.");
            level2CacheSize = LEVEL2CACHECSIZEDEFAULT;
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
