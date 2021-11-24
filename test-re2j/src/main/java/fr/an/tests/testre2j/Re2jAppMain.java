package fr.an.tests.testre2j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

public class Re2jAppMain {

	public static void main(String[] args) {
		Re2jAppMain app = new Re2jAppMain();
		app.run(args);
	}

	private void run(String[] args) {
		benchMatchesAAAAX(100);
		benchMatchesAAAAX(1000_000);
		benchMatchesAAAAX(10_000_000);
		
		String text1 = generateRandomACTG(10_000_000);
		
		String[] regexps = new String [] {
				// benchmark with out "|"
				// "(a*t)*(a[gc]t)*c*)*(ag*t)*c(gt)*tga(gc)*tt",
				// benchmark with "|" 
				//"(a*t)*((a[gc]t((ab)|(ag*)))*|(ag*t((ab)|(at*))))c(gt)*tga(gc)*tt",
				// "(a*t)+gg(a*(c|(g*|tacg(a*(c|(g*|tacg(a*(c|(g*|tacg(a*(c|(g*|tacg))))))))))))",
				"(a*t)+gg((a*c|(gt*))|(ca*|(t*a((a*c|(gt*))|(ca*|(t*a((a*c|(gt*))|(ca*|(t*a((a*c|(gt*))|(ca*|(t*a))))))))))))"
		};
		
		for(String regexp : regexps) {
			Pattern javaPattern1 = Pattern.compile(regexp);
			com.google.re2j.Pattern re2jPattern1 = com.google.re2j.Pattern.compile(regexp);
			
			findJavaPatterns(text1, re2jPattern1);
			
			for(int i = 1; i < 3; i++) {
				benchFindJavaPatterns(text1, javaPattern1, i);
				benchFindRe2jPatterns(text1, re2jPattern1, i);
			}
		}
		
	}

	private static void benchMatchesAAAAX(int len) {
		System.out.println("bench matches AA(" + len + ")AX");
		// run the following on java8, fixed on 9
	    StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < len; i++) {
	    	sb.append("a");
	    }
	    sb.append("!");
	    String text = sb.toString();
	    
	    String regexp = "^(a+)+$";
	    Pattern javaPattern = Pattern.compile(regexp);
		com.google.re2j.Pattern re2jPattern = com.google.re2j.Pattern.compile(regexp);
		
	    for(int i = 1; i < 3; i++) {
			benchMatchesJavaPatterns(text, javaPattern, i);
			benchMatchesRe2jPatterns(text, re2jPattern, i);
		}
	}
	
    
	private static List<Integer> findJavaPatterns(String text, Pattern javaPattern) {
		List<Integer> startIndexes = new ArrayList<>();
		System.out.println("find patterns " + javaPattern);
		Matcher matcher = javaPattern.matcher(text);
		int fromIndex = 0;
		for(;;) {
			boolean found = matcher.find(fromIndex);
			if (!found) {
				break;
			}
			int foundStart = matcher.start();
			startIndexes.add(foundStart);
			fromIndex = matcher.end(); // or start() + 1 ??
		}
		System.out.println("found " + startIndexes.size() + " patterns => start indexes (limit 15 first): " + 
				startIndexes.subList(0, Math.min(15, startIndexes.size())));
		return startIndexes;
	}
	
	private static long benchFindJavaPatterns(String text, Pattern javaPattern, int repeatCount) {
		System.out.println("bench find java Pattern (repeat " + repeatCount + ") ...");		
		long startNanos = System.nanoTime();
		Matcher matcher = javaPattern.matcher(text);
		for(int repeat = 0; repeat < repeatCount; repeat++) {
			int fromIndex = 0;
			for(;;) {
				boolean found = matcher.find(fromIndex);
				if (!found) {
					break;
				}
				fromIndex = matcher.end(); // or start() + 1 ??
			}
		}
		long nanos = System.nanoTime() - startNanos;
		long avgMillis = nanos / repeatCount / 1000_000;
		System.out.println("bench find patterns using java.Pattern, took " + avgMillis + " ms");
		return avgMillis;
	}

	private static long benchFindRe2jPatterns(String text, com.google.re2j.Pattern re2jPattern, int repeatCount) {
		System.out.println("bench find re2j.Pattern (repeat " + repeatCount + ") ...");		
		long startNanos = System.nanoTime();
		Matcher matcher = re2jPattern.matcher(text);
		for(int repeat = 0; repeat < repeatCount; repeat++) {
			int fromIndex = 0;
			for(;;) {
				boolean found = matcher.find(fromIndex);
				if (!found) {
					break;
				}
				fromIndex = matcher.end(); // or start() + 1 ??
			}
		}
		long nanos = System.nanoTime() - startNanos;
		long avgMillis = nanos / repeatCount / 1000_000;
		System.out.println("bench find patterns using re2j.Pattern, took " + avgMillis + " ms");
		return avgMillis;
	}

	
	private static void benchMatchesJavaPatterns(String text, Pattern javaPattern, int repeatCount) {
		System.out.println("bench matches java Pattern (repeat " + repeatCount + ") ...");		
		long startNanos = System.nanoTime();
		Matcher matcher = javaPattern.matcher(text);
		for(int repeat = 0; repeat < repeatCount; repeat++) {
			matcher.matches();
		}
		long nanos = System.nanoTime() - startNanos;
		long avgMillis = nanos / repeatCount / 1000_000;
		System.out.println("bench matches  patterns using java.Pattern, took " + avgMillis + " ms");
	}

	private static long benchMatchesRe2jPatterns(String text, com.google.re2j.Pattern re2jPattern, int repeatCount) {
		System.out.println("bench matches re2j.Pattern (repeat " + repeatCount + ") ...");		
		long startNanos = System.nanoTime();
		Matcher matcher = re2jPattern.matcher(text);
		for(int repeat = 0; repeat < repeatCount; repeat++) {
			matcher.matches();
		}
		long nanos = System.nanoTime() - startNanos;
		long avgMillis = nanos / repeatCount / 1000_000;
		System.out.println("bench matches patterns using re2j.Pattern, took " + avgMillis + " ms");
		return avgMillis;
	}

	
	
	private String generateRandomACTG(int len) {
		StringBuilder sb = new StringBuilder(len);
		Random rand = new Random(0);
		char[] ACTG = new char[] { 'a', 'c', 't', 'g' };
		for(int i = 0; i < len; i++) {
			int r = rand.nextInt(4);
			char ch = ACTG[r];
			sb.append(ch);
		}
		return sb.toString();
	}
}
