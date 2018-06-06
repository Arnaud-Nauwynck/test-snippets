package fr.an.util.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.impl.util.LigthweightMapEntry;

/**
 * a simpler partial replacement for <code>LinkedHashMap<String,T></code>
 * with restriction that keys and values can not be null. 
 * <br/>
 * better optimized, for small in-memory consumption and optimal performance when using small maps in read-only.
 *
 * for medium to high size, performance of put()/remove() in O(N) like an ArrayList! 
 * (only lookup is fast, then inserting/removal need memory move, and optionnally re-allocating arrays and re-indexing all)
 * 
 * Traversing is O(1) like an ArrayList, both on key/values/entry
 * 
 * <p/> 
 * Algorithm & Data Structure layout:
 * key-values are stored in insertion order, first in embedded fields (from index 0 to 7), then in 
 * an alternate key-value.
 * For small map (size < 8), there is no need for any extra memory allocation, so this is Garbage Collector friendly.
 * For higher size (> 8), 2 extra arrays are allocated... re-hashing the whole map when put/remove is O(N) !
 * 
 * allocation for key-value and for hash is not the same size... hash array is an indirection (index) to key-value storage. 
 * 
 */
public final class ArrayHashStringMap<T> {

    private static final int NONE = -1;
    
    private static final int EMBEDDED_KV_LEN = 8;
    private static final int EMBEDDED_HASH_LEN = 16;
    private static final int EMBEDDED_HASH_MAX_CONFLICT = 3;

    private int size;
    
    private String key0;
    private T value0;
    private String key1;
    private T value1;
    private String key2;
    private T value2;
    private String key3;
    private T value3;
    private String key4;
    private T value4;
    private String key5;
    private T value5;
    private String key6;
    private T value6;
    private String key7;
    private T value7;
    private String[] remainingKeys; // = key16,key17,...
    private Object[] remainingValues; // = value6,value17, ...

    private int indexHash0 = -1;
    private int indexHash1 = -1;
    private int indexHash2 = -1;
    private int indexHash3 = -1;
    private int indexHash4 = -1;
    private int indexHash5 = -1;
    private int indexHash6 = -1;
    private int indexHash7 = -1;
    private int indexHash8 = -1;
    private int indexHash9 = -1;
    private int indexHash10 = -1;
    private int indexHash11 = -1;
    private int indexHash12 = -1;
    private int indexHash13 = -1;
    private int indexHash14 = -1;
    private int indexHash15 = -1; 
    private int[] remainingIndexHash; // = indexHash16, indexHash17, ...
    
    // ------------------------------------------------------------------------

    public ArrayHashStringMap() {
    }

    // ------------------------------------------------------------------------

    // @Override
    public int size() {
        return size;
    }

    // @Override
    public boolean isEmpty() {
        return size != 0;
    }

    // @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) return false; // should not occur
        return containsKey((String) key);
    }

    public boolean containsKey(String key) {
        final int hash = hash0(key);
        int entry = _findEmbeddedSlot(key, hash);
        if (entry != NONE) {
            return true;
        }
        // find in remaining
        if (remainingIndexHash != null) {
            entry = _findRemainingSlot(key, hash);
            if (entry != NONE) {
                return true;
            }
        }
        return false;
    }

    
    protected int findEntry(String key, int hash) {
        int res = _findEmbeddedSlot(key, hash);
        if (res != -1) {
            return res;
        }
        if (remainingIndexHash != null) {
            res = _findRemainingSlot(key, hash);
        }
        return res;
    }
    
    protected int _findEmbeddedSlot(String key, int hash) {
        int slot = embeddedHashSlot(hash);
        int index = embeddedIndexHashAt(slot);
        if (index == NONE) {
            return NONE;
        }
        String keyAt = keyAt(index);
        if (keyAt != null && key.equals(keyAt)) {
            return slot;
        }
        // find conflict in embedded
        int maxEmbeddedI = Math.min(slot + EMBEDDED_HASH_MAX_CONFLICT, EMBEDDED_HASH_LEN);
        for(slot = slot + 1; slot < maxEmbeddedI; slot++) {
            index = embeddedIndexHashAt(slot);
            if (index == NONE) {
                return NONE;
            }
            keyAt = keyAt(index);
            if (keyAt != null && key.equals(keyAt)) {
                return slot;
            }                
        }
        return NONE;
    }
    
    protected int _findRemainingSlot(String key, int hash) {
        int slot = remainingHashSlot(hash);
        int index = remainingIndexHash[slot];
        if (index == NONE) {
            return -1;
        }
        String keyAt = keyAt(index);
        if (key.equals(keyAt)) {
            return slot;
        }
        final int firstRemainSlot = slot;
        slot = nextRemainingSlot(slot);
        for(; slot != firstRemainSlot; slot = nextRemainingSlot(slot)) {
            index = remainingIndexHash[slot];
            if (index == NONE) {
                return NONE;
            }
            keyAt = keyAt(index);
            if (key.equals(keyAt)) {
                return slot;
            }
        }
        return NONE;
    }
    
