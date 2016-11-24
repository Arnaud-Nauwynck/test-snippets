package fr.an.tests.testMapFlatMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class MapFlatMapTest {

	@Test
	public void test2() {
		List<Integer> ls1 = range(1,2);
		List<Integer> ls2 = range(3,4);
		List<String> res = ls1.stream().flatMap(x1 -> 
			ls2.stream().map(x2 -> 
					"" + x1 + "-" + x2))
				.collect(Collectors.toList());
		List<String> expectedRes = Arrays.asList("1-3", "1-4", "2-3", "2-4");
		Assert.assertEquals(expectedRes, res);
		
		// basic equivalent...
		List<String> basicRes = new ArrayList<>();
		for(int x1 : ls1) {
			for(int x2: ls2) {
				basicRes.add(x1 + "-" + x2);
			}
		}
		Assert.assertEquals(expectedRes, basicRes);
		
		// forEach equivalent...
		List<String> foreachRes = new ArrayList<>();
		ls1.forEach(x1 -> {
			ls2.forEach(x2 -> {
				foreachRes.add(x1 + "-" + x2);
			});
		});
		Assert.assertEquals(expectedRes, foreachRes);
	}
	
	@Test
	public void test3() {
		List<Integer> ls1 = range(1,2);
		List<Integer> ls2 = range(3,4);
		List<Integer> ls3 = range(5,6);
		List<String> res = ls1.stream().flatMap(x1 -> 
			ls2.stream().flatMap(x2 -> 
				ls3.stream().map(x3 -> 
					"" + x1 + "-" + x2 + "-" + x3)))
				.collect(Collectors.toList());
		List<String> expectedRes = Arrays.asList("1-3-5", "1-3-6", "1-4-5", "1-4-6", 
				"2-3-5", "2-3-6", "2-4-5", "2-4-6");
		Assert.assertEquals(expectedRes, res);
		
		// basic equivalent...
		List<String> basicRes = new ArrayList<>();
		for(int x1 : ls1) {
			for(int x2 : ls2) {
				for(int x3 : ls3) {
					basicRes.add(x1 + "-" + x2 + "-" + x3);
				}
			}
		}
		Assert.assertEquals(expectedRes, basicRes);
		
		// forEach equivalent...
		List<String> foreachRes = new ArrayList<>();
		ls1.forEach(x1 -> {
			ls2.forEach(x2 -> {
				ls3.forEach(x3 -> {
					foreachRes.add(x1 + "-" + x2 + "-" + x3);
				});
			});
		});
		Assert.assertEquals(expectedRes, foreachRes);
	}

	private static List<Integer> range(int start, int end) {
		List<Integer> res = new ArrayList<>();
		for(int i = start; i <= end; i++) {
			res.add(i);
		}
		return res;
	}
}
