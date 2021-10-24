package fr.an.tests.hadoopfsinstrumented;

import static java.lang.System.nanoTime;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.statistics.IOStatistics;

import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSOutputStreamStats;
import lombok.Getter;

public class InstrumentedFSDataOutputStream extends FSDataOutputStream {

	private final FSDataOutputStream delegate;
	
	@Getter
	private final Path path;
	
	@Getter
	private final InstrumentedFSOutputStreamStats stats;
	
	// --------------------------------------------------------------------------------------------

	public InstrumentedFSDataOutputStream(FSDataOutputStream delegate, Statistics hadoopStats, //
			Path path, InstrumentedFSOutputStreamStats stats) {
		super(delegate, hadoopStats);
		this.delegate = delegate;
		this.path = path;
		this.stats = stats;
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	// overwrite default implementation of FSDataOutputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public long getPos() {
		return delegate.getPos();
	}

	@Override
	public OutputStream getWrappedStream() {
		return delegate.getWrappedStream();
	}

	@Override
	public boolean hasCapability(String capability) {
		return delegate.hasCapability(capability);
	}

	@Override
	public void hflush() throws IOException {
		long startNanos = nanoTime();
		delegate.hflush();
		long nanos = nanoTime() - startNanos;
		stats.hflushStats.increment(nanos);
	}

	@Override
	public void hsync() throws IOException {
		long startNanos = nanoTime();
		delegate.hsync();
		long nanos = nanoTime() - startNanos;
		stats.hsyncStats.increment(nanos);
	}

	@Override
	public void setDropBehind(Boolean dropBehind) throws IOException {
		delegate.setDropBehind(dropBehind);
	}

    /**
     * redefine private in super class
     */
    private void incCount(int value) {
        int temp = written + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        written = temp;
    }
    
	// override from java.io.OutputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public void write(int b) throws IOException {
		long startNanos = nanoTime();
		delegate.write(b);
		long nanos = nanoTime() - startNanos;
		stats.writeBytesStats.increment(nanos, 1);
		incCount(1);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		long startNanos = nanoTime();
		delegate.write(b, off, len);
		long nanos = nanoTime() - startNanos;
		stats.writeBytesStats.increment(nanos, len);
		incCount(len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		// write(b, 0, b.length);
		super.write(b);
	}

	@Override
	public void flush() throws IOException {
		long startNanos = nanoTime();
		delegate.flush();
		long nanos = nanoTime() - startNanos;
		stats.flushStats.increment(nanos);
	}

	@Override
	public IOStatistics getIOStatistics() {
		return delegate.getIOStatistics();
	}

	@Override
	public AbortableResult abort() {
		return delegate.abort();
	}
	
	// override from java.lang.Object
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
		return "InstrumentedFSDataOutputStream{" + delegate.toString() + "}";
	}

}