//    // @Override
//    public boolean containsValue(Object value) {
//        // TODO Auto-generated method stub
//        return false;
//    }

    // @Override
    public T get(Object key) {
        if (!(key instanceof String)) return null; // should not occur
        return get((String) key); 
    }
    
    public T get(String key) {
        final int hash = hash0(key);
        int slot = _findEmbeddedSlot(key, hash);
        if (slot != -1) {
            int index = embeddedIndexHashAt(slot);
            return valueAt(index);
        }
        if (remainingIndexHash != null) {
            slot = _findRemainingSlot(key, hash);
            if (slot != -1) {
                int index = remainingIndexHashAt(slot);
                return valueAt(index);
            }
        }
        return null;
    }

    // @Override
    public T put(String key, T value) {
        final int hash = hash0(key);
        final int index = size;
        // find if override existing key (to fast replace, no remove/insert!)
        int foundIndex = findEntry(key, hash);
        if (foundIndex != -1) {
            T oldValue = valueAt(foundIndex);
            if (foundIndex < EMBEDDED_KV_LEN) {
                setEmbeddedKeyValueAt(foundIndex, key, value);
            } else {
                setRemainingKeyValueAt(foundIndex - EMBEDDED_KV_LEN, key, value);
            }
            // no need to update hash->index indirection
            return oldValue;
        } else {
            // insert new key/value
            if (size < EMBEDDED_KV_LEN) {
                // insert embedded key-value ...
                setEmbeddedKeyValueAt(index, key, value);
            } else {
                ensureIncreaseRemainingKeyValueLen(1);
                setRemainingKeyValueAt(index - EMBEDDED_KV_LEN, key, value);
            }
            size++;
            
            // then insert hash indirection either in embedded hash (if not too many conflicts) or in remaining hash
            int slot = embeddedHashSlot(hash);
            int maxEmbeddedSlot = Math.min(slot + EMBEDDED_HASH_MAX_CONFLICT, EMBEDDED_HASH_LEN);
            for(; slot < maxEmbeddedSlot; slot++) {
                int i = embeddedIndexHashAt(slot);
                if (i == NONE) {
                    // ok insert hash->index here
                    setEmbeddedIndexHashAt(slot, index);
                    return null;
                }
            }
            // else insert in remainingHash
            ensureIncreaseRemainingHashIndexLen(1);
            slot = findInsertRemainEntry(hash);
            setRemainingIndexHashAt(slot, index);
            return null;            
        }
    }

    protected int findInsertRemainEntry(int hash) {
        final int firstRemainSlot = remainingHashSlot(hash);
        int indexAt = remainingIndexHashAt(firstRemainSlot);
        if (indexAt == NONE) {
            return firstRemainSlot;
        }
        int i = nextRemainingSlot(firstRemainSlot);
        for(; i != firstRemainSlot; i = nextRemainingSlot(i)) {
            indexAt = remainingIndexHashAt(i);
            if (indexAt == NONE) {
                return i;
            }
        }
        throw new IllegalStateException();
    }

    private void ensureIncreaseRemainingKeyValueLen(int incr) {
        int minLen = size + incr;
        String[] k = remainingKeys;
        if (k == null) {
            remainingKeys = new String[16];
            remainingValues = new Object[16];
        } else {
            if (k.length <= minLen) {
                // need realloc
                int newLen = minLen + 8 + minLen >>> 1; // ~+50%  heuristic pre-allocation size
                remainingKeys = Arrays.copyOf(remainingKeys, newLen);
                remainingValues = Arrays.copyOf(remainingValues, newLen);
            }
        }
    }

    private void ensureIncreaseRemainingHashIndexLen(int incr) {
        int minLen = size + incr;
        if (remainingIndexHash == null) {
            remainingIndexHash = new int[23]; // heuristic pre-allocation size
            Arrays.fill(remainingIndexHash, NONE);
        } else {
            if (remainingIndexHash.length <= minLen) {
                final int[] prevIndexes = remainingIndexHash;
                int newLen = minLen + 16 + minLen >>> 1; // ~+50%  heuristic pre-allocation size
                int[] newIndexes = new int[newLen];
                Arrays.fill(newIndexes, NONE);
                remainingIndexHash = newIndexes;
                // need rehash full indexes!
                for (int i = 0; i < prevIndexes.length; i++) {
                    int prevI = prevIndexes[i];
                    if (prevI != NONE) {
                        String key = keyAt(prevI);
                        int hash = hash0(key);
                        int at = findInsertRemainEntry(hash);
                        newIndexes[at] = prevI;
                    }
                }                
            }
        }
    }

    // @Override
    public T remove(Object key) {
        if (!(key instanceof String)) return null;
        return remove((String) key);
    }

    public T remove(String key) {
        T res = null;
        final int hash = hash0(key);
        int index = NONE;
        // find and remove hash->index + shift indexes
        int foundSlot = _findEmbeddedSlot(key, hash);
        if (foundSlot != NONE) {
            // remove from embedded hasIndex + shift remaining conflicts + update hash index
            index = embeddedIndexHashAt(foundSlot);
            shiftRemoveEmbeddedIndexHash(foundSlot, index);
            if (size >= EMBEDDED_KV_LEN) {
                updateDecrRemainingHashIndex(index);
            }
        } else {
            if (remainingKeys != null) {
                foundSlot = _findRemainingSlot(key, hash);
                if (foundSlot != NONE) {
                    index = remainingIndexHashAt(foundSlot);
                    shiftRemoveRemainingIndexHash(foundSlot, index);
                }
            }
        }
        if (index == NONE) {
            return null;
        }
        // now remove from key-value
        size--;
        if (index < EMBEDDED_KV_LEN) {
            shiftRemoveEmbeddedKeyValueFrom(index);
            if (size >= EMBEDDED_KV_LEN) {
                setEmbeddedKeyValueAt(EMBEDDED_KV_LEN-1, remainingKeyAt(0), remainingValueAt(0));
                shiftRemoveRemainingKeyValues(0);
            }
        } else {
            shiftRemoveRemainingKeyValues(index - EMBEDDED_KV_LEN);
        }
        return res;
    }

    // @Override
    public void putAll(Map<String, T> m) {
        int incr = m.size();
        ensureIncreaseRemainingKeyValueLen(incr);
        ensureIncreaseRemainingHashIndexLen(incr);
        
        for(Map.Entry<String,T> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    // @Override
    public void clear() {
        for(int i = 0; i < EMBEDDED_KV_LEN; i++) {
            setEmbeddedKeyValueAt(i, null, null);
        }
        for (int i = 0; i < EMBEDDED_HASH_LEN; i++) {
            setEmbeddedIndexHashAt(i, NONE);
        }
        remainingIndexHash = null;
        remainingKeys = null;
        remainingValues = null;
    }

//    // @Override
//    public Set<String> keySet() {
//      throw FxUtils.notImpl();
//    }
//
//    // @Override
//    public Collection<T> values() {
//      throw FxUtils.notImpl();
//    }
//
//  // @Override
//  public Set<java.util.Map.Entry<String, T>> entrySet() {
//      throw FxUtils.notImpl();
//  }

    // @Override
    public Iterator<T> valuesIterator() {
        return new Iterator<T>() {
            int currIndex = -1;
            @Override
            public boolean hasNext() {
                return currIndex < size;
            }
            @Override
            public T next() {
                currIndex++;
                return valueAt(currIndex);
            }
        };
    }
    
    public Iterator<java.util.Map.Entry<String, T>> entrySetIterator() {
        return new Iterator<java.util.Map.Entry<String, T>>() {
            int currIndex = -1;
            LigthweightMapEntry<String,T> entry = new LigthweightMapEntry<>(null, null);
            @Override
            public boolean hasNext() {
                return currIndex < size;
            }
            @Override
            public Map.Entry<String,T> next() {
                currIndex++;
                entry._setCurr(keyAt(currIndex), valueAt(currIndex));
                return entry;
            }
        };
    }

    public Map<String,T> toLinkedHashMap() {
        Map<String,T> res = new LinkedHashMap<>();
        copyTo(res);
        return res;
    }

    public void copyTo(Map<String,T> res) {
        for(int i = 0; i < size; i++) {
            res.put(keyAt(i), valueAt(i));
        }
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(keyAt(i));
            sb.append(":");
            sb.append(valueAt(i));

            if (i + 1 < size) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    // ------------------------------------------------------------------------
        

    private static int hash0(String key) {
        int tmp = key.hashCode();
        if (tmp == NONE) {
            tmp = 1;
        }
        return tmp;
    }

    /*pp*/ static int embeddedHashSlot(int hashCode) {
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        int res = // hashCode % EMBEDDED_HASH_LEN; // 
                hashCode & (EMBEDDED_HASH_LEN - 1);
        assert (0 <= res && res < EMBEDDED_HASH_LEN);
        return res;
    }
    
    protected final int embeddedIndexHashAt(int i) {
        switch(i) {
        case 0: return indexHash0;
        case 1: return indexHash1;
        case 2: return indexHash2;
        case 3: return indexHash3;
        case 4: return indexHash4;
        case 5: return indexHash5;
        case 6: return indexHash6;
        case 7: return indexHash7;
        case 8: return indexHash8;
        case 9: return indexHash9;
        case 10: return indexHash10;
        case 11: return indexHash11;
        case 12: return indexHash12;
        case 13: return indexHash13;
        case 14: return indexHash14;
        case 15: return indexHash15;
        default: throw new IllegalArgumentException();
        }
    }
    
    protected final void setEmbeddedIndexHashAt(int i, int indexHash) {
        switch(i) {
        case 0: indexHash0 = indexHash; break;
        case 1: indexHash1 = indexHash; break;
        case 2: indexHash2 = indexHash; break;
        case 3: indexHash3 = indexHash; break;
        case 4: indexHash4 = indexHash; break;
        case 5: indexHash5 = indexHash; break;
        case 6: indexHash6 = indexHash; break;
        case 7: indexHash7 = indexHash; break;
        case 8: indexHash8 = indexHash; break;
        case 9: indexHash9 = indexHash; break;
        case 10: indexHash10 = indexHash; break;
        case 11: indexHash11 = indexHash; break;
        case 12: indexHash12 = indexHash; break;
        case 13: indexHash13 = indexHash; break;
        case 14: indexHash14 = indexHash; break;
        case 15: indexHash15 = indexHash; break;
        default: throw new IllegalArgumentException();
        }
    }
    
    protected final String _embeddedKeyAt(int i) {
        switch(i) {
        case 0: return key0;
        case 1: return key1;
        case 2: return key2;
        case 3: return key3;
        case 4: return key4;
        case 5: return key5;
        case 6: return key6;
        case 7: return key7;
        default: throw new IllegalArgumentException();
        }
    }

    protected final T _embeddedValueAt(int i) {
        switch(i) {
        case 0: return value0;
        case 1: return value1;
        case 2: return value2;
        case 3: return value3;
        case 4: return value4;
        case 5: return value5;
        case 6: return value6;
        case 7: return value7;
        default: throw new IllegalArgumentException();
        }
    }

    protected final String keyAt(int i) {
        if (i < EMBEDDED_KV_LEN) {
            return _embeddedKeyAt(i);
        } else {
            return remainingKeyAt(i - EMBEDDED_KV_LEN);
        }
    }

    protected final T valueAt(int i) {
        if (i < EMBEDDED_KV_LEN) {
            return _embeddedValueAt(i);
        } else {
            return remainingValueAt(i - EMBEDDED_KV_LEN);
        }
    }

    protected void setEmbeddedKeyValueAt(int i, String key, T value) {
        switch(i) {
        case 0: key0 = key; value0 = value; break;
        case 1: key1 = key; value1 = value; break;
        case 2: key2 = key; value2 = value; break;
        case 3: key3 = key; value3 = value; break;
        case 4: key4 = key; value4 = value; break;
        case 5: key5 = key; value5 = value; break;
        case 6: key6 = key; value6 = value; break;
        case 7: key7 = key; value7 = value; break;
        default: throw new IllegalArgumentException();
        }
    }
    
    protected void shiftRemoveEmbeddedKeyValueFrom(int i) {
        switch(i) {
        case 0: key0 = key1; value0 = value1; 
        case 1: key1 = key2; value1 = value2; // no break;
        case 2: key2 = key3; value2 = value3; // no break;
        case 3: key3 = key4; value3 = value4; // no break;
        case 4: key4 = key5; value4 = value5; // no break;
        case 5: key5 = key6; value5 = value6; // no break;
        case 6: key6 = key7; value6 = value7; // no break;
        case 7: key7 = null; value7 = null; // no break;
            break;
        default: throw new IllegalArgumentException();
        }
    }


    private void shiftRemoveEmbeddedIndexHash(int slot, int prevIndex) {
        // remove index at slot ... shift following conflict if any
        setEmbeddedIndexHashAt(slot, NONE);
        final int startSlot = slot;
        // find last filled (inclusive)
        int endSlot = startSlot;
        int maxEmbeddedSlot = Math.min(slot + EMBEDDED_HASH_MAX_CONFLICT, EMBEDDED_HASH_LEN);
        for(;;) {
            int next = nextEmbeddedSlot(endSlot);
            if (next == maxEmbeddedSlot) {
                break;
            }
            int index = embeddedIndexHashAt(next);
            if (index == NONE) {
                break;
            }
            endSlot = next;
        }
        if (startSlot != endSlot) {
            recursiveShiftRemoveEmbeddedConflict(startSlot, endSlot);
        } // else no conflict: next is empty
        
        // update index (decrement 1) for all indexHash[.] > prevIndex !
        updateDecrEmbeddedHashIndex(prevIndex);
    }

    private void recursiveShiftRemoveEmbeddedConflict(int startSlot, int endSlot) {
        outer: while(startSlot != endSlot) {
            for(int slot = endSlot; slot != startSlot; slot = prevEmbeddedSlot(slot)) { // TODO use MAX_CONFLICT??
                int index = embeddedIndexHashAt(slot);
                if (index == NONE) {
                    break; // should not occur?
                }
                String keyAt = keyAt(index);
                int expectedSlot = embeddedHashSlot(hash0(keyAt));
                if (expectedSlot == startSlot) {
                    // found conflict, shift then recurse Embedded
                    setEmbeddedIndexHashAt(startSlot, index);
                    setEmbeddedIndexHashAt(slot, NONE);
                    
                    startSlot = slot;
                    continue outer; // recurse
                }
            }
            break; // no conflict found 
        }
    }

    private void updateDecrEmbeddedHashIndex(int prevIndex) {
        for(int i = 0; i < EMBEDDED_HASH_LEN; i++) {
            int index = embeddedIndexHashAt(i);
            if (index > prevIndex) {
                setEmbeddedIndexHashAt(i, index-1);
            }
        }
    }
    
    private int nextEmbeddedSlot(int i) {
        int res = i + 1;
        if(res >= EMBEDDED_HASH_LEN) {
            res = 0;
        }
        return res;
    }

    private int prevEmbeddedSlot(int i) {
        int res = i - 1;
        if(res < 0 ) {
            res = EMBEDDED_HASH_LEN - 1;
        }
        return res;
    }
    
    private void shiftRemoveRemainingKeyValues(int i) {
        int shiftCount = size - EMBEDDED_KV_LEN - i;
        System.arraycopy(remainingKeys, i+1, remainingKeys, i, shiftCount);
        System.arraycopy(remainingValues, i+1, remainingValues, i, shiftCount);
    }

    private void shiftRemoveRemainingIndexHash(int slot, int prevIndex) {
        // remove index at slot ... shift following conflict if any
        remainingIndexHash[slot] = NONE;
        final int startSlot = slot;
        // find last filled (inclusive)
        int endSlot = startSlot;
        for(;;) {
            int nextSlot = nextRemainingSlot(endSlot);
            int nextIndex = remainingIndexHashAt(nextSlot);
            if (nextIndex == NONE) {
                break;
            }
            endSlot = nextSlot;
        }
        if (startSlot != endSlot) {
            recursiveShiftRemoveRemainingConflict(startSlot, endSlot);
        } // else no conflict: next is empty
        
        // update index (decrement 1) for all indexHash[.] > prevIndex !
        updateDecrRemainingHashIndex(prevIndex);
    }

    private void recursiveShiftRemoveRemainingConflict(int startSlot, int endSlot) {
        while(startSlot != endSlot) {
            for(int slot = endSlot; slot != startSlot; slot = prevRemainingSlot(slot)) {
                int index = remainingIndexHash[slot];
                String keyAt = keyAt(index);
                int expectedSlot = remainingHashSlot(hash0(keyAt));
                if (expectedSlot == startSlot) {
                    // found conflict, shift then recurse remaining
                    remainingIndexHash[startSlot] = index;
                    remainingIndexHash[slot] = NONE;
                    
                    startSlot = slot;
                    continue; // recurse
                }
            }
            break; // no conflict found 
        }
    }

    private void updateDecrRemainingHashIndex(int prevIndex) {
        int remainCount = size - prevIndex;
        int sizeFragment = remainingIndexHash.length >>> 2; // : size/4 
        // either iter by remaining hash, or iter all
        boolean rescanAll = remainCount < sizeFragment;
        rescanAll = true; // TODO
        if (rescanAll) { 
            for(int i = 0; i < remainingIndexHash.length; i++) {
                int index = remainingIndexHash[i];
                if (index > prevIndex) {
                    remainingIndexHash[i]--;
                }
            }
        } else {
            throw FxUtils.notImplYet();
//            for(int i = prevIndex + 1; i < size; i++) {
//                String keyAt = 
//            }
        }
    }
    
    protected int remainingHashSlot(int hashCode) {
        return hashCode % remainingIndexHash.length;
    }

    private int nextRemainingSlot(int i) {
        int res = i + 1;
        if(res >= remainingIndexHash.length) {
            res = 0;
        }
        return res;
    }

    private int prevRemainingSlot(int i) {
        int res = i - 1;
        if(res < 0 ) {
            res = remainingIndexHash.length - 1;
        }
        return res;
    }

    private int remainingIndexHashAt(int slot) { return remainingIndexHash[slot]; }
    private void setRemainingIndexHashAt(int slot, int index) { remainingIndexHash[slot] = index; }
    
    private String remainingKeyAt(int i) { return remainingKeys[i]; } 
    @SuppressWarnings("unchecked")
    private T remainingValueAt(int i) { return (T) remainingValues[i]; } 

    private void setRemainingKeyValueAt(int i, String key, T value) {
        remainingKeys[i] = key;
        remainingValues[i] = value;
    }

}
