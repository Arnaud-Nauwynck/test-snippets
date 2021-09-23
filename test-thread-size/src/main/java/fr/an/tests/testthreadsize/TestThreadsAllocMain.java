package fr.an.tests.testthreadsize;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * simple test to launch as many threads as possible
 * 
 * without parameters:
 * Failed to allocate/start Thread 2860 .. stopping
 * 
 * same in Eclipse Debug mode:
 * Failed to allocate/start Thread 2817 .. stopping
 * 
 * -Xmx32m -Xms32m
 * ..same
 * 
 * -Xmx256m -Xms256m
 * 
 * using  -Xss10k
 * Failed to allocate/start Thread 5031 .. stopping
 * 
 * using -Xmx256m -Xms256m -Xss200k
 * Failed to allocate/start Thread 3120.. stopping
 * 
 * (default Xss ?)
 * using -Xmx256m -Xms256m -Xss300k
 * Failed to allocate/start Thread 2861 .. stopping
 * 
 * using -Xmx256m -Xms256m -Xss400k
 * Failed to allocate/start Thread 2347 .. stopping
 * 
 * using -Xmx256m -Xms256m -Xss500k
 * Failed to allocate/start Thread 2155 .. stopping
 * 
 */
public class TestThreadsAllocMain {

	private static AtomicBoolean sleep = new AtomicBoolean(true);
	
	public static void main(String[] args) {
		for(int i = 0; ; i++) {
			try {
				Thread thread = new Thread(() -> sleepRun());
				thread.start();
			} catch(Throwable ex) {
				System.out.println("Failed to allocate/start Thread " + i + " .. stopping");
				sleep.set(false);
				break;
			}
		}
	}

	private static void sleepRun() {
		while (sleep.get()) {
			try {
				Thread.sleep(10_000);
			} catch (InterruptedException e) {
				System.err.println("Interrupted");
			}
		}
	}
}
