package ru.cache.vlad.yanchenko.caches;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_KIND_ARGUMENT_KEY;
import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.LEVEL_2_CACHE_SIZE_ARGUMENT_KEY;

/**
 * In charge of an operations done with an HDD cache.
 *
 * @author v.yanchenko
 */
public class HDDCache<T, V> extends AbstractCache<T, V> implements Serializable, ICache<T, V> {

    private final Map<String, String> commandLineArguments;

    private int cacheHits = 0;
    private int cacheMisses = 0;
    // Number of entries to be present in cache.
    private int cacheEntriesNumber;

    /**
     * Create an instance of this class
     *
     * @param arguments from command line
     */
    public HDDCache(@NonNull Map<String, String> arguments) {
        commandLineArguments = arguments;
        cacheEntriesNumber = Integer.parseInt(commandLineArguments.get(LEVEL_2_CACHE_SIZE_ARGUMENT_KEY));
        switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
            case LFU, MRU -> cacheEntries = new HashMap<>();
            case LRU -> cacheEntries = new LinkedHashMap<>();
        }
    }

    @Override
    public Map<T, V> getCacheEntries() {
        return cacheEntries;
    }

    @Override
    public void clearCache() throws IOException {
        File dir = new File(FileUtils.FILES_FOLDER);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                Files.delete(file.toPath());
            }
        }
        cacheEntries.clear();
//        mapFrequency.clear();
        size = 0;
    }

    // Uploading file to RAM.
    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException {
        V obj;
        // Serializing object
        try (FileInputStream fos = new FileInputStream((String) cacheEntries.get(key));
             ObjectInputStream ous = new ObjectInputStream(fos)) {
            obj = (V) ous.readObject();
            // Increasing a call count for this entry.
//            mapFrequency.put(key, mapFrequency.get(key) + 1);
            lastAccessedEntryKey = key;
            switch (CacheKind.valueOf(commandLineArguments.get(CACHE_KIND_ARGUMENT_KEY))) {
                case LFU -> {
                    // TODO
                }
                case LRU -> {
                    obj = cacheEntries.get(key);
                    cacheEntries.remove(key);
                    cacheEntries.put(key, obj);
                }
                case MRU -> {
                    lastAccessedEntryKey = key;
                    obj = cacheEntries.get(lastAccessedEntryKey);
                }
                default -> {
                    // TODO
                }
            }
        }
        return obj;
    }

    // Saving file to disk.
    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) throws IOException {
        String fullFileName = FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION;
        // Deserializing object
        try (FileOutputStream fos = new FileOutputStream(fullFileName);
             ObjectOutputStream ous = new ObjectOutputStream(fos)) {
            ous.writeObject(cacheEntry);
        }
        size++;
//        mapFrequency.put(key, 1);
        cacheEntries.put(key, (V) fullFileName);
        lastAccessedEntryKey = key;
    }

    @Override
    public void removeEntry(@NonNull T key) throws NotPresentException {
        Path filePath = Path.of(FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NotPresentException("\tEntry with key=" + key + ", value=" + filePath + " is absent in cache");
        }
//            mapFrequency.remove(key);
        cacheEntries.remove(key);
    }

    @Override
    public boolean hasCacheEntry(@NonNull T key) {
        File file = new File(
                FileUtils.FILES_FOLDER
                        + FileUtils.FILE_PREFIX
                        + key
                        + FileUtils.FILE_EXTENSION);
        return file.exists();
    }

    @Override
    public T getLeastUsedEntryKey(@NonNull CacheKind cacheKind) {
        switch (cacheKind) {
            case LFU -> {
                //TODO
            }
            case LRU -> {
                // Getting the first key from a map of cache entries, since first is the one that was used least recently.
                return cacheEntries.entrySet().iterator().next().getKey();
            }
            case MRU -> {
                return lastAccessedEntryKey;
            }
            default -> {
                //TODO
            }
        }
        return null;
    }

    @Override
    public int getCacheHits() {
        return cacheHits;
    }

    @Override
    public void setCacheHits(int hitsHDDCache) {
        cacheHits = hitsHDDCache;
    }

    @Override
    public int getCacheMisses() {
        return cacheMisses;
    }

    @Override
    public void setCacheMisses(int missesHDDCache) {
        cacheMisses = missesHDDCache;
    }

    @Override
    public int getEntriesNumber() {
        return cacheEntriesNumber;
    }

    @Override
    public void resetCacheStatistics() {
        cacheMisses = 0;
        cacheHits = 0;
    }
}
