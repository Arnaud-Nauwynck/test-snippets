package fr.an.util.collections;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class SortedArrayDicTest {
    
    private static final Field elementDataField;
    static {
        try {
            elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }
    
    private static class SortedList extends AbstractMap<String,Integer> {
        List<String> ls = new ArrayList<String>();
        
        private Object[] lsElementData() {
            try {
                return (Object[]) elementDataField.get(ls);
            } catch (Exception ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
        
        public void add(String key) {
            int index = Arrays.binarySearch(lsElementData(), 0, ls.size(), key);
            if (index < 0) {
                index = - index - 1;
                ls.add(index, key);
            }
        }
        public void remove(String key) {
            int index = Arrays.binarySearch(lsElementData(), 0, ls.size(), key);
            if (index >= 0) {
                ls.remove(index);
            }
        }
        public String get(int i) {
            return ls.get(i);
        }

        public boolean containsKey(Object key) {
            int index = Arrays.binarySearch(lsElementData(), 0, ls.size(), key);
            return (index >= 0);
        }
        
        public int size() {
            return ls.size();
        }
        
        @Override
        public Integer put(String key, Integer value) {
            add(key);
            return null;
        }

        @Override
        public Integer remove(Object key) {
            remove((String) key);
            return null;
        }

        @Override
        public Set<Entry<String, Integer>> entrySet() {
            throw new UnsupportedOperationException();
        }
        
        
    }
       
    @Test
    public void testPutRemove() {
        Random rand = new Random(0);
        SortedArrayDic<Integer> sut = new SortedArrayDic<Integer>();
        SortedMap<String,Integer> check = new TreeMap<String,Integer>();
        
        SortedList sortedList = new SortedList();
        
        for(int repeat = 0; repeat < 100000; repeat++) {
            int randValue = rand.nextInt();
            boolean addOrRemove = true;
            if (sortedList.size() > 10) {
                addOrRemove = (repeat % 3) != 0;
            }
            if (addOrRemove) {
                randValue = rand.nextInt();
            } else {
                int index = rand.nextInt(sortedList.size());
                randValue = Integer.parseInt(sortedList.get(index));                
            }
            String key = Integer.toString(randValue);
            Integer value = randValue;
            if (addOrRemove) {
                sortedList.add(key);
                sut.put(key, value);
                check.put(key, value);
            } else {
                sortedList.remove(key);
                sut.remove(key);
                check.remove(key);
            }
            if (repeat%100 == 0) {
                Assert.assertEquals(sut, check);
            }
            // Assert.assertEquals(sut.toString(), check.toString());
        }
        Assert.assertEquals(sut, check);
    }
    
    
    
    @Test
    public void testBench() {
        SortedArrayDic<Integer> sut = new SortedArrayDic<Integer>();
        SortedMap<String,Integer> check = new TreeMap<String,Integer>();
        SortedList sortedList = new SortedList();
        
        
        int[] benchArraySizes = { 5, 10, 20, 50, 100, 1000, 10000, 100000 };
        for (int i = 0; i < benchArraySizes.length; i++) {
            int benchArraySize = benchArraySizes[i];
            int repeatCount = 1000 + 10 * benchArraySizes[benchArraySizes.length-1 - i];
            
            doTestBench(sut, benchArraySize, repeatCount, "SortedArrayDic");
            doTestBench(check, benchArraySize, repeatCount, "TreeMap");
            doTestBench(sortedList, benchArraySize, repeatCount, "SortedList");
            System.out.println();
            
        }
    }
    
    @Test
    public void testBench_repeat() {
        int benchArraySize = 10000;
        for (int i = 0; i < 4; i++) {
            SortedArrayDic<Integer> test = new SortedArrayDic<Integer>();
            doTestBench(test, benchArraySize, 10000, "SortedArrayDic");
        }
    }

    private static final boolean DEBUG = false;
    
    protected void doTestBench(Map<String,Integer> sut, int benchArraySize, int repeatCount, String display) {
        int distinctEltsCount = benchArraySize*2;
        String[] keys = new String[distinctEltsCount];
        Integer[] values = new Integer[distinctEltsCount];
        for (int i = 0; i < distinctEltsCount; i++) {
            int v = i * 3;
            keys[i] = String.valueOf(v);
            values[i] = Integer.valueOf(v);
        }
        Random rand = new Random(0);
        
        for (int i = 0; i < benchArraySize; i++) {
            sut.put(keys[i], values[i]);
        }
        
        if (DEBUG) System.out.print("sizes: ");
        int countAdded = 0;
        int countRemoved = 0;
        long startMillis = System.currentTimeMillis();
        for(int repeat = 0; repeat < repeatCount; repeat++) {
            int randValue = rand.nextInt(distinctEltsCount);
            String key = keys[randValue];
            Integer value = values[randValue];
            
            if (! sut.containsKey(key)) {
                sut.put(key, value);
                countAdded++;
            } else {
                sut.remove(key);
                countRemoved++;
            }
            
            if (DEBUG) {
                if ((repeat-1) % 100 == 0) {
                    System.out.print(sut.size() + " ");
                }
            }
        }
        if (DEBUG) System.out.println();
        
        long time = System.currentTimeMillis() - startMillis;
        System.out.println("bench " + display + " [size:" + benchArraySize + " x repeat:" + repeatCount + "] " + time + " ms"  
            + " (= " + repeatCount + " containsKey(), " + countAdded + " put(), " + countRemoved + " remove() )");
    }
    
    
    
}
