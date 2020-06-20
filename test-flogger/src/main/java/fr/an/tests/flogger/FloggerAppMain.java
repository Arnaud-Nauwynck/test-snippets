package fr.an.tests.flogger;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.StackSize;
import com.google.common.flogger.backend.Platform;

public class FloggerAppMain {
	static {
//		String configInfo = Platform.getConfigInfo();
//		System.out.println("flogger Plateform.getConfigInfo: " + configInfo);
//		// => 
//		flogger Plateform.getConfigInfo: Platform: com.google.common.flogger.backend.system.DefaultPlatform
//		BackendFactory: Default logger backend factory
//		Clock: Default millisecond precision clock
//		LoggingContext: Empty logging context
//		LogCallerFinder: Default stack-based caller finder
		
		System.setProperty("flogger.backend_factory", "com.google.common.flogger.backend.slf4j.Slf4jBackendFactory#getInstance");
		String configInfo2 = Platform.getConfigInfo();
		System.out.println("flogger Plateform.getConfigInfo: " + configInfo2);
		// =>
//		flogger Plateform.getConfigInfo: Platform: com.google.common.flogger.backend.system.DefaultPlatform
//		BackendFactory: SLF4J backend
//		Clock: Default millisecond precision clock
//		LoggingContext: Empty logging context
//		LogCallerFinder: Default stack-based caller finder
	}
	private static final FluentLogger log = FluentLogger.forEnclosingClass();

	
	public static void main(String[] args) throws Exception {
		testSlf4j();

		log.atInfo().log("test flogger info msg");
		
		log.atInfo().with(MetadataKey.single("key1", String.class), "value1")
			.with(MetadataKey.single("key2", String.class), "value2")
			.log("test flogger info msg");
		// =>
//		2020-06-20 19:59:56,313 [main] INFO  fr.an.tests.flogger.FloggerAppMain - test flogger info msg [CONTEXT key1="value1" key2="value2" ]
		
		log.atWarning().log("test flogger warn msg");

		recurseThenLog(50, StackSize.SMALL);
		recurseThenLog(50, StackSize.MEDIUM);
		recurseThenLog(50, StackSize.LARGE);
		recurseThenLog(50, StackSize.NONE);

		for(int i = 0; i < 30; i++) {
			log.atInfo().every(10).log("log msg every 10 times .. %d", i);
			// => 
//			2020-06-20 19:50:38,912 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 10 times .. 0 [CONTEXT ratelimit_count=10 ]
//			2020-06-20 19:50:38,912 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 10 times .. 10 [CONTEXT ratelimit_count=10 ]
//			2020-06-20 19:50:38,912 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 10 times .. 20 [CONTEXT ratelimit_count=10 ]
		}

		for(int i = 0; i < 30; i++) {
			// this work by using the LogSite = filename + line number...
			log.atInfo().every(10).log("log msg every 10 times .. (eval text) " + i);
		}
		
		for(int i = 0; i < 30; i++) {
			log.atInfo().atMostEvery(1, TimeUnit.SECONDS).log("log msg every 1s .. %d", i);
			Thread.sleep(100);
			// =>
//			2020-06-20 19:50:38,914 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 1s .. 0 [CONTEXT ratelimit_period="1 SECONDS" ]
//			2020-06-20 19:50:39,916 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 1s .. 10 [CONTEXT ratelimit_period="1 SECONDS [skipped: 9]" ]
//			2020-06-20 19:50:40,918 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log msg every 1s .. 20 [CONTEXT ratelimit_period="1 SECONDS [skipped: 9]" ]
		}

		for(int i = 0; i < 30; i++) {
			log.atInfo().atMostEvery(1, TimeUnit.SECONDS).log("log msg every 1s .. (eveal text) " + i);
			Thread.sleep(100);
		}

		
		log.atInfo().every(3).log("log every 3:a"); log.atInfo().every(3).log("log every 3:b"); log.atInfo().every(3).log("log every 3:c"); log.atInfo().every(3).log("log every 3:d");
		// same line!!! => 
//		2020-06-20 19:57:35,297 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log every 3:a [CONTEXT ratelimit_count=3 ]
//		2020-06-20 19:57:35,297 [main] INFO  fr.an.tests.flogger.FloggerAppMain - log every 3:d [CONTEXT ratelimit_count=3 ]
		

	}

	private static void recurseThenLog(int depth, StackSize logStackSize) {
		if (depth > 0) {
			recurseThenLog(depth-1, logStackSize);
		} else {
			log.atInfo().withStackTrace(logStackSize).log("test flogger info msg with stacktrace-" + logStackSize);
		}
	}
	

	private static void testSlf4j() {
		Logger slf4jlog = LoggerFactory.getLogger(FloggerAppMain.class);
		slf4jlog.info("test slf4j message");
		slf4jlog.warn("test slf4j message");
	}

	
	
}
