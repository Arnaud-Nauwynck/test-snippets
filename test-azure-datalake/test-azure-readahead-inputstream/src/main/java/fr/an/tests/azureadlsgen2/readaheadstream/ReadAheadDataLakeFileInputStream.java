package fr.an.tests.azureadlsgen2.readaheadstream;

import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.models.*;
import lombok.AllArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * InputStream adapter for DataLakeFileClient, using internally N read-ahead blocks calls with thread pool
 */
public class ReadAheadDataLakeFileInputStream extends InputStream {

    public static final int DEFAULT_READ_BLOCK_SIZE = 8 * 1024 * 1024;
    public static final int DEFAULT_READAHEAD_THREAD_COUNT = 5;

    private final DataLakeFileClient underlyingFileClient;

    private final int readBlockSize;

    private final int readaheadThreadCount;

    private final ExecutorService executorService;

    private final DownloadRetryOptions readBlockRetryOptions = null; // default to 5 retries
    private final Duration readBlockTimeout = Duration.ofMinutes(10);

    private final Object lock = new Object();

    // @GuardedBy("lock")
    private final List<Future<FileBlockReadResponse>> fileBlockRespFutures = new ArrayList<>();

    private byte[] currBlockData;
    private int currBlockDataOffset;
    private long currFilePosition;

    private long fileLength;

    private long nextReadAheadFilePosition;

    @AllArgsConstructor
    private static class FileBlockReadResponse {
        final long filePos;
        final byte[] data;
        final long submitTime;
        final int submitToExecReadMillis;
        final int readMillis;
    }

    //---------------------------------------------------------------------------------------------

    public ReadAheadDataLakeFileInputStream(DataLakeFileClient underlyingFileClient, int readaheadThreadCount) {
        this(underlyingFileClient, DEFAULT_READ_BLOCK_SIZE, readaheadThreadCount, ForkJoinPool.commonPool());
    }

    public ReadAheadDataLakeFileInputStream(DataLakeFileClient underlyingFileClient,
                                            int readBlockSize, int readaheadThreadCount,
                                            ExecutorService executorService) {
        this.underlyingFileClient = underlyingFileClient;
        this.readBlockSize = readBlockSize;
        this.readaheadThreadCount = readaheadThreadCount;
        this.executorService = executorService;

        PathProperties fileProps = underlyingFileClient.getProperties();
        this.fileLength = fileProps.getFileSize();
        for(int i = 0; i < readaheadThreadCount && nextReadAheadFilePosition < fileLength; i++) {
            doSubmitReadAheadNextBlock();
        }
    }

    public long getCurrFilePosition() {
        return currFilePosition;
    }

    public long getNextReadAheadFilePosition() {
        synchronized (lock) {
            return nextReadAheadFilePosition;
        }
    }

    // implements java.io.InputStream
    //---------------------------------------------------------------------------------------------

    /** implements InputStream, but should rarely be used, cf instead <code>read(byte[],offset,len)</code> */
    @Override
    public int read() throws IOException {
        byte[] data = new byte[1];
        int readCount = read(data, 0, 1);
        if (readCount == 0) {
            // should not occur!
            for(;;) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                readCount = read(data, 0, 1);
                if (readCount == 1) {
                    break;
                }
            }
        }
        return data[0];
    }

    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        int currOff = off;
        int currRemainLen = len;
        int maxWaitCount = 3;
        for(int waitCount = 0; waitCount < maxWaitCount; waitCount++) {
            byte[] buffer;
            int bufferPos;
            synchronized (lock) {
                buffer = this.currBlockData;
                bufferPos = this.currBlockDataOffset;
            }
            if (buffer != null) {
                // consume available data
                int availableLen = Math.min(currRemainLen, buffer.length - bufferPos);
                System.arraycopy(buffer, bufferPos, data, currOff, availableLen);
                bufferPos += availableLen;
                currRemainLen -= availableLen;
                this.currFilePosition += availableLen;
                if( bufferPos == buffer.length) {
                    // completly read block => remove, readAhead another block
                    synchronized (lock) {
                        this.currBlockData = null;
                        this.currBlockDataOffset = 0;
                        if (nextReadAheadFilePosition < fileLength) {
                            doSubmitReadAheadNextBlock();
                        }
                    }
                }
                if (currRemainLen == 0) {
                    break;
                }
            } else {
                // wait some data!
                Future<FileBlockReadResponse> optWaitFuture = null;
                synchronized (lock) {
                    if (! fileBlockRespFutures.isEmpty()) {
                        Future<FileBlockReadResponse> respFuture0 = fileBlockRespFutures.get(0);
                        if (respFuture0.isDone()) {
                            doOnFileBlockFutureDone(respFuture0);
                        } else {
                            // wait little time on this future... maybe give up after a while
                            optWaitFuture = respFuture0;
                            // .. respFuture0.wait(100);  outside of lock?
                        }
                    }
                }
                if (optWaitFuture != null) {
                    try {
                        optWaitFuture.wait(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (optWaitFuture.isDone()) {
                        synchronized (lock) {
                            doOnFileBlockFutureDone(optWaitFuture);
                        }
                    }
                }
            }
        }
        return len - currRemainLen;
    }

    //---------------------------------------------------------------------------------------------

    private void doOnFileBlockFutureDone(Future<FileBlockReadResponse> respFuture0) {
        fileBlockRespFutures.remove(0);
        try {
            FileBlockReadResponse resp0Block = respFuture0.get();
            this.currBlockData = resp0Block.data;
            // TOADD log slow calls, update time statistics
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        this.lock.notifyAll();
    }

    private void doSubmitReadAheadNextBlock() {
        long remainReadAhead = fileLength - nextReadAheadFilePosition;
        if (remainReadAhead <= 0) {
            return;
        }
        final long readPos = nextReadAheadFilePosition;
        int readLen = (int) Math.min(remainReadAhead, readBlockSize);
        long submitTime = System.currentTimeMillis();
        Future<FileBlockReadResponse> future = this.executorService.submit(
                () -> asyncReadAheadBlock(readPos, readLen, submitTime) // executed in other thread
        );
        this.nextReadAheadFilePosition += readLen;
        this.fileBlockRespFutures.add(future);
    }

    private FileBlockReadResponse asyncReadAheadBlock(long filePos, int count, long submitTime) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(count);
        FileRange range = new FileRange(filePos, (long)count);
        DataLakeRequestConditions blockReadRequestConditions = null; // lease, ifModified..
        // Context context
        long readStartTime = System.currentTimeMillis();

        FileReadResponse resp = this.underlyingFileClient.readWithResponse(buffer, range, //
                readBlockRetryOptions, blockReadRequestConditions, false, readBlockTimeout, null);

        int readMillis = (int) (System.currentTimeMillis() - readStartTime);
        int submitToExecReadMillis = (int) (readStartTime - readStartTime);
        return new FileBlockReadResponse(filePos, buffer.toByteArray(),
                submitTime, submitToExecReadMillis, readMillis);
    }

}
