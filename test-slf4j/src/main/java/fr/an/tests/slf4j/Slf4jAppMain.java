package fr.an.tests.slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.tests.other.OtherFoo;

public class Slf4jAppMain {
	private static final Logger log = LoggerFactory.getLogger(Slf4jAppMain.class);

	public static void main(String[] args) throws Exception {
		System.out.println("Hello");
		
		log.info("START +++++++++++");
		
		log.info("Hello info msg");
		log.debug("Hello debug msg");
		log.warn("Hello warn msg");
		log.error("Hello error msg");
		
		log.info("currentThread name: " + Thread.currentThread().getName());
		
		log.info("before other??");
		OtherFoo.foo();
		log.info("after other??");
		
		testLog4j();

		new Thread() {
			public void run() {
				log.info("from thread... currentThread name: " + Thread.currentThread().getName());
				
			}
		}.start();
				
		Thread.sleep(1000);
		log.info("END -------------");
	}


	private static void testLog4j() {
		org.apache.log4j.Logger log4jLogger = org.apache.log4j.LogManager.getLogger(Slf4jAppMain.class);
		log4jLogger.info("test log4j message");
		log4jLogger.warn("test log4j message");
	}

}
