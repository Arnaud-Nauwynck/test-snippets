package fr.an.test.glowroot;

import org.junit.Test;

public class ExUtilsTest {

	@Test
	public void testCurrentStackTraceShortPath() {
		String res = ExUtils.currentStackTraceShortPath();
		System.out.println("curr stack: " + res);
	}
	
	@Test
	public void testStackTraceToShortPath() {
		Exception ex = new Exception("test");
		String res = ExUtils.stackTraceToShortPath(ex);
		System.out.println("ex: " + res);
	}

	@Test
	public void testStackTraceToShortPath_cause() {
		Exception cause = new Exception("CauseEx");
		Exception ex = new Exception("test", cause);
		String res = ExUtils.stackTraceToShortPath(ex);
		System.out.println("ex: " + res);
	}

}
