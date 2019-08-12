package fr.an.fssync.fs.nio;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class ExecutorMd5EnricherEntryVisitor extends FsEntryVisitor {

    private static FsEntry END_MARKER = new FsEntry(null, null);

    private final Path rootPath;
    private final FsEntryVisitor delegate;

    private final ExecutorService executor;
    private BlockingQueue<Future<FsEntry>> queue = new LinkedBlockingQueue<>();

    private Future<?> consumerToDelegateFuture;
    private Throwable mainConsumeToDelegateLoopEx;

    private AtomicLong md5NanoTotal = new AtomicLong();
    private AtomicInteger errorCount = new AtomicInteger();

    private static class Buffer {
	ByteBuffer buff = ByteBuffer.allocate(16 * 4096);
	MessageDigest md = NioMD5Utils.safeMD5Digest();
    }

    private ThreadLocal<Buffer> threadLocalBuffer = new ThreadLocal<Buffer>() {
	@Override
	protected Buffer initialValue() {
	    return new Buffer();
	}
    };

    /** executor MUST contains at least 2 Threads */
    public ExecutorMd5EnricherEntryVisitor(Path rootPath, FsEntryVisitor delegate, ExecutorService executor) {
	this.rootPath = rootPath;
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
    public void begin() {
	// do nothing
    }

    @Override
    public void visit(FsEntry entry) {
	if (!entry.isFile()) {
	    queue.add(CompletableFuture.completedFuture(entry));
	} else {
	    queue.add(executor.submit(() -> enrichEntry(entry)));
	}
    }

    @Override
    public void end() {
	try {
	    // wait consume all..
	    waitFinishConsumeToDelegate();
	} finally {
	    delegate.end();
	}
    }

    protected FsEntry enrichEntry(FsEntry entry) {
	long startNano = System.nanoTime();
	Path osPath = rootPath.resolve(entry.path.toUri()); // FileSystems.getDefault().getPath(entry.path);
	try {
	    Buffer buffer = threadLocalBuffer.get();
	    String md5 = NioMD5Utils.md5(osPath, buffer.md, buffer.buff);
	    if (!Objects.equals(md5, entry.info.md5)) {
		entry.info = entry.info.builderCopy().md5(md5).build();
	    }
	    buffer.md.reset();
	} catch (Exception ex) {
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
	    } catch (InterruptedException | ExecutionException ex) {
		mainConsumeToDelegateLoopEx = ex;
		// no rethrow! executed within thread
	    } catch (RuntimeException ex) {
		mainConsumeToDelegateLoopEx = ex;
		// no rethrow! executed within thread
	    }
	});
    }

    private void mainConsumeToDelegateLoop() throws InterruptedException, ExecutionException {
	for (;;) {
	    Future<FsEntry> future = queue.take();
	    FsEntry entry = future.get();

	    if (entry == END_MARKER) {
		break;
	    }

	    delegate.visit(entry);
	}
    }

    public void waitFinishConsumeToDelegate() {
	queue.add(CompletableFuture.completedFuture(END_MARKER));

	try {
	    consumerToDelegateFuture.get();
	} catch (InterruptedException ex) {
	    mainConsumeToDelegateLoopEx = ex;
	} catch (ExecutionException ex) {
	    mainConsumeToDelegateLoopEx = ex.getCause();
	}

	if (mainConsumeToDelegateLoopEx != null) {
	    if (mainConsumeToDelegateLoopEx instanceof RuntimeException) {
		throw (RuntimeException) mainConsumeToDelegateLoopEx;
	    } else {
		throw new RuntimeException("Failed", mainConsumeToDelegateLoopEx);
	    }
	}
    }

}
