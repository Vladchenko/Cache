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

    // Keeps a couples of "value=key" of a command line arguments
    private Map<String, String> arguments = new HashMap();
    // Default sizes for cache (in case defined ones are wrong)
    private static final int LEVEL1CACHECSIZEDEFAULT = 10;
    private static final int LEVEL2CACHECSIZEDEFAULT = 10;
    // Minimum size that cache is allowed to have 
    private static final int LEVEL1CACHEMINIMUMVALUE = 1;
    private static final int LEVEL2CACHEMINIMUMVALUE = 1;
    private int level1CacheSize = 0;
    private int level2CacheSize = 0;
    private String cacheKind;

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
            setLevel1CacheSize(Integer.parseInt(number));
            if (getLevel1CacheSize() < getLEVEL1CACHEMINIMUMVALUE()) {
                throw new NumberFormatException();
            }
            System.out.println("Level 1 cache size is set to "
                    + getLevel1CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 1 cache size is specified in a wrong way ! Used by default.");
            setLevel1CacheSize(getLEVEL1CACHECSIZEDEFAULT());
        }

        // ProcessArguments a level2 cache size value
        number = arguments.get("level2Cache");
        try {
            setLevel2CacheSize(Integer.parseInt(number));
            if (getLevel2CacheSize() < getLEVEL2CACHEMINIMUMVALUE()) {
                throw new NumberFormatException();
            }
            System.out.println("Level 2 cache size is set to "
                    + getLevel2CacheSize());
        } catch (NumberFormatException nfe) {
            System.out.println("Level 2 cache size is specified in a wrong way ! Used by default.");
            setLevel2CacheSize(getLEVEL2CACHECSIZEDEFAULT());
        }
        setCacheKind(arguments.get("cachekind"));
        if (getCacheKind().isEmpty()) {
            System.out.println("cachekind is not set, used default - least recently used");
            setCacheKind("lru");
        } else {
            System.out.println("cachekind is set to - " + getCacheKind());
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

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    /**
     * @return the LEVEL1CACHECSIZEDEFAULT
     */
    public static int getLEVEL1CACHECSIZEDEFAULT() {
        return LEVEL1CACHECSIZEDEFAULT;
    }

    /**
     * @return the LEVEL2CACHECSIZEDEFAULT
     */
    public static int getLEVEL2CACHECSIZEDEFAULT() {
        return LEVEL2CACHECSIZEDEFAULT;
    }

    /**
     * @return the LEVEL1CACHEMINIMUMVALUE
     */
    public static int getLEVEL1CACHEMINIMUMVALUE() {
        return LEVEL1CACHEMINIMUMVALUE;
    }

    /**
     * @return the LEVEL2CACHEMINIMUMVALUE
     */
    public static int getLEVEL2CACHEMINIMUMVALUE() {
        return LEVEL2CACHEMINIMUMVALUE;
    }

    /**
     * @return the arguments
     */
    public Map<String, String> getArguments() {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    /**
     * @return the level1CacheSize
     */
    public int getLevel1CacheSize() {
        return level1CacheSize;
    }

    /**
     * @param level1CacheSize the level1CacheSize to set
     */
    public void setLevel1CacheSize(int level1CacheSize) {
        this.level1CacheSize = level1CacheSize;
    }

    /**
     * @return the level2CacheSize
     */
    public int getLevel2CacheSize() {
        return level2CacheSize;
    }

    /**
     * @param level2CacheSize the level2CacheSize to set
     */
    public void setLevel2CacheSize(int level2CacheSize) {
        this.level2CacheSize = level2CacheSize;
    }

    /**
     * @return the cacheKind
     */
    public String getCacheKind() {
        return cacheKind;
    }

    /**
     * @param cacheKind the cacheKind to set
     */
    public void setCacheKind(String cacheKind) {
        this.cacheKind = cacheKind;
    }
//</editor-fold>
}
