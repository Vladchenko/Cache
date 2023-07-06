package ru.cache.vlad.yanchenko.caches.hierarchy.disk;

import android.support.annotation.NonNull;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.caches.hierarchy.AbstractCache;
import ru.cache.vlad.yanchenko.exceptions.NotPresentException;
import ru.cache.vlad.yanchenko.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Disk abstract cache.
 *
 * @param <T> key to get an entry by.
 * @param <V> value(entry) of cache.
 */
public abstract class AbstractDiskCache<T, V> extends AbstractCache<T, V> implements ICache<T, V> {

    /**
     * TODO
     */
    protected AbstractDiskCache(int size) {
        this.size = size;
    }

    @Override
    public V getEntry(@NonNull T key) throws IOException, ClassNotFoundException, NotPresentException {
        V cacheEntry;
        // Serializing object
        try (FileInputStream fos = new FileInputStream((String) cacheEntries.get(key));
             ObjectInputStream ous = new ObjectInputStream(fos)) {
            cacheEntry = (V) ous.readObject();
        }
        return cacheEntry;
    }

    @Override
    public void putEntry(@NonNull T key, @NonNull V cacheEntry) throws IOException {
        String fullFileName = FileUtils.FILES_FOLDER + FileUtils.FILE_PREFIX + key + FileUtils.FILE_EXTENSION;
        // Deserializing object
        try (FileOutputStream fos = new FileOutputStream(fullFileName);
             ObjectOutputStream ous = new ObjectOutputStream(fos)) {
            ous.writeObject(cacheEntry);
        }
        cacheEntries.put(key, (V) fullFileName);
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
//            mapFrequency.remove(key);     //TODO This one move to child and before its call, call super.removeEntry()
        cacheEntries.remove(key);
    }

    @Override
    public void clearCache() throws IOException {
        super.clearCache();
        File dir = new File(FileUtils.FILES_FOLDER);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                Files.delete(file.toPath());
            }
        }
    }

    @Override
    public boolean hasCacheEntry(@NonNull T key) {
        // TODO Maybe just cacheEntries.containsKey(key); ?
        File file = new File(
                FileUtils.FILES_FOLDER
                        + FileUtils.FILE_PREFIX
                        + key
                        + FileUtils.FILE_EXTENSION);
        return file.exists();
    }
}
