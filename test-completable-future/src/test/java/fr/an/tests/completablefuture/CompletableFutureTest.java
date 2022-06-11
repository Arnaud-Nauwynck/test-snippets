package fr.an.tests.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.junit.Test;

import lombok.val;

public class CompletableFutureTest {

	/**
	 * <PRE>
	 * [main] register cf.thenAccept( -> ..)
     * [main] cf.complete('test') ..
     * [main] exec thenAccept callback arg:test
     * [main] .. done cf.complete('test')
	 * </PRE>
	 */
	@Test
	public void test1() {
		val cf = new CompletableFuture<String>();
		msgCurrThread("register cf.thenAccept( -> ..)");
		cf.thenAccept(arg -> {
			msgCurrThread("exec thenAccept callback arg:" + arg);
		});
		msgCurrThread("cf.complete('test') ..");
		cf.complete("test");
		msgCurrThread(".. done cf.complete('test')");
	}
	
	/**
	 * <PRE>
	 * [main] register cf.thenAccept( -> callback 1 ..)
     * [main] register cf.thenAccept( -> callback 2 ..)
     * [main] cf.complete('test') ..
     * [main] exec thenAccept callback 2 arg:test
     * [main] exec thenAccept callback 1 arg:test
     * [main] .. done cf.complete('test')
     * </PRE>
	 */
	@Test
	public void test_thenAccept_multiple() {
		val cf = new CompletableFuture<String>();
		msgCurrThread("register cf.thenAccept( -> callback 1 ..)");
		cf.thenAccept(arg -> {
			msgCurrThread("exec thenAccept callback 1 arg:" + arg);
		});
		msgCurrThread("register cf.thenAccept( -> callback 2 ..)");
		cf.thenAccept(arg -> {
			msgCurrThread("exec thenAccept callback 2 arg:" + arg);
		});
		msgCurrThread("cf.complete('test') ..");
		cf.complete("test");
		msgCurrThread(".. done cf.complete('test')");
	}
	
	/**
	 * <PRE>
	 * [main] cf.complete('test') ..
     * [main] .. done cf.complete('test')
     * [main] register cf.thenAccept( -> callback 1 ..)
     * [main] thenAccept (1) arg:test
     * [main] register cf.thenAccept( -> callback 2 ..)
     * [main] thenAccept (2) arg:test
     * </PRE>
	 */
	@Test
	public void test_complete_thenAccept() {
		val cf = new CompletableFuture<String>();
		msgCurrThread("cf.complete('test') ..");
		cf.complete("test");
		msgCurrThread(".. done cf.complete('test')");
		msgCurrThread("register cf.thenAccept( -> callback 1 ..)");
		cf.thenAccept(arg -> {
			msgCurrThread("thenAccept (1) arg:" + arg);
		});
		msgCurrThread("register cf.thenAccept( -> callback 2 ..)");
		cf.thenAccept(arg -> {
			msgCurrThread("thenAccept (2) arg:" + arg);
		});
	}

	/**
	 * <PRE>
	 * [main] task = pool.submit( -> ..)
     * [main] blocking task.get()
     * [main] exec task callback 
     * [main] .. task.get() => test
     * [main] pool.execute(task) // => should do nothing! already executed
	 * </PRE>
	 */
	@Test
	public void test_ForkJoinPool_submit_execute_alreadyDone() throws Exception {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		msgCurrThread("task = pool.submit( -> ..)");
		ForkJoinTask<String> task = pool.submit(() -> {
			msgCurrThread("exec task callback ");
			return "test";
		});
		msgCurrThread("blocking task.get()");
		val res = task.get();
		msgCurrThread(".. task.get() => " + res);
		
		msgCurrThread("pool.execute(task) // => should do nothing! already executed"); 
		pool.execute(task);
	}

