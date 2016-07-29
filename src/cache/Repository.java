/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

/**
 *
 * @author v.yanchenko
 */
public class Repository {

    public static Repository oRepository = Repository.getInstance();
    public static final int LEVEL1CACHECSIZEDEFAULT = 10;
    public static final int LEVEL2CACHECSIZEDEFAULT = 10;
    public static final int LEVEL1CACHEMINIMUMVALUE = 1;
    public static final int LEVEL2CACHEMINIMUMVALUE = 1;
    public static final String FILEPREFIX = "cache_File_";
    public static final String FILEEXT = ".cache";
    public static final String FILESFOLDER = "Cached Data\\";

    private int level1CacheSize;
    private int level2CacheSize;

    private String cacheKind;

    private Repository() {
    }

    public static Repository getInstance() {
        if (oRepository == null) {
            oRepository = new Repository();
        }
        return oRepository;
    }

    public int getLevel1CacheSize() {
        return level1CacheSize;
    }

    public void setLevel1CacheSize(int level1CacheSize) {
        this.level1CacheSize = level1CacheSize;
    }

    public int getLevel2CacheSize() {
        return level2CacheSize;
    }

    public void setLevel2CacheSize(int level2CacheSize) {
        this.level2CacheSize = level2CacheSize;
    }

    public String getCacheKind() {
        return cacheKind;
    }

    public void setCacheKind(String cacheKind) {
        this.cacheKind = cacheKind;
    }

}
