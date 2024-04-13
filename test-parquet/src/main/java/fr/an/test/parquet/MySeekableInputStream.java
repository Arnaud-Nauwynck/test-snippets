package fr.an.test.parquet;

import org.apache.parquet.io.DelegatingSeekableInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MySeekableInputStream extends org.apache.parquet.io.DelegatingSeekableInputStream {

    public MySeekableInputStream(InputStream stream) {
        super(stream);
    }

    @Override
    public long getPos() throws IOException {
        return 0; // TODO
    }

    @Override
    public void seek(long l) throws IOException {
        // TODO
    }

    @Override
    public int read(ByteBuffer buf) throws IOException {
        return super.read(buf);
    }

    @Override
    public void readFully(ByteBuffer buf) throws IOException {
        super.readFully(buf);
    }
}