	/**
	 * <PRE>
	 * [main] pool.execute(task)
     * [main] blocking task.get()
     * [ForkJoinPool.commonPool-worker-5] exec task 0 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 1 callback 
     * 
     * [main] exec task callback ******
     * 
     * [main] .. task.get() => test
     * [main] latch.await()
     * [ForkJoinPool.commonPool-worker-7] exec task 2 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 3 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 5 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 4 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 6 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 7 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 12 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 11 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 9 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 8 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 10 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 13 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 14 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 19 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 18 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 16 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 15 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 17 callback 
     * [main] .. await done
	 * </PRE>
	 */
	@Test
	public void test_ForkJoinPool_submit_execute_explicitGet() throws Exception {
		doTest_ForkJoinPool_submit_execute(true, true);
	}

	/**
	 * <PRE>
	 * [main] N x tasks = pool.submit( -> ..)
     * [main] pool.execute(task)
     * [main] latch.await()
     * [ForkJoinPool.commonPool-worker-3] exec task 0 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 1 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 2 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 3 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 4 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 5 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 6 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 7 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 8 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 12 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 13 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 10 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 11 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 9 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 15 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 14 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 17 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 16 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 18 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 19 callback 
     * 
     * [ForkJoinPool.commonPool-worker-7] exec task callback ******
     * 
     * [main] .. await done
     * 	 * </PRE>
	 */
	@Test
	public void test_ForkJoinPool_submit_execute_noexplicitGet() throws Exception {
		doTest_ForkJoinPool_submit_execute(true, false);
	}
	
	/**
	 * <PRE>
	 * [main] N x tasks = pool.submit( -> ..)
     * [main] blocking task.get()
     * [ForkJoinPool.commonPool-worker-3] exec task 0 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 1 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 2 callback
     *  
     * [main] exec task callback ******
     *  
     * [ForkJoinPool.commonPool-worker-9] exec task 3 callback 
     * [main] .. task.get() => test
     * [main] latch.await()
     * [ForkJoinPool.commonPool-worker-13] exec task 5 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 4 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 6 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 7 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 8 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 11 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 13 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 9 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 12 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 10 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 14 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 16 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 15 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 17 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 19 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 18 callback 
     * [main] .. await done
	 * </PRE>
	 */
	@Test
	public void test_ForkJoinPool_submit_noexecute_explicitGet() throws Exception {
		doTest_ForkJoinPool_submit_execute(false, true);
	}

	/**
	 * <PRE>
	 * [main] N x tasks = pool.submit( -> ..)
     * [main] latch.await()
     * [ForkJoinPool.commonPool-worker-5] exec task 1 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 0 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 3 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 2 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 5 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 4 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 6 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 7 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 13 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 12 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 11 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 9 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 10 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 8 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 14 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 15 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 18 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 19 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 17 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 16 callback 
     * 
     * [ForkJoinPool.commonPool-worker-13] exec task callback ******
     * 
     * [main] .. await done
	 * </PRE>
	 */
	@Test
	public void test_ForkJoinPool_submit_noexecute_noExplicitGet() throws Exception {
		doTest_ForkJoinPool_submit_execute(false, false);
	}
	
