package fr.an.tests.testjdk12;

import java.lang.StackWalker.StackFrame;

/**
 * 
 * 
 *
 */
public class AppMain {

	public static void main(String[] args) {
		new AppMain().run();
	}

	private void run() {
		testCallerMethodUsingJdk9StackWalker();
	}

	/**
	 * https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
	 */
	public void testCallerMethodUsingJdk9StackWalker() {
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
