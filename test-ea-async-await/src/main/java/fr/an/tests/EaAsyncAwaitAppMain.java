package fr.an.tests;

import static com.ea.async.Async.await;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EaAsyncAwaitAppMain {

	private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);
	private static final ScheduledCompletableHelper scheduledCompletableHelper = new ScheduledCompletableHelper(scheduledExecutor);

	public static void main(String[] args) throws Exception {
		new EaAsyncAwaitAppMain().run();

		System.out.println("shutdown");
		scheduledExecutor.shutdown();
		System.out.println("Exiting");

	}

	private void run() throws InterruptedException, ExecutionException {
		String res = asyncFoo().get();
		System.out.println("run => " + res);
	}

	public CompletableFuture<String> asyncFoo() {
        showCurrentStackTrace("asyncFoo");
        String res1 = await(asyncBar("init", "1"));
        showCurrentStackTrace("asyncFoo after step 1: asyncBar");
		String res2 = await(asyncBar(res1, "2"));
        showCurrentStackTrace("asyncFoo after step 2: asyncBar");
        return completedFuture(res2);
    }

	private CompletionStage<String> asyncBar(String text1, String text2) {
		showCurrentStackTrace("asyncBar");
		// return completedFuture(text1 + "," + text2);
		return scheduledCompletableHelper.<String>schedule(
				() -> doBar(text1, text2),
				1L, TimeUnit.SECONDS);
	}

	private static String doBar(String text1, String text2) {
		showCurrentStackTrace("doBar");
		return text1 + "," + text2;
	}

	private static void showCurrentStackTrace(String meth) {
		String threadName = Thread.currentThread().getName();
		System.out.println("calling " + meth + " from thread: '" + threadName + "' with stack:");
		new Exception().printStackTrace(System.out);
	}
	
}
