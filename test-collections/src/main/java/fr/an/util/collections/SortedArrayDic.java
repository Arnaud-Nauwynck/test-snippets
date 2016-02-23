package fr.an.util.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * a Sorted Map<String,V> optimized for memory consuptions (and read operations)
 *
 * @param <V>
 */
public class SortedArrayDic<V> extends AbstractMap<String, V> {
    
    /**
     * Shared empty array instance used for empty instances.
     */
    private static final String[] EMPTY_ELEMENT_KEY = {};
    private static final Object[] EMPTY_ELEMENT_DATA = {};
    
    private static final int DEFAULT_ALLOC_SIZE = 4; 
    
    private int size;
    private String[] keys;
    private Object[] values;
    
    // ------------------------------------------------------------------------

    public SortedArrayDic() {
        this.keys = EMPTY_ELEMENT_KEY;
        this.values = EMPTY_ELEMENT_DATA;
    }
    
    public SortedArrayDic(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.keys = new String[initialCapacity];
        this.values = new Object[initialCapacity];
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsValue(Object value) {
        final int size = this.size;
        final Object[] values = this.values;
        if (value==null) {
            for(int i = 0; i < size; i++) {
                if (values[i] == null)
                    return true;
            }
        } else {
            for(int i = 0; i < size; i++) {
                if (value.equals(values[i]))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        final int size = this.size;
        final String[] keys = this.keys;
        if (key==null) {
            for(int i = 0; i < size; i++) {
                if (keys[i] == null)
                    return true;
            }
        } else {
            for(int i = 0; i < size; i++) {
                if (key.equals(keys[i]))
                    return true;
            }
        }
        return false;
    }

    @Override
    public final V get(Object key) {
        if (!(key instanceof String)) return null;
        return get((String) key);
    }

    @SuppressWarnings("unchecked")
    public final V get(String key) {
        switch(size) {
        case 0: return null;
        case 1: return (key == keys[0] || (key != null && key.equals(keys[0])))? (V) values[0] : null;
        default:
            int index = searchKeyIndex(key);
            if (index < 0) return null;
            return (V) values[index];
        }
    }

    public final int searchKeyIndex(String key) {
        if (size == 0 || key == null) return -1;
        int index = binarySearchWithPrefix(0, size, key, 0);
        return index;
    }
    
    /**
     * cf Arrays.binarySearch().. but using common prefix in String
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param toIndex the index of the last element (exclusive) to be searched
     * @param key the value to be searched for
     * @param prefixLen common prefix len of elements in [fromIndex, toIndex(
     * @return
     */
    private int binarySearchWithPrefix(int fromIndex, int toIndex, String key, int prefixLen) {
        int res = binarySearch0(this.keys, fromIndex, toIndex, key, prefixLen);
        return res;
    }

    private static int binarySearch0(String[] a, int fromIndex, int toIndex, String key, int prefixLen) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            String midVal = a[mid];
            int cmp = midVal.compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1); // key not found.
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public V put(String key, V value) {
        if (size == 0) {
            if (keys.length == 0) {
                this.keys = new String[DEFAULT_ALLOC_SIZE];
                this.values = new Object[DEFAULT_ALLOC_SIZE];
            }
            size = 1;
            keys[0] = key;
            values[0] = value;
            return null;
        }
        int index = searchKeyIndex(key);
        V res;
        if (index < 0) {
            // this is a new value to insert (not a duplicate).
            index = - index - 1;
            res = null;
        } else {
            res = (V) values[index];
        }
        if (size < keys.length) {
            System.arraycopy(keys, index, keys, index+1, size - index);
            System.arraycopy(values, index, values, index+1, size - index);
            keys[index] = key;
            values[index] = value;
            size++; 
        } else {
            // grow... realloc + copy
            String[] srcKeys = keys;
            Object[] srcValues = values;
            int newAlloc = size + 1 + (size>> 1); // heuristic..
            keys = new String[newAlloc]; 
            values = new Object[newAlloc]; 
            System.arraycopy(srcKeys, 0, keys, 0, index);
            System.arraycopy(srcValues, 0, values, 0, index);
            keys[index] = key;
            values[index] = value;
            System.arraycopy(srcKeys, index, keys, index+1, size - index);
            System.arraycopy(srcValues, index, values, index+1, size - index);
            size++;
        }
        return res;
    }

    @Override
    public final V remove(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return remove((String) key);
    }
    
    public V remove(String key) {
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            if (keys[0] == key || (keys[0]!=null && keys[0].equals(key))) {
                size = 0;
                @SuppressWarnings("unchecked")
                V prev = (V) values[0];
                keys[0] = null;
                values[0] = null;
                return prev;
            } else {
                return null;
            }
        }
        int index = searchKeyIndex(key);
        if (index < 0) {
            return null;
        }
        @SuppressWarnings("unchecked")
        V res = (V) values[index];
        System.arraycopy(keys, index+1, keys, index, size-index-1);
        System.arraycopy(values, index+1, values, index, size-index-1);
        size--;
        return res;
    }

    @Override
    public void clear() {
        final int prevSize = size;
        this.size = 0;
        switch(prevSize) {
        case 0: 
            break;
        case 1:
            keys[0] = null;
            values[0] = null;
            break;
        default:
            Arrays.fill(this.keys, 0, prevSize, null);
            Arrays.fill(this.values, 0, prevSize, null);
        }
    }

    @Override
    public Set<String> keySet() {
        switch(size) {
        case 0: return Collections.emptySet();
        case 1: return Collections.singleton(keys[0]);
        default: return new AbstractSet<String>() {
            @Override
            public Iterator<String> iterator() {
                return new KeyItr();
            }
            @Override
            public int size() {
                return size;
            }
        };
        }
    }

    private class KeyItr implements Iterator<String> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        
        public boolean hasNext() {
            return cursor != size;
        }

        public String next() {
            // checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            String [] keys = SortedArrayDic.this.keys;
            if (i >= keys.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return keys[lastRet = i];
        }
        
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            // checkForComodification();
            try {
                SortedArrayDic.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                // expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        switch(size) {
        case 0: return Collections.emptySet();
        case 1: return Collections.singleton((V) values[0]);
        default: return new AbstractSet<V>() {
            @Override
            public Iterator<V> iterator() {
                return new ValueItr();
            }
            @Override
            public int size() {
                return size;
            }

        };
        }
    }


    private class ValueItr implements Iterator<V> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public V next() {
            // checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] values = SortedArrayDic.this.values;
            if (i >= keys.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (V) values[lastRet = i];
        }
        
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            // checkForComodification();
            try {
                SortedArrayDic.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                // expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Set<java.util.Map.Entry<String, V>> entrySet() {
        switch(size) {
        case 0: return Collections.emptySet();
        case 1: return Collections.<Map.Entry<String, V>>singleton(new SimpleEntry<String,V>(keys[0], (V) values[0]));
        default: return new AbstractSet<java.util.Map.Entry<String, V>>() {
            @Override
            public Iterator<java.util.Map.Entry<String, V>> iterator() {
                return new EntryItr();
            }
            @Override
            public int size() {
                return size;
            }

        };
        }
    }


    private class EntryItr implements Iterator<java.util.Map.Entry<String, V>> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public Map.Entry<String, V> next() {
            // checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            String[] keys = SortedArrayDic.this.keys;
            Object[] values = SortedArrayDic.this.values;
            if (i >= keys.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            lastRet = i;
            return new SimpleEntry<String,V>(keys[i], (V) values[i]);
        }
        
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            // checkForComodification();
            try {
                SortedArrayDic.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                // expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    
    // ------------------------------------------------------------------------

    @Override
    protected Object clone() {
        try {
            SortedArrayDic<?> v = (SortedArrayDic<?>) super.clone();
            v.size = size;
            v.keys = Arrays.copyOf(keys, size);
            v.values = Arrays.copyOf(values, size);
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map))
            return false;
        Map<?,?> m = (Map<?,?>) o;
        if (m.size() != size())
            return false;

        try {
            final int size = this.size;
            final String[] keys = this.keys;
            final Object[] values = this.values;
            for(int i = 0; i < size; i++) {
                String key = keys[i];
                Object value = values[i];
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        final int size = this.size;
        final String[] keys = this.keys;
        final Object[] values = this.values;
        for(int i = 0; i < size; i++) {
            String key = keys[i];
            Object value = values[i];
            h = + (key   == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
        return h;
    }

    @Override
    public String toString() {
        final int size = this.size;
        if (size == 0)
            return "{}";
        final String[] keys = this.keys;
        final Object[] values = this.values;
        StringBuilder sb = new StringBuilder(2 + size << 4);
        sb.append('{');
        for(int i = 0; i < size; i++) {
            String key = keys[i];
            Object value = values[i];
            sb.append(key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (i + 1 != size)
                sb.append(',').append(' ');
        }
        return sb.append('}').toString();
    }
    
}
