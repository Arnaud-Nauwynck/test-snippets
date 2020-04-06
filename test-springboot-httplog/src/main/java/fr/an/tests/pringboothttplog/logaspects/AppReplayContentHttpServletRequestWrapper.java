package fr.an.tests.pringboothttplog.logaspects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppReplayContentHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private byte[] replayContent;
	private IOException replayGetInputStreamException;
	private IOException replayReadException;
	private InputStream replayBufferInputStream;
	private BufferedReader replayReader;
	private ServletInputStream replayServletInputStream;
	
    public AppReplayContentHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

    public byte[] consumeAllForReplay() {
		// explicitely consume ahead of time the body InputStream !!
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			InputStream reqInputStream = null;
			try {
				reqInputStream = getRequest().getInputStream();
			} catch(IOException ex) {
				this.replayGetInputStreamException = ex; // no rethrow here..
				log.info("Failed to read request InputStream.. will be rethrown on getInputStream().read");
			}
			try {
				// read fully, to buffer
				byte[] buf = new byte[4096];
				int readCount = 0;
				while( -1 != (readCount = reqInputStream.read(buf))) {
					buffer.write(buf, 0, readCount);
				}
			} catch(IOException ex) {
				this.replayReadException = ex; // no rethrow here..
				log.info("Failed to read request InputStream.. will be rethrown on getInputStream().read");
			} finally {
				if (reqInputStream != null) {
					reqInputStream.close();
				}
			}
			
			this.replayContent = buffer.toByteArray();
		} catch(IOException ex) {
			// should not occur: buffer.close() never throws
		}
		this.replayBufferInputStream = new ByteArrayInputStream(this.replayContent);
		this.replayServletInputStream = new InnerReplayServletInputStream();
		return replayContent;
    }
    
	@Override
    public ServletInputStream getInputStream() throws IOException {
		if (replayBufferInputStream != null) {
			if (this.replayGetInputStreamException != null) {
				throw this.replayGetInputStreamException;
			}

			return replayServletInputStream;
		}
		return getRequest().getInputStream();
    }

	@Override
	public BufferedReader getReader() throws IOException {
		if (replayReader != null) {
			return replayReader;
		}
		if (replayBufferInputStream != null) {
			String reqEncoding = getRequest().getCharacterEncoding(); // example: "aplication/json;charset=UTF-8"
			
			Charset charset;
			try {
				charset = Charset.forName(reqEncoding);
			} catch(Exception ex) {
				// error.. default to UTF-8!
				charset = Charset.forName("UTF-8"); 
			}
			
			this.replayReader = new BufferedReader(new InputStreamReader(replayBufferInputStream, charset));
		}
		return getRequest().getReader();
	}

	private class InnerReplayServletInputStream extends ServletInputStream {
		
		private void checkReplayReadEx() throws IOException {
			if (replayReadException != null) {
				throw replayReadException;
			}
		}
		
		@Override
		public int read() throws IOException {
			checkReplayReadEx();
			return replayBufferInputStream.read();
		}
		
		@Override
		public void setReadListener(ReadListener listener) {
			// ?? TODO
			log.warn("NOT IMPLEMENTED setReadListener()");
			// listener.onAllDataRead();
		}
		
		@Override
		public boolean isReady() {
			return true;
		}
		
		@Override
		public boolean isFinished() {
			try {
				return replayBufferInputStream.available() == 0;
			} catch(IOException ex) {
				return true; // can not occur on ByteArrayInputStream
			}
		}

//		@Override
//		public int readLine(byte[] b, int off, int len) throws IOException {
//			checkReplayReadEx();
//			return replayBufferInputStream.readLine(b, off, len);
//		}

		@Override
		public int read(byte[] b) throws IOException {
			checkReplayReadEx();
			return replayBufferInputStream.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			checkReplayReadEx();
			return replayBufferInputStream.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			return replayBufferInputStream.skip(n);
		}

		@Override
		public int available() throws IOException {
			return replayBufferInputStream.available();
		}

		@Override
		public void close() throws IOException {
			replayBufferInputStream.close();
		}

		@Override
		public synchronized void mark(int readlimit) {
			replayBufferInputStream.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			replayBufferInputStream.reset();
		}

		@Override
		public boolean markSupported() {
			return replayBufferInputStream.markSupported();
		}
		
	}

}
