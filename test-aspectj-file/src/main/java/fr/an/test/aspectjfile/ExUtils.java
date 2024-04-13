package fr.an.test.aspectjfile;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class ExUtils {

	/* private to force all static */
	private ExUtils() {}

	public static String currentStackTraceShortPath() {
		// return stackTraceToString(new Exception());
		StringBuilder sb = new StringBuilder(250);
		StackTraceElement[] trace = new Exception().getStackTrace();
		String prevClassName = null;
		for (int i = 1; i < trace.length; i++) { // skip first (self) method
        	StackTraceElement traceElement = trace[i];
            appendTraceElement(sb, traceElement, prevClassName);
            prevClassName = traceElement.getClassName();
            if (i + 1 < trace.length) {
            	sb.append("/");
            }
        }
		return sb.toString();
	}
	
	/** @return ex.printStackTrace() as String */
	public static String stackTraceToString(Throwable ex) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(buffer);
		ex.printStackTrace(out);
		return buffer.toString();
	}
	
	

	public static String stackTraceToShortPath(Throwable ex) {
		StringBuilder sb = new StringBuilder(250);
		
		// Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu =
            Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(ex);

        StackTraceElement[] trace = ex.getStackTrace();
        String prevClassName = null;
        for (int i = 0; i < trace.length; i++) {
        	StackTraceElement traceElement = trace[i];
            appendTraceElement(sb, traceElement, prevClassName);
            prevClassName = traceElement.getClassName();
            if (i + 1 < trace.length) {
            	sb.append("/");
            }
        }
        // Print suppressed exceptions, if any
        for (Throwable se : ex.getSuppressed()) {
        	sb.append(" Suppressed ");
            printEnclosedStackTrace(sb, se, trace, dejaVu);
            sb.append(" ");
        }
        
        // Print cause, if any
        Throwable ourCause = ex.getCause();
        if (ourCause != null && ourCause != ex) {
            sb.append(" Caused by ");
        	printEnclosedStackTrace(sb, ourCause, trace, dejaVu);
        }

//        if (sb.length() > 0 && sb.charAt(sb.length()-1) == '/') {
//        	sb.deleteCharAt(sb.length()-1);
//        }
        return sb.toString();
    }

	private static void appendTraceElement(StringBuilder sb, StackTraceElement te, String prevClassName) {
		String fqn = te.getClassName();
		if (fqn != null && !fqn.equals(prevClassName)) {
			int lastDot = fqn.lastIndexOf('.');
			String shortName = fqn;
			if (lastDot != -1) {
				shortName = fqn.substring(lastDot + 1, fqn.length());
			}
			sb.append(shortName);
			sb.append(".");
		}
		sb.append(te.getMethodName());
		if (te.getLineNumber() > 0) {
			sb.append(":" + te.getLineNumber());
		}
	}

    /**
     * Print our stack trace as an enclosed exception for the specified
     * stack trace.
     */
    private static void printEnclosedStackTrace(StringBuilder sb,
    		Throwable ex,
    		StackTraceElement[] enclosingTrace,
    		Set<Throwable> dejaVu) {
        if (dejaVu.contains(ex)) {
            sb.append("[CIRCULAR REFERENCE:" + ex + "]");
        } else {
            dejaVu.add(ex);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = ex.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >=0 && trace[m].equals(enclosingTrace[n])) {
                m--; n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            // s.println(prefix + caption + this);
            String prevClassName = null;
            for (int i = 0; i <= m; i++) {
            	appendTraceElement(sb, trace[i], prevClassName);
            	prevClassName = trace[i].getClassName();
            	if (i + 1 <= m) {
            		sb.append("/");
            	}
            }
            if (framesInCommon != 0) {
                sb.append("... " + framesInCommon + " more");
            }
            // Print suppressed exceptions, if any
            for (Throwable se : ex.getSuppressed()) {
                printEnclosedStackTrace(sb, se, trace, dejaVu);
            }
            
            // Print cause, if any
            Throwable ourCause = ex.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(sb, ourCause, trace, dejaVu);
            }
        }
    }
    
}
