package fr.an.util.collections;

import java.util.Map;

public class ImmutableMapEntry<K,V> implements Map.Entry<K,V> {
    
    private final K key;
    private final V value;

    // ------------------------------------------------------------------------
    
    public ImmutableMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // ------------------------------------------------------------------------

    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
    
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
    
}
