package fr.an.tests.countwords;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.FrenchStemmer;

public class CountApp {

    private static final Pattern sep = Pattern.compile("[\\p{Punct}} \r\t]"); // "[ ,;:!\\.\t'`\"]");
    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

    public static void main(String[] args) {
	String fileName = (args.length != 0) ? args[0] : "src/test/data/20000-lieues.txt";
	File file = new File(fileName);
	try (BufferedReader lineReader = new BufferedReader(
		new InputStreamReader(new BufferedInputStream(new FileInputStream(file))))) {

	    readLinesCountWords(lineReader);

	} catch (Exception ex) {
	    System.err.println("Failed");
	    ex.printStackTrace();
	}

    }

    private static class Counter {
	int count;
    }

    private static void readLinesCountWords(BufferedReader lineReader) throws IOException {
	Map<String, Counter> counters = new TreeMap<>();

	analyze(lineReader, counters);
//	analyzeLucene(lineReader, counters);

	System.out.println("--------------");
	for (Map.Entry<String, Counter> e : counters.entrySet()) {
	    String base = e.getKey();
	    Counter c = e.getValue();
	    System.out.println(base + ": " + c.count);
	}
	System.out.println("Count distincts: " + counters.size());
    }

    protected static void analyze(BufferedReader lineReader, Map<String, Counter> counters) throws IOException {
	String line;
	while (null != (line = lineReader.readLine())) {
	    splitAndIncrCount(counters, line);
	}
    }

    protected static void analyzeLucene(BufferedReader lineReader, Map<String, Counter> counters) throws IOException {
	// Analyzer analyzer = new StandardAnalyzer();
	Analyzer analyzer = CustomAnalyzer.builder().withTokenizer("standard")
			.addTokenFilter("lowercase")
//		      .addTokenFilter("stop")
//		      .addTokenFilter("porterstem")
//		      .addTokenFilter("capitalization")
		.build();
	try {
	    @SuppressWarnings("resource")
	    TokenStream stream = analyzer.tokenStream(null, lineReader);
	    stream = new SnowballFilter(stream, new FrenchStemmer());

	    stream.reset();
	    while (stream.incrementToken()) {
		CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
		String word = term.toString();
		if (word.isEmpty()) {
		    continue;	
		}
		splitAndIncrCount(counters, word);
	    }
	} catch (IOException e) {
	    // not thrown b/c we're using a string reader...
	    throw new RuntimeException(e);
	}

    }

    protected static void splitAndIncrCount(Map<String, Counter> counters, String wordsText) {
	String[] words = sep.split(wordsText);
	for (String word : words) {
	    if (word.isEmpty()) {
		continue;
	    }
	    incrCount(counters, word);
	}
    }

    private static void incrCount(Map<String, Counter> counters, String word) {
	String base = word.toLowerCase();
//	if (base.contentEquals("s")) {
//	    base = base.substring(0, base.length()-1);
//	}
	base = base.replace("_", "");
	base = base.replace("“", "").replace("”", "");
	// System.out.println(base);

	if (PATTERN_NUMBER.matcher(base).matches()) {
	    return;
	}
	
	Counter c = counters.get(base);
	if (c == null) {
	    c = new Counter();
	    counters.put(base, c);
	}
	c.count++;
    }

}
