package fr.an.tests.hadoopfsinstrumented;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.ReadOption;
import org.apache.hadoop.io.ByteBufferPool;

public class InstrumentedFSDataInputStream extends FSDataInputStream {

	private final FSDataInputStream delegate;

	// --------------------------------------------------------------------------------------------

	public InstrumentedFSDataInputStream(InputStream in, FSDataInputStream delegate) {
		super(in);
		this.delegate = delegate;
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
		delegate.seek(desired);
	}

	@Override
	public long getPos() throws IOException {
		return delegate.getPos();
	}

	@Override
	public int read(long position, byte[] buffer, int offset, int length) throws IOException {
		return delegate.read(position, buffer, offset, length);
	}
	
	@Override
	public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
		delegate.readFully(position, buffer, offset, length);
	}

	@Override
	public void readFully(long position, byte[] buffer) throws IOException {
		delegate.readFully(position, buffer);
	}

	@Override
	public boolean seekToNewSource(long targetPos) throws IOException {
		return delegate.seekToNewSource(targetPos);
	}

	@Override
	public int read(ByteBuffer buf) throws IOException {
		return delegate.read(buf);
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
	public ByteBuffer read(ByteBufferPool bufferPool, int maxLength, EnumSet<ReadOption> opts)
			throws IOException, UnsupportedOperationException {
		return delegate.read(bufferPool, maxLength, opts);
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


	// override from java.io.InputStream
	// --------------------------------------------------------------------------------------------

	@Override
	public int read() throws IOException {
		return delegate.read();
	}

	@Override
	public long skip(long n) throws IOException {
		return delegate.skip(n);
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
		delegate.reset();
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
		return delegate.toString();
	}

}
