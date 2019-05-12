package fr.an.test.fsscancomp.nio;

import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class ExecutorMd5EnricherEntryHandler extends ImageEntryHandler {

	private static ImageEntry END_MARKER = new ImageEntry(false, "", 0, 0, null);
	
	private final ImageEntryHandler delegate;

	private final ExecutorService executor;
	private BlockingQueue<Future<ImageEntry>> queue = new LinkedBlockingQueue<>();
	
	private Future<?> consumerToDelegateFuture;
	private Throwable mainConsumeToDelegateLoopEx;
	
	private AtomicLong md5NanoTotal = new AtomicLong();
	private AtomicInteger errorCount = new AtomicInteger();


	private static class Buffer {
		ByteBuffer buff = ByteBuffer.allocate(16*4096);
		MessageDigest md = NioMD5Utils.safeMD5Digest();
	}
	private ThreadLocal<Buffer> threadLocalBuffer = new ThreadLocal<Buffer>() {
		@Override
		protected Buffer initialValue() {
			return new Buffer();
		}
	};
	
	/** executor MUST contains at least 2 Threads */
	public ExecutorMd5EnricherEntryHandler(
			ImageEntryHandler delegate, ExecutorService executor) {
		this.delegate = delegate;
		this.executor = executor;
		startConsumerToDelegate();
	}
	
	public long getMd5NanoTotal() {
		return md5NanoTotal.longValue();
	}

	public int getErrorCount() {
		return errorCount.intValue();
	}

	@Override
	public void handle(ImageEntry entry) {
		if (!entry.isFile) {
			queue.add(CompletableFuture.completedFuture(entry));
		} else {
			queue.add(executor.submit(() -> enrichEntry(entry)));
		}
	}
	
	@Override
	public void close() {
		try {
			// wait consume all..
			waitFinishConsumeToDelegate();
		} finally {
			delegate.close();
		}
	}
	
	
	protected ImageEntry enrichEntry(ImageEntry entry) {
		long startNano = System.nanoTime();
		Path path = FileSystems.getDefault().getPath(entry.path);
		try {
			Buffer buffer = threadLocalBuffer.get();
			entry.md5 = NioMD5Utils.md5(path, buffer.md, buffer.buff);
			buffer.md.reset();
		} catch(Exception ex) {
			// ignore, no rethrow!
			errorCount.incrementAndGet();
		}
		long nano = System.nanoTime() - startNano;
		md5NanoTotal.addAndGet(nano);
		return entry;
	}

	protected void startConsumerToDelegate() {
	    this.consumerToDelegateFuture = executor.submit(() -> {
            try {
            	mainConsumeToDelegateLoop();
            } catch(InterruptedException| ExecutionException ex) {
            	mainConsumeToDelegateLoopEx = ex;
                // no rethrow! executed within thread
            } catch (RuntimeException ex) {
            	mainConsumeToDelegateLoopEx = ex;
                // no rethrow! executed within thread
            }
	    });
	}
	
	private void mainConsumeToDelegateLoop() throws InterruptedException, ExecutionException {
	    for(;;) {
	        Future<ImageEntry> future = queue.take();
	        ImageEntry entry = future.get();
	
	        if (entry == END_MARKER) {
	            break;
	        }

	        delegate.handle(entry);
	    }
	}
	
	public void waitFinishConsumeToDelegate() {
		queue.add(CompletableFuture.completedFuture(END_MARKER));
		
	    try {
	    	consumerToDelegateFuture.get();
	    } catch(InterruptedException ex) {
	    	mainConsumeToDelegateLoopEx = ex;
	    } catch(ExecutionException ex) {
	    	mainConsumeToDelegateLoopEx = ex.getCause();
	    }
	    
	    if (mainConsumeToDelegateLoopEx != null) {
	    	if (mainConsumeToDelegateLoopEx instanceof RuntimeException) {
	    		throw (RuntimeException)mainConsumeToDelegateLoopEx;
	    	} else {
	    		throw new RuntimeException("Failed", mainConsumeToDelegateLoopEx);
	    	}
	    }
	}
	
}
