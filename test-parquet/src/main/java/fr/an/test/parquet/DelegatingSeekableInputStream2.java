package fr.an.test.parquet;

import org.apache.parquet.io.SeekableInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class DelegatingSeekableInputStream2 extends SeekableInputStream {

    private final int COPY_BUFFER_SIZE = 8*1024*1024;  // default to 8ko in  DelegatingSeekableInputStream ... changed to 8Mo !
    private final byte[] temp = new byte[COPY_BUFFER_SIZE];
    private final InputStream stream;

    public DelegatingSeekableInputStream2(InputStream stream) {
        this.stream = stream;
    }

    public InputStream getStream() {
        return this.stream;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public abstract long getPos() throws IOException;

    public abstract void seek(long var1) throws IOException;

    public int read() throws IOException {
        return this.stream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return this.stream.read(b, off, len);
    }

    public void readFully(byte[] bytes) throws IOException {
        readFully(this.stream, bytes, 0, bytes.length);
    }

    public void readFully(byte[] bytes, int start, int len) throws IOException {
        readFully(this.stream, bytes, start, len);
    }

    public int read(ByteBuffer buf) throws IOException {
        return buf.hasArray() ? readHeapBuffer(this.stream, buf) : readDirectBuffer(this.stream, buf, this.temp);
    }

    public void readFully(ByteBuffer buf) throws IOException {
        if (buf.hasArray()) {
            readFullyHeapBuffer(this.stream, buf);
        } else {
            readFullyDirectBuffer(this.stream, buf, this.temp);
        }

    }

    static void readFully(InputStream f, byte[] bytes, int start, int len) throws IOException {
        int offset = start;

        int bytesRead;
        for(int remaining = len; remaining > 0; offset += bytesRead) {
            bytesRead = f.read(bytes, offset, remaining);
            if (bytesRead < 0) {
                throw new EOFException("Reached the end of stream with " + remaining + " bytes left to read");
            }

            remaining -= bytesRead;
        }

    }

    static int readHeapBuffer(InputStream f, ByteBuffer buf) throws IOException {
        int bytesRead = f.read(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
        if (bytesRead < 0) {
            return bytesRead;
        } else {
            buf.position(buf.position() + bytesRead);
            return bytesRead;
        }
    }

    static void readFullyHeapBuffer(InputStream f, ByteBuffer buf) throws IOException {
        readFully(f, buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
        buf.position(buf.limit());
    }

    static int readDirectBuffer(InputStream f, ByteBuffer buf, byte[] temp) throws IOException {
        int nextReadLength = Math.min(buf.remaining(), temp.length);

        int totalBytesRead;
        int bytesRead;
        for(totalBytesRead = 0; (bytesRead = f.read(temp, 0, nextReadLength)) == temp.length; nextReadLength = Math.min(buf.remaining(), temp.length)) {
            buf.put(temp);
            totalBytesRead += bytesRead;
        }

        if (bytesRead < 0) {
            return totalBytesRead == 0 ? -1 : totalBytesRead;
        } else {
            buf.put(temp, 0, bytesRead);
            totalBytesRead += bytesRead;
            return totalBytesRead;
        }
    }

    static void readFullyDirectBuffer(InputStream f, ByteBuffer buf, byte[] temp) throws IOException {
        int nextReadLength = Math.min(buf.remaining(), temp.length);

        int bytesRead;
        for(bytesRead = 0; nextReadLength > 0 && (bytesRead = f.read(temp, 0, nextReadLength)) >= 0; nextReadLength = Math.min(buf.remaining(), temp.length)) {
            buf.put(temp, 0, bytesRead);
        }

        if (bytesRead < 0 && buf.remaining() > 0) {
            throw new EOFException("Reached the end of stream with " + buf.remaining() + " bytes left to read");
        }
    }
}
