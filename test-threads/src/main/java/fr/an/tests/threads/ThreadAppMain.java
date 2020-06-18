package fr.an.tests.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadAppMain {
	private static final Logger log = LoggerFactory.getLogger(ThreadAppMain.class);

	public static void main(String[] args) throws Exception {
		log.info("info from main thread");
		new Thread() {
			public void run() {
				log.info("from thread... currentThread name: " + Thread.currentThread().getName());
				
			}
		}.start();
		
		testThread_oldSchool();
		testThread_concurrent();
		testThread_reactor(); // TODO ..
		
		Thread.sleep(1000);
	}

	private static void testThread_oldSchool() {
		List<Callable<Double>> tasks = new ArrayList<>();
		for(int src = 0; src < 20; src++) {
			final int srcVal = src;
			tasks.add(() -> {
				log.info("computing " + srcVal);
				Thread.sleep(1000);
				return srcVal * 2.0;
			});
		}
		
		List<Double> results = new ArrayList<>();
		Object lock = new Object();
		Object lockRes = new Object();
		
		for(int i = 0; i < 5; i++) {
			new Thread() {
				public void run() {
					for(;;) {
						Callable<Double> task;
						synchronized(lock) {
							if (tasks.isEmpty()) {
								log.info("thread exiting..");
								return;
							}
							task = tasks.remove(tasks.size() - 1);
						}
						
						
						try {
							Double res = task.call();
					
							synchronized(lockRes) {
								results.add(res);
							}
							
						} catch (Exception e) {
						}
						
					}
				}
			}.start();
		}// for
		
		
		// wait !!!
		for(;;) {
			synchronized(lock) {
				if (tasks.isEmpty()) {
					break;
				}
			}
		}
		log.info("finished waiting");

		
		// My results !!
		for(Double res : results) {
			log.info("My results:" + res);
		}
	}
	
	private static void testThread_concurrent() throws Exception {
		List<Callable<Double>> tasks = new ArrayList<>();
		for(int src = 0; src < 20; src++) {
			final int srcVal = src;
			tasks.add(() -> {
				log.info("computing " + srcVal);
				Thread.sleep(1000);
				return srcVal * 2.0;
			});
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		
		List<Double> resList = submitAndWaitAll(executorService, tasks);
		for(Double res : resList) {
			log.info("My results:" + res);
		}
	}
	
	public static <T> List<T> submitAndWaitAll(ExecutorService executorService, List<Callable<T>> tasks) throws Exception {
		List<T> res = new ArrayList<>();
		List<Future<T>> futures = new ArrayList<>();
		for(Callable<T> task : tasks) {
			Future<T> future = executorService.submit(task);
			futures.add(future);
		}
		// CompletableFuture
		for(Future<T> future : futures) {
			T resItem = future.get();  // wait..
			res.add(resItem);
		}
		return res;
	}
	
	
	public static void testThread_reactor() {
		
	}
	
}
