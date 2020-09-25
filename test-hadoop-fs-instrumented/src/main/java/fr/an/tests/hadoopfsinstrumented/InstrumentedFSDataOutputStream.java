package fr.an.tests.hadoopfsinstrumented;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem.Statistics;

public class InstrumentedFSDataOutputStream extends FSDataOutputStream {

	private final FSDataOutputStream delegate;

	// --------------------------------------------------------------------------------------------

	public InstrumentedFSDataOutputStream(OutputStream out, Statistics stats, FSDataOutputStream delegate) {
		super(out, stats);
		this.delegate = delegate;
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
		delegate.hflush();
	}

	@Override
	public void hsync() throws IOException {
		delegate.hsync();
	}

	@Override
	public void setDropBehind(Boolean dropBehind) throws IOException {
		delegate.setDropBehind(dropBehind);
	}

	// override from java.io.OutputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public void write(int b) throws IOException {
		delegate.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		delegate.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		delegate.write(b);
	}

	@Override
	public void flush() throws IOException {
		delegate.flush();
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
		return delegate.toString();
	}

}
