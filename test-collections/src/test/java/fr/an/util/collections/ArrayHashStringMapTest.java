package fr.an.util.collections;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.an.util.collections.ArrayHashStringMap;

public class ArrayHashStringMapTest {

    @Test
    public void testEmbeddedHashSlot() {
        for (int i = -1000; i < 1000; i++) {
            int slot = ArrayHashStringMap.embeddedHashSlot(i);
            Assert.assertTrue(slot >= 0);
            Assert.assertTrue(slot < 16);
        }
    }
    
    @Test
    public void testPut() {
        ArrayHashStringMap<Integer> sut = new ArrayHashStringMap<Integer>();
        
        sut.put("1", 1);
        Assert.assertEquals("[1:1]", sut.toString());
        Assert.assertTrue(sut.containsKey("1"));
        Assert.assertEquals((Integer)1, sut.get("1"));
        
        sut.put("2", 2);
        Assert.assertEquals("[1:1, 2:2]", sut.toString());
        Assert.assertTrue(sut.containsKey("1"));
        Assert.assertEquals((Integer)1, sut.get("1"));
        Assert.assertTrue(sut.containsKey("2"));
        Assert.assertEquals((Integer)2, sut.get("2"));
        
        sut.put("3", 3);
        Assert.assertEquals("[1:1, 2:2, 3:3]", sut.toString());
        Assert.assertTrue(sut.containsKey("1"));
        Assert.assertTrue(sut.containsKey("2"));
        Assert.assertTrue(sut.containsKey("3"));
        
        for (int i = 4; i < 100; i++) {
            String keyI = Integer.toString(i);
            sut.put(keyI, i);
            Assert.assertEquals(i, sut.size());
            
            for (int j = 1; j <= i; j++) {
                String jKey = Integer.toString(j);
                Assert.assertTrue(sut.containsKey(jKey));
                Assert.assertEquals((Integer)j, sut.get(jKey));
            }
        }
    }
    
    @Test
    public void testRemove() {
        ArrayHashStringMap<Integer> sut = new ArrayHashStringMap<Integer>();
        HashMap<String,Integer> check = new HashMap<String,Integer>();
        
        
        for (int i = 1; i < 100; i++) {
            String keyI = Integer.toString(i);
            sut.put(keyI, i);
            check.put(keyI, i);
        }
        
        String key = "50";
        sut.remove(key);
        Assert.assertTrue(sut.toString().contains(", 49:49, 51:51, "));
        check.remove(key);
        Assert.assertFalse(sut.containsKey(key));
        assertEqualsNoOrder(check, sut);
        
        key = "1";
        sut.remove(key);
        Assert.assertTrue(sut.toString().startsWith("[2:2, 3:3, "));
        check.remove(key);
        Assert.assertFalse(sut.containsKey(key));
        assertEqualsNoOrder(check, sut);
        
        for (int i = 1; i < 100; i++) {
            System.out.println("remove " + i);
            if (i == 18) {
                System.out.println();
            }
            String keyI = Integer.toString(i);
            sut.remove(keyI);
            check.remove(keyI);
            assertEqualsNoOrder(check, sut);            
        }

    }
 
    private static void assertEqualsNoOrder(Map<String,Integer> expected, ArrayHashStringMap<Integer> actual) {
        Assert.assertEquals(expected, actual.toLinkedHashMap());
        
        for(String key : expected.keySet()) {
            Assert.assertTrue(actual.containsKey(key));
        }
    }
    
}
