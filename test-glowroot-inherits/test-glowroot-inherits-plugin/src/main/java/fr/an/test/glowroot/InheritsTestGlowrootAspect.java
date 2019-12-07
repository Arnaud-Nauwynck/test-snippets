package fr.an.test.glowroot;

import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class InheritsTestGlowrootAspect {

    static int aOpenCount = 0;
    static int bOpenCount = 0;

    /**
     * Instrument calls to <code>fr.an.test.glowroot.A.open()</code>
     */
    @Pointcut(className = "fr.an.test.glowroot.A", methodName = "open", methodParameterTypes = {},
	    subTypeRestriction = "fr.an.test.glowroot.A")
    public static class Aopen_Pointcut {

	@OnBefore
	public static void onBefore() {
	    aOpenCount++;
	    logCall("A.open() .. count current opened A:" + aOpenCount);
	}

    }
    
    /**
     * Instrument calls to <code>fr.an.test.glowroot.A.close()</code>
     */
    @Pointcut(className = "fr.an.test.glowroot.A", methodName = "close", methodParameterTypes = {},
	    subTypeRestriction = "fr.an.test.glowroot.A")
    public static class Aclose_Pointcut {

	@OnBefore
	public static void onBefore() {
	    aOpenCount--;
	    logCall("A.close() .. count current opened A:" + aOpenCount);
	}

    }


    /**
     * Instrument calls to <code>fr.an.test.glowroot.B.open()</code>
     */
    @Pointcut(className = "fr.an.test.glowroot.B", methodName = "open", methodParameterTypes = {},
	    subTypeRestriction = "fr.an.test.glowroot.B")
    public static class Bopen_Pointcut {

	@OnBefore
	public static void onBefore() {
	    bOpenCount++;
	    logCall("B.open() .. count current opened B:" + bOpenCount);
	}

    }
    
    /**
     * Instrument calls to <code>fr.an.test.glowroot.B.close()</code>
     */
    @Pointcut(className = "fr.an.test.glowroot.B", methodName = "close", methodParameterTypes = {},
	    subTypeRestriction = "fr.an.test.glowroot.B")
    public static class Bclose_Pointcut {

	@OnBefore
	public static void onBefore() {
	    bOpenCount--;
	    logCall("B.close() .. count current opened B:" + bOpenCount);
	}

    }

    private static void logCall(String msg) {
	System.out.println(msg);
	System.out.println("at ");
	new Exception().printStackTrace(System.out);
	System.out.println();
    }

}