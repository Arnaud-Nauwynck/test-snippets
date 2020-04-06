package fr.an.tests.testjdk9;

import java.lang.StackWalker.StackFrame;

/**
 *
 */
public class AppMain {

	public static void main(String[] args) {
		new AppMain().run();
	}

	private void run() {
		testJdk9StackWalker();
	}

	/**
	 * https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
	 */
	public void testJdk9StackWalker() {
	    String meth = jdk9_CurrentCallerMethod();
	    System.out.println("test using jdk>=9 StackWalker.. => " + meth);
	}

	private static String jdk9_CurrentCallerMethod() {
		StackFrame callerStackFrame = StackWalker.getInstance()
	                      .walk(s -> s.skip(1).findFirst())
	                      .get();
		return callerStackFrame.getMethodName();
	}
	
}
