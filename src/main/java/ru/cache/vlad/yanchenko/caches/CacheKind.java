package ru.cache.vlad.yanchenko.caches;

/**
 * Define cache kind.
 */
public enum CacheKind {
    /**
     * Least frequently used
     */
    LFU,
    /**
     * Least recently used
     */
    LRU,
    /**
     * Most recently used
     */
    MRU
}
