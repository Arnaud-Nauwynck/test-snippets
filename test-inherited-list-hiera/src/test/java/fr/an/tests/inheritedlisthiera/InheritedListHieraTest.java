package fr.an.tests.inheritedlisthiera;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class InheritedListHieraTest {

    @Test
    public void testInheritedValuesForPath() {
	final String value_a = "value_a", 
		value_a_b = "value_a_b", 
		value_a_b_c1 = "value_a_b_c1", 
		value_a_b_c2 = "value_a_b_c2"; 
			
	InheritedListHiera<String> hiera = new InheritedListHiera<>();
	 hiera.addToSubTree("/a", value_a);
	 hiera.addToSubTree("/a/b", value_a_b);
	 hiera.addToSubTree("/a/b/c1", value_a_b_c1);
	 hiera.addToSubTree("/a/b/c2", value_a_b_c2);
	 
	 List<String> res_a = hiera.inheritedValuesForPath("/a"); 
	 assertEquals(res_a, value_a);
	 
	 List<String> res_a_b = hiera.inheritedValuesForPath("/a/b"); 
	 assertEquals(res_a_b, value_a, value_a_b);
	 List<String> res2_a_b = hiera.inheritedValuesForPath("/a/b/foo/bar/baz"); 
	 assertEquals(res2_a_b, value_a, value_a_b);
	 
	 List<String> res_a_b_c1 = hiera.inheritedValuesForPath("/a/b/c1"); 
	 assertEquals(res_a_b_c1, value_a, value_a_b, value_a_b_c1);
	 List<String> res2_a_b_c1 = hiera.inheritedValuesForPath("/a/b/c1/foo/bar/baz");
	 assertEquals(res2_a_b_c1, value_a, value_a_b, value_a_b_c1);
	 
	 List<String> res_foo = hiera.inheritedValuesForPath("/foo"); 
	 assertEquals(res_foo);
    }

    @Test
    public void testInheritedValuesForPath_bench() {
	// inserting 200 tree rules
	InheritedListHiera<String> hiera = new InheritedListHiera<>();
	int countPrefixes = 200;
	int repeat = 50_000;
	String[][] prefixes = new String[countPrefixes][];
	hiera.addToSubTree("aaaa", "a");
	hiera.addToSubTree("aaaa/bbbb", "a_b");
	hiera.addToSubTree("aaaa/bbbb/cccc", "a_b_c");
	for (int i = 0; i < countPrefixes; i ++) {
	    prefixes[i] = new String[] { "aaaa", "bbbb", "cccc", Integer.toString(i) };
	    hiera.addToSubTree(prefixes[i], "a_b_c_" + i);
	}
	// repeat resolving pathes
	String[] repeatSubPath = new String[] { "foo", "bar", "baz" };
	int pathLen = prefixes[0].length + repeatSubPath.length;
	String[] path = new String[pathLen];
	
	long startTime = System.currentTimeMillis();

	List<String> res = new ArrayList<>();
	for (int i = 0; i < countPrefixes; i++) {
	    String[] prefix = prefixes[i]; // "aaaa/bbbb/cccc/" + i;
	    System.arraycopy(prefix, 0, path, 0, prefix.length);
	    System.arraycopy(repeatSubPath, 0, path, prefix.length, repeatSubPath.length);
	    // = "aaaa/bbbb/cccc/" + i + "/foo/bar/baz";
	    for (int j = 0; j < repeat; j++) {
		res.clear();
		hiera.collectInheritedValues(res, path);
		Assert.assertEquals(4, res.size());
	    }
	}
	
	long millis = System.currentTimeMillis() - startTime;
	System.out.println("time to call " + countPrefixes*repeat + " collectInheritedValues() with path len:" + pathLen + " : " + millis + "ms");
	// resilt => time to call 10000000 collectInheritedValues() with path len:7 : 1479ms
    }
    
    
    protected static void assertEquals(List<String> actual, String... expected) {
	Assert.assertEquals(expected.length, actual.size());
	for(int i = 0; i < expected.length; i++) {
	    Assert.assertEquals(expected[i], actual.get(i));
	}
    }
}
