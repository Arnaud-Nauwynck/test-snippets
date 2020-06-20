package fr.an.tests.csvunivocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

public class CsvUnivocityAppMain {

	public static void main(String[] args) throws Exception {
		
		testReadAsStringCols();
		testReadCsvSemiColAsStringCols();
		testParseBeans();
		
		testWriteAstObjectCols();
	}

	private static void testReadAsStringCols() throws Exception {
		try (Reader inputReader = new InputStreamReader(new FileInputStream(new File("src/test/data/file1.csv")), "UTF-8")) {
		    CsvParserSettings settings = new CsvParserSettings();
			// settings.getFormat().setDelimiter(';');
		    settings.setHeaderExtractionEnabled(true);
			CsvParser parser = new CsvParser(settings);
		    List<String[]> parsedRows = parser.parseAll(inputReader);
		    System.out.println("parsed csv as String[] .. " + parsedRows.size());
		}
	}
	
	private static void testReadCsvSemiColAsStringCols() throws Exception {
		try (Reader inputReader = new InputStreamReader(new FileInputStream(new File("src/test/data/file-semicol.csv")), "UTF-8")) {
		    CsvParserSettings settings = new CsvParserSettings();
			settings.getFormat().setDelimiter(';');
		    settings.setHeaderExtractionEnabled(true);
			CsvParser parser = new CsvParser(settings);
		    List<String[]> parsedRows = parser.parseAll(inputReader);
		    System.out.println("parsed csv as String[] .. " + parsedRows.size());
		}
	}
	
	private static void testWriteAstObjectCols() throws Exception {
		List<Object[]> lines = new ArrayList<>();
		lines.add(new Object[] { 1, 1.2, "hello", "hello with ;", new Date() });
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File("target/test.csv")),"UTF-8")){
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setEscapeUnquotedValues(true);
			CsvWriter writer = new CsvWriter(outputWriter, settings);
            writer.writeRowsAndClose(lines);
	    }
	}
	
	private static class Bean1 {
		@Parsed(field = "fieldInt")
		private int fieldInt;
		
		@Parsed(field = "fieldDouble")
		private double fieldDouble;
		
		@Parsed(field = "fieldStr")
		private String fieldStr;
		
		@Parsed(field = "fieldQuotedStr")
		private String fieldQuotedStr;
		
		@Parsed(field = "fieldDate")
		private String fieldDate; // Date does not work..

		public int getFieldInt() {
			return fieldInt;
		}
//		public void setFieldInt(int fieldInt) { // Does not work if public setter !!!!
//			this.fieldInt = fieldInt;
//		}
		public double getFieldDouble() {
			return fieldDouble;
		}
//		public void setFieldDouble(double fieldDouble) {
//			this.fieldDouble = fieldDouble;
//		}
		public String getFieldStr() {
			return fieldStr;
		}
//		public void setFieldStr(String fieldStr) {
//			this.fieldStr = fieldStr;
//		}
		public String getFieldQuotedStr() {
			return fieldQuotedStr;
		}
//		public void setFieldQuotedStr(String fieldQuotedStr) {
//			this.fieldQuotedStr = fieldQuotedStr;
//		}
//		public Date getFieldDate() {
//			return fieldDate;
//		}
//		public void setFieldDate(Date fieldDate) {
//			this.fieldDate = fieldDate;
//		}
//		
		
	}
	
	public static void testParseBeans() throws Exception {
		try (Reader inputReader = new InputStreamReader(new FileInputStream(new File("src/test/data/file1.csv")), "UTF-8")) {
		    BeanListProcessor<Bean1> rowProcessor = new BeanListProcessor<Bean1>(Bean1.class);
		    CsvParserSettings settings = new CsvParserSettings();
		    settings.setHeaderExtractionEnabled(true);
		    settings.setProcessor(rowProcessor);
		    CsvParser parser = new CsvParser(settings);
		    parser.parse(inputReader);
		    List<Bean1> beans = rowProcessor.getBeans();
		    System.out.println("parsed beans: " + beans);
		}
	}	
	
}
