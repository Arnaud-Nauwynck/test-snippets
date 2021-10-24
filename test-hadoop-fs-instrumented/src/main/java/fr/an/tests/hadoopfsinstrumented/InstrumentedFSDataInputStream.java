package fr.an.tests.hadoopfsinstrumented;

import static java.lang.System.nanoTime;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.ReadOption;
import org.apache.hadoop.fs.statistics.IOStatistics;
import org.apache.hadoop.io.ByteBufferPool;

import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSInputStreamStats;
import lombok.Getter;
import lombok.val;

public class InstrumentedFSDataInputStream extends FSDataInputStream {

	@Getter
	private final Path path;
	
	private final FSDataInputStream delegate;

	@Getter
	private final InstrumentedFSInputStreamStats stats;
	
	// --------------------------------------------------------------------------------------------

	public InstrumentedFSDataInputStream(FSDataInputStream delegate, Path path, InstrumentedFSInputStreamStats stats) {
		super(delegate);
		this.delegate = delegate;
		this.path = path;
		this.stats = stats;
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	// override from org.apache.hadoop.fs.FSDataInputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public InputStream getWrappedStream() {
		return delegate.getWrappedStream();
	}

	@Override
	public void seek(long desired) throws IOException {
		long startNanos = nanoTime();
		delegate.seek(desired);
		long nanos = nanoTime() - startNanos;
		stats.seekStats.increment(nanos);
	}

	@Override
	public long getPos() throws IOException {
		return delegate.getPos();
	}

	@Override
	public int read(long position, byte[] buffer, int offset, int length) throws IOException {
		long startNanos = nanoTime();
		int res = delegate.read(position, buffer, offset, length);
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, res);
		return res;
	}
	
	@Override
	public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
		long startNanos = nanoTime();
		delegate.readFully(position, buffer, offset, length);
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, length);
	}

	@Override
	public void readFully(long position, byte[] buffer) throws IOException {
		long startNanos = nanoTime();
		delegate.readFully(position, buffer);
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, buffer.length);
	}

	@Override
	public boolean seekToNewSource(long targetPos) throws IOException {
		long startNanos = nanoTime();
		val res = delegate.seekToNewSource(targetPos);
		long nanos = nanoTime() - startNanos;
		stats.seekStats.increment(nanos);
		return res;
	}

	@Override
	public int read(ByteBuffer buf) throws IOException {
		long startNanos = nanoTime();
		int res = delegate.read(buf);
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, res);
		return res;
	}

	@Override
	public FileDescriptor getFileDescriptor() throws IOException {
		return delegate.getFileDescriptor();
	}

	@Override
	public void setReadahead(Long readahead) throws IOException, UnsupportedOperationException {
		delegate.setReadahead(readahead);
	}

	@Override
	public void setDropBehind(Boolean dropBehind) throws IOException, UnsupportedOperationException {
		delegate.setDropBehind(dropBehind);
	}

	@Override
	public ByteBuffer read(ByteBufferPool bufferPool, int maxLength, EnumSet<ReadOption> opts) throws IOException, UnsupportedOperationException {
		long startNanos = nanoTime();
		val res = delegate.read(bufferPool, maxLength, opts);
		long nanos = nanoTime() - startNanos;
		val bufferPos = res.position(); // TOCHECK
		stats.readBytesStats.increment(nanos, bufferPos);
		return res;
	}

	@Override
	public void releaseBuffer(ByteBuffer buffer) {
		delegate.releaseBuffer(buffer);
	}

	@Override
	public void unbuffer() {
		delegate.unbuffer();
	}

	@Override
	public boolean hasCapability(String capability) {
		return delegate.hasCapability(capability);
	}

	@Override
	public int read(long position, ByteBuffer buf) throws IOException {
		long startNanos = nanoTime();
		int res = delegate.read(position, buf);
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, res);
		return res;
	}

	@Override
	public void readFully(long position, ByteBuffer buf) throws IOException {
		delegate.readFully(position, buf);
	}

	@Override
	public IOStatistics getIOStatistics() {
		return delegate.getIOStatistics();
	}


	// override from java.io.InputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public int read() throws IOException {
		long startNanos = nanoTime();
		int res = delegate.read();
		long nanos = nanoTime() - startNanos;
		stats.readBytesStats.increment(nanos, 1);
		return res;
	}

	@Override
	public long skip(long n) throws IOException {
		long startNanos = nanoTime();
		long res = delegate.skip(n);
		long nanos = nanoTime() - startNanos;
		stats.skipBytesStats.increment(nanos, n);
		return res;
	}

	@Override
	public int available() throws IOException {
		return delegate.available();
	}

	@Override
	public void mark(int readlimit) {
		delegate.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		long startNanos = nanoTime();
		delegate.reset();
		long nanos = nanoTime() - startNanos;
		stats.resetStats.increment(nanos);
	}

	@Override
	public boolean markSupported() {
		return delegate.markSupported();
	}

	// overwrite from java.lang.Object
	// --------------------------------------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		return "InstrumentedFSDataInputStream{" + delegate.toString() + "}";
	}

}