	protected void doTest_ForkJoinPool_submit_execute(boolean explicitExecTask, boolean explicitTaskGet) throws Exception {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		int taskCount = 20;
		val latch = new CountDownLatch(taskCount + 1); 
		msgCurrThread("N x tasks = pool.submit( -> ..)");
		for(int i = 0; i < taskCount; i++) {
			val fI = i;
			ForkJoinTask<String> taskI = pool.submit(() -> {
				msgCurrThread("exec task " + fI + " callback ");
				sleep();
				latch.countDown();
				return "test";
			});
		}
		ForkJoinTask<String> task = pool.submit(() -> {
			msgCurrThread("exec task callback ******");
			latch.countDown();
			return "test";
		});
		if (explicitExecTask) {
			msgCurrThread("pool.execute(task)"); 
			pool.execute(task); // javadoc: arrange for "asynchronous" execution of the task 
			// ... task already submitted => do nothing?? 
			// (will be done later, maybe immediatly after in task.get() in main thread) ??
		}
		
		if (explicitTaskGet) {
			msgCurrThread("blocking task.get()");
			val res = task.get();
			msgCurrThread(".. task.get() => " + res);
		}
		
		msgCurrThread("latch.await()");
		latch.await();
		msgCurrThread(".. await done");
	}

	
	/**
	 * <PRE>
	 * [main] N x tasks = pool.submit( -> ..)
     * [main] anotherPool.execute(task)
     * [ForkJoinPool.commonPool-worker-5] exec task 1 callback 
     * [main] latch.await()
     * [ForkJoinPool.commonPool-worker-3] exec task 0 callback 
     * 
     * [ForkJoinPool-1-worker-3] exec task callback ******
     * 
     * [ForkJoinPool.commonPool-worker-9] exec task 3 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 2 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 5 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 4 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 6 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 8 callback 
     * [ForkJoinPool.commonPool-worker-5] exec task 7 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 9 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 12 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 13 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 11 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 10 callback 
     * [ForkJoinPool.commonPool-worker-9] exec task 14 callback 
     * [ForkJoinPool.commonPool-worker-13] exec task 15 callback 
     * [ForkJoinPool.commonPool-worker-15] exec task 16 callback 
     * [ForkJoinPool.commonPool-worker-3] exec task 17 callback 
     * [ForkJoinPool.commonPool-worker-11] exec task 18 callback 
     * [ForkJoinPool.commonPool-worker-7] exec task 19 callback 
     * [main] .. await done
	 * </PRE>
	 */
	@Test
	public void doTest_ForkJoinPool_submit_executeInAnotherPool() throws Exception {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		val anotherPool = new ForkJoinPool(1);
		int taskCount = 20;
		val latch = new CountDownLatch(taskCount + 1);
		msgCurrThread("N x tasks = pool.submit( -> ..)");
		for(int i = 0; i < taskCount; i++) {
			val fI = i;
			pool.submit(() -> {
				msgCurrThread("exec task " + fI + " callback ");
				sleep();
				latch.countDown();
				return "test";
			});
		}
		ForkJoinTask<String> task = pool.submit(() -> {
			msgCurrThread("exec task callback ******");
			latch.countDown();
			return "test";
		});

		msgCurrThread("anotherPool.execute(task)"); 
		anotherPool.execute(task);
		
		msgCurrThread("latch.await()");
		latch.await();
		msgCurrThread(".. await done");
		anotherPool.shutdown();
	}
	
	/**
	 * <PRE>
	 * [ForkJoinPool.commonPool-worker-3] exec runAsync task
	 * [ForkJoinPool.commonPool-worker-3] exec future.thenAccept
	 * </PRE>
	 */
	@Test
	public void test_runAsync() throws Exception {
		val latch = new CountDownLatch(1); 
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			msgCurrThread("exec runAsync task");
			sleep();
		});
		future.thenAccept(voidArg -> {
			msgCurrThread("exec future.thenAccept");
			latch.countDown();
		});
		latch.await();
	}

	/**
	 * <PRE>
	 * [ForkJoinPool.commonPool-worker-3] exec runAsync task
     * [ForkJoinPool.commonPool-worker-3] exec future.thenAccept test
	 * </PRE>
	 */
	@Test
	public void test_supplyAsync() throws Exception {
		val latch = new CountDownLatch(1); 
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			msgCurrThread("exec runAsync task");
			sleep();
			return "test";
		});
		future.thenAccept(arg -> {
			msgCurrThread("exec future.thenAccept " + arg);
			latch.countDown();
		});
		latch.await();
	}

	/**
	 * <PRE>
	 * [ForkJoinPool-1-worker-3] exec runAsync task
     * [ForkJoinPool-1-worker-3] exec future.thenAccept 
	 * </PRE>
	 */
	@Test
	public void test_runAsync_anotherPool() throws Exception {
		val latch = new CountDownLatch(1); 
		val anotherPool = new ForkJoinPool(1);
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			msgCurrThread("exec runAsync task");
			sleep();
		}, anotherPool);
		future.thenAccept(voidArg -> {
			msgCurrThread("exec future.thenAccept ");
			latch.countDown();
		});
		latch.await();
		anotherPool.shutdown();
	}

	
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
			
	protected static void msgCurrThread(String msg) {
		val name = Thread.currentThread().getName();
		System.out.println("[" + name + "] " + msg);
	}

}
