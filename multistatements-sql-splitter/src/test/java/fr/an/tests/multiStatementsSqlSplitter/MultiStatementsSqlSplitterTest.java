package fr.an.tests.multiStatementsSqlSplitter;

import org.junit.Assert;
import org.junit.Test;

import lombok.val;

public class MultiStatementsSqlSplitterTest {

	@Test
	public void testSplit() {
		String text = "stmt1-line1\n"
				+ "stmt1-line2;\n" // <= split
				+ "stmt2-line1\n"
				+ "stmt2-line2\n"
				;
		val res = MultiStatementsSqlSplitter.splitSemiColon(text);
		Assert.assertEquals(2, res.size());
		val stmt1 = res.get(0);
		val stmt2 = res.get(1);
		Assert.assertEquals(1, stmt1.lineNumber);
		Assert.assertEquals("stmt1-line1\nstmt1-line2", stmt1.text);
		Assert.assertEquals(2, stmt2.lineNumber);
		Assert.assertEquals("\nstmt2-line1\nstmt2-line2\n", stmt2.text);
	}

	@Test
	public void testSplit_lineComment() {
		String text = "stmt1-line1 -- ignore ;;; \n" // in comment.. no split
				+ "stmt1-line2 -- ignore ;;; \n" // in comment.. no split
				+ ";\n" // <= split
				+ "stmt2-line1\n"
				+ "stmt2-line2\n"
				;
		val res = MultiStatementsSqlSplitter.splitSemiColon(text);
		Assert.assertEquals(2, res.size());
		val stmt1 = res.get(0);
		val stmt2 = res.get(1);
		Assert.assertEquals(1, stmt1.lineNumber);
		Assert.assertEquals("stmt1-line1 -- ignore ;;; \n" 
				+ "stmt1-line2 -- ignore ;;; \n", stmt1.text);
		Assert.assertEquals(3, stmt2.lineNumber);
		Assert.assertEquals("\nstmt2-line1\n" 
				+ "stmt2-line2\n", stmt2.text);
	}

	@Test
	public void testSplit_simpleComment() {
		String text = "stmt1-line1 /* ignore ;;; \n" // in comment.. no split
				+ " also ignore ;; \n" // no split
				+ " -- also ; \n" //
				+ "*/ \n" //
				+ "stmt1-line2\n" //
				+ ";\n" // <= split
				+ "stmt2-line1\n" //
				+ "stmt2-line2\n"
				;
		val res = MultiStatementsSqlSplitter.splitSemiColon(text);
		Assert.assertEquals(2, res.size());
		val stmt1 = res.get(0);
		val stmt2 = res.get(1);
		Assert.assertEquals(1, stmt1.lineNumber);
		Assert.assertEquals("stmt1-line1 /* ignore ;;; \n" // in comment.. no split
				+ " also ignore ;; \n" // no split
				+ " -- also ; \n" //
				+ "*/ \n" //
				+ "stmt1-line2\n", stmt1.text);
		Assert.assertEquals(6, stmt2.lineNumber);
		Assert.assertEquals("\n" // <= split
				+ "stmt2-line1\n" //
				+ "stmt2-line2\n", stmt2.text);
	}
}
