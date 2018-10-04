package fr.an.tests.currmeth;

import java.lang.StackWalker.StackFrame;

/**
 * https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
 * 
 *
 */
public class AppMain {

	public static void main(String[] args) {
		new AppMain().run();
	}

	private void run() {
		testUsingThreadStack();
		testUsingExceptionStackTrace();
		testUsingAnonymousObject();
		testUsingAnonymousClass();
//		testCallerClassUsingSunReflect();
		testUsingJdk9StackWalker();
	}


	public void testUsingThreadStack() {
		String meth = Thread.currentThread().getStackTrace()[1].getMethodName();
		System.out.println("test using currentThread().getStackTrace()[1].. => " + meth);
	}

	public void testUsingExceptionStackTrace() {
		String meth = new Exception().getStackTrace()[0].getMethodName();
		System.out.println("test using new Exception().getStackTrace()[0].. => " + meth);
	}
	
	public void testUsingAnonymousObject() {
		String meth = new Object(){}.getClass().getEnclosingMethod().getName();
		System.out.println("test using new Object(){}.. => " + meth);
	}
	
	public void testUsingAnonymousClass() {
		class Local {};
		String meth = Local.class.getEnclosingMethod().getName();
		System.out.println("test using class Local{}.. => " + meth);
	}

//	@SuppressWarnings("restriction")
//	public void testCallerClassUsingSunReflect() {
//		Class<?> callerClass = sun.reflect.Reflection.getCallerClass();
//		// Exception in thread "main" java.lang.InternalError: CallerSensitive annotation expected at frame 1
//		System.out.println("testCallerClassUsingSunReflect => " + callerClass);
//	}
	
	public void testUsingJdk9StackWalker() {
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
