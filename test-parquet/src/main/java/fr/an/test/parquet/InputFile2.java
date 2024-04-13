package fr.an.test.parquet;

import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.IOException;
import java.io.InputStream;

public class InputFile2 implements org.apache.parquet.io.InputFile {

    @Override
    public long getLength() throws IOException {
        return 0;
    }

    @Override
    public SeekableInputStream newStream() throws IOException {
        InputStream is = null;
        return new H2Dele;
    }
}
