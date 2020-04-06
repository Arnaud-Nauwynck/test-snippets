package fr.an.test.glowroot;

import java.util.concurrent.atomic.AtomicInteger;

import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindReceiver;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class ThreadAspect extends AbstractFilePluginAspect {

	static AtomicInteger threadConstructorCount = new AtomicInteger();
	static AtomicInteger threadExitCount  = new AtomicInteger();
	static AtomicInteger currentThreadAliveCount = new AtomicInteger();
	
    /**
     * Instrument calls to <code>java.lang.Thread.<init>()</code>
     */
    @Pointcut(className = "java.lang.Thread", 
    		methodName = "<init>", 
    		methodParameterTypes = {
    				"java.lang.ThreadGroup", "java.lang.Runnable", 
    				"java.lang.String", // name
    				"long", // stackSize,
    				"java.security.AccessControlContext", // 
    				"boolean", // inheritThreadLocals"
    				})
    public static class Thread_ConstructorAdvice {
        @OnBefore
        public static void onBefore(
        		@BindParameter String name) {
        	int count = threadConstructorCount.incrementAndGet();
    		int active = currentThreadAliveCount.incrementAndGet();
        	logCall("(" + count + ") Thread.<init>() '" + name + "'.. currActive:" + active);
        }
    }

    /**
     * Instrument calls to <code>java.lang.Thread.exit()</code>
     */
    @Pointcut(className = "java.lang.Thread", 
    		methodName = "exit", methodParameterTypes = {})
    public static class ThreadPoolExecutor_ShutdownAdvice {
        @OnBefore
        public static void onBefore(@BindReceiver Thread thisThread) {
        	String name = thisThread.getName();
        	int count = threadExitCount.incrementAndGet();
    		int active = currentThreadAliveCount.decrementAndGet();
    		logCall("(" + count + ") Thread.exit() '" + name + "' .. currActive:" + active);
        }
    }

//    /**
//     * Instrument calls to <code>java.lang.Thread.start()</code>
//     */
//    @Pointcut(className = "java.lang.Thread", 
//    		methodName = "start", methodParameterTypes = {})
//    public static class ThreadPoolExecutor_ShutdownNowAdvice {
//        @OnBefore
//        public static void onBefore() {
//        	int count = threadStartCount.incrementAndGet();
//    		logCall("(" + count + ") Thread.start()");
//        }
//    }

}
