package fr.an.test.parquet;

import java.io.ByteArrayOutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.parquet.hadoop.thrift.ThriftReadSupport;
import org.apache.parquet.hadoop.thrift.ThriftToParquetFileWriter;
import org.apache.parquet.hadoop.util.ContextUtil;
import org.apache.parquet.thrift.ThriftParquetReader;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.junit.Test;

import fr.an.test.parquet.thrift.ThriftFoo;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThriftParquetTest {

    @Test
    public void testThriftParquet_write_read() throws Exception {
        Path path = new Path("target/test-thrift.parquet");
        Configuration conf = new Configuration();
        val fs = FileSystem.getLocal(conf);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
      
        log.info("write thrift -> parquet");
        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        TaskAttemptID taskId = new TaskAttemptID("local", 0, true, 0, 0);
        TaskAttemptContext taskAttemptCtx = ContextUtil.newTaskAttemptContext(conf, taskId);
        
        try (ThriftToParquetFileWriter w = new ThriftToParquetFileWriter(
                path, 
                taskAttemptCtx, 
                protocolFactory, ThriftFoo.class)) {
     
            for(int i = 0; i < 100; i++) {
                ThriftFoo foo = new ThriftFoo();
                foo.setIntField(i);
                
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final TProtocol protocol = protocolFactory.getProtocol(
                        new TIOStreamTransport(baos));
           
                foo.write(protocol);
           
                w.write(new BytesWritable(baos.toByteArray()));
    
            }
        }
        
        log.info("read parquet -> thrift");
        try (val reader = ThriftParquetReader.builder(
                new ThriftReadSupport<>(ThriftFoo.class), path)
                // .withThriftClass(ThriftFoo.class)
                .build()) {
            for(int i = 0; i < 100; i++) {
                ThriftFoo foo = reader.read();
                if (foo.getIntField() != i) {
                    throw new IllegalStateException();
                }
            }
        }

        log.info("done read parquet");
    }
}
