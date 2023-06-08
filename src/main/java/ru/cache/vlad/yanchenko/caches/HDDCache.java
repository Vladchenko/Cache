package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static ru.cache.vlad.yanchenko.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.ArgumentsConstants.LEVEL_2_CACHE_SIZE_ARGUMENT_KEY;

/**
 * In charge of an operations made with a RAM cache.
 *
 * @author v.yanchenko
 */
public class HDDCache extends AbstractCache implements Serializable, ICache {

    private final Map<String, String> mArguments;

    private int mCacheHits = 0;
    private int mCacheMisses = 0;
    // Number of entries to be present in cache.
    private int mCacheEntriesNumber;

    /**
     * Create an instance of this class
     *
     * @param arguments from command line
     */
    public HDDCache(@NonNull Map<String, String> arguments) {
        mArguments = arguments;
        mCacheEntriesNumber = Integer.parseInt(mArguments.get(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY));
        switch (CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
            case LFU, MRU -> mCacheEntries = new HashMap<>();
            case LRU -> mCacheEntries = new LinkedHashMap<>();
        }
    }

    @Override
    public Map<Object, Object> getCacheEntries() {
        return mCacheEntries;
    }

    @Override
    public void clearCache() throws IOException {
        File dir = new File(FileUtils.FILES_FOLDER);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                Files.delete(file.toPath());
            }
        }
        mCacheEntries.clear();
//        mapFrequency.clear();
        mSize = 0;
    }

    // Uploading file to RAM.
    @Override
    public Object getEntry(@NonNull Object key) throws IOException, ClassNotFoundException {
        Object obj;
        FileInputStream fos = null;
        ObjectInputStream ous = null;
        // Serializing object
        try {
            fos = new FileInputStream((String) mCacheEntries.get(key));
            ous = new ObjectInputStream(fos);
            obj = ous.readObject();
            // Increasing a call count for this entry.
//            mapFrequency.put(key, mapFrequency.get(key) + 1);
            mLastAccessedEntryKey = key;
            switch (CacheKind.valueOf(mArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
                case LFU -> {
                    // TODO
                }
                case LRU -> {
                    obj = mCacheEntries.get(key);
                    mCacheEntries.remove(key);
                    mCacheEntries.put(key, obj);
                }
                case MRU -> {
                    mLastAccessedEntryKey = key;
                    return mLastAccessedEntryKey;
                }
                default -> {
                    // TODO
                }
            }
        } finally {
            if (ous != null) {
                ous.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return obj;
    }

    // Saving file to disk.
    @Override
    public void putEntry(@NonNull Object key, @NonNull Object obj) throws IOException {
        String fullFileName = FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION;
        FileOutputStream fos;
        ObjectOutputStream ous;
        // Deserializing object
        fos = new FileOutputStream(fullFileName);
        ous = new ObjectOutputStream(fos);
        ous.writeObject(obj);
        if (ous != null) {
            ous.close();
        }
        if (fos != null) {
            fos.close();
        }
//        File file = new File(fullFileName);
//        cacheSize += file.length();
        mSize++;
//        mapFrequency.put(key, 1);
        mCacheEntries.put(key, fullFileName);
        mLastAccessedEntryKey = key;
    }

    @Override
    public void removeEntry(@NonNull Object key) throws NotPresentException {
        File file = new File(FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
//            mapFrequency.remove(key);
            mCacheEntries.remove(key);
        } else {
            throw new NotPresentException("\tEntry with key=" + key + ", value=" + file.getName()
                    + " is absent in cache");
        }
    }

    @Override
    public boolean hasCacheEntry(@NonNull Object key) {
        File file = new File(
                FileUtils.FILES_FOLDER
                        + FileUtils.FILE_PREFIX
                        + key
                        + FileUtils.FILE_EXTENSION);
        return file.exists();
    }

    @Override
    public Object getLeastUsedEntry(@NonNull CacheKind cacheKind) {
        switch (cacheKind) {
            case LFU -> {
            }
            case LRU -> {
                // Getting the first key from a map of objects, since first is the one that was used least recently.
                return mCacheEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return mLastAccessedEntryKey;
            }
            default -> {
            }
        }
        return null;
    }

    @Override
    public int getCacheHits() {
        return mCacheHits;
    }

    @Override
    public void setCacheHits(int hitsHDDCache) {
        mCacheHits = hitsHDDCache;
    }

    @Override
    public int getCacheMisses() {
        return mCacheMisses;
    }

    @Override
    public void setCacheMisses(int missesHDDCache) {
        mCacheMisses = missesHDDCache;
    }

    @Override
    public int getEntriesNumber() {
        return mCacheEntriesNumber;
    }

    @Override
    public void setEntriesNumber(int entriesNumber) {
        mCacheEntriesNumber = entriesNumber;
    }

    @Override
    public void resetCacheStatistics() {
        mCacheMisses = 0;
        mCacheHits = 0;
    }

    /**
     * TODO
     *
     * @param hddCacheEntriesNumber
     */
    public void setHDDCacheEntriesNumber(int hddCacheEntriesNumber) {
        mCacheEntriesNumber = hddCacheEntriesNumber;
    }
}
