package org.kitteh.trackr.util;

import java.util.Map;

/**
 * It's a map! With lowercase String keys! And a limited methods list!
 * 
 * @param <V>
 *            Value type
 */
public class LCMap<V> {
    private final Map<String, V> map;

    public LCMap(Map<String, V> starter) {
        this.map = starter;
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key.toString().toLowerCase());
    }

    public V get(Object key) {
        return this.map.get(key.toString().toLowerCase());
    }

    public V put(String key, V value) {
        return this.map.put(key.toLowerCase(), value);
    }

    public V remove(Object key) {
        return this.map.remove(key.toString().toLowerCase());
    }
}