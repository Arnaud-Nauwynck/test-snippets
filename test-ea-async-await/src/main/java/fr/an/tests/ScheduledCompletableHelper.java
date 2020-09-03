package fr.an.tests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ScheduledCompletableHelper {
	
	private ScheduledExecutorService executor;
	
	public ScheduledCompletableHelper(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public <T> CompletableFuture<T> schedule(
			Supplier<T> command, 
			long delay, TimeUnit unit) {
		CompletableFuture<T> completableFuture = new CompletableFuture<>();
		executor.schedule(() -> execToCompletedFuture(command, completableFuture), delay, unit);
		return completableFuture;
	}

	private static <T> Boolean execToCompletedFuture(Supplier<T> command, CompletableFuture<T> completableFuture) {
		try {
			T res = command.get();
			return completableFuture.complete(res);
		} catch (Throwable t) {
			return completableFuture.completeExceptionally(t);
		}
	}

}
