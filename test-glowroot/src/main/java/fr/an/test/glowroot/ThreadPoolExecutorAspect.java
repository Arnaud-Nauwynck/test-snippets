package fr.an.test.glowroot;

import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class ThreadPoolExecutorAspect extends AbstractFilePluginAspect {

	static int threadPoolConstructorCount;
	static int threadPoolShutdownCount;
	static int threadPoolShutdownNowCount;
	
    /**
     * Instrument calls to <code>java.util.concurrent.ThreadPoolExecutor.<init>()</code>
     */
    @Pointcut(className = "java.util.concurrent.ThreadPoolExecutor", 
    		methodName = "<init>", 
    		methodParameterTypes = {"int",// corePoolSize 
    				"int", // maximumPoolSize,
    				"long", // keepAliveTime", //
    				"java.util.concurrent.TimeUnit", // unit 
    				"java.util.concurrent.BlockingQueue", // workQueue 
    				"java.util.concurrent.ThreadFactory", // threadFactory 
    				"java.util.concurrent.RejectedExecutionHandler" // handler"
    				})
    public static class ThreadPoolExecutor_ConstructorAdvice {
        @OnBefore
        public static void onBefore() {
        	threadPoolConstructorCount++;
    		logCall("(" + threadPoolConstructorCount + ") ThreadPoolExecutor.<init>()");
        }
    }

    /**
     * Instrument calls to <code>java.util.concurrent.ThreadPoolExecutor.shutdown()</code>
     */
    @Pointcut(className = "java.util.concurrent.ThreadPoolExecutor", 
    		methodName = "shutdown", methodParameterTypes = {})
    public static class ThreadPoolExecutor_ShutdownAdvice {
        @OnBefore
        public static void onBefore() {
        	threadPoolShutdownCount++;
    		logCall("(" + threadPoolShutdownCount + ") ThreadPoolExecutor.shutdown()");
        }
    }

    /**
     * Instrument calls to <code>java.util.concurrent.ThreadPoolExecutor.shutdownNow()</code>
     */
    @Pointcut(className = "java.util.concurrent.ThreadPoolExecutor", 
    		methodName = "shutdownNow", methodParameterTypes = {})
    public static class ThreadPoolExecutor_ShutdownNowAdvice {
        @OnBefore
        public static void onBefore() {
        	threadPoolShutdownNowCount++;
    		logCall("(" + threadPoolShutdownNowCount + ") ThreadPoolExecutor.shutdownNow()");
        }
    }

}
