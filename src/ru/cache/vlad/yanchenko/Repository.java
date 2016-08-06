/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cache.vlad.yanchenko;

import java.io.IOException;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    public static final String FILEPREFIX = "cache_file_";
    public static final String FILEEXT = ".cache";
    public static final String FILESFOLDER = "Cached Data\\";

    private int level1CacheSize;
    private int level2CacheSize;

    private boolean detailedReport = false;

//    private String cacheKind;
    public enum cacheKindEnum {
        NONE, LFU, LRU, MRU, LRR
    };
    cacheKindEnum cacheKind;

    Logger logger = Logger.getLogger("myLog");
    FileHandler fileHandler;

    private Repository() {
        
        cacheKind = cacheKindEnum.NONE;

        // Setting up a logging
        try {
            fileHandler = new FileHandler("Cache_log.log", 100000, 1);
            MyFormatter formatter = new MyFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Repository getInstance() {
        if (oRepository == null) {
            oRepository = new Repository();
        }
        return oRepository;
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    /**
     * @return the cacheKind
     */
    public cacheKindEnum getCacheKind() {
        return cacheKind;
    }

    /**
     * @param cacheKind the cacheKind to set
     */
    public void setCacheKind(cacheKindEnum cacheKind) {
        this.cacheKind = cacheKind;
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
//</editor-fold>

}

    // Overriding an implementation of a standard formatter.
class MyFormatter extends SimpleFormatter {

    @Override
    public synchronized String format(LogRecord record) {
        return record.getMessage() + System.getProperty("line.separator");
    }
    
}
