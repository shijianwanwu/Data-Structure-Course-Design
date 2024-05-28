package com.byr.project.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 使用缓存对查询进行优化
 * * 使用LRU（最近最少使用）策略：使用一个基于LRU策略的缓存，当缓存满时，LRU策略会移除最近最少使用的缓存项。
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int cacheSize;

    public LRUCache(int cacheSize) {
        super(16, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= cacheSize;
    }
}