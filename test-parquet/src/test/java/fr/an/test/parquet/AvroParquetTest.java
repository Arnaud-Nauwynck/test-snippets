package fr.an.test.parquet;

import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.junit.Test;

import fr.an.test.parquet.avro.AvroFoo;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AvroParquetTest {

    @Test
    public void testAvroToParquet_write_read() throws Exception {
        Path path = new Path("target/test.parquet");
        Configuration conf = new Configuration();
        val inputFile = HadoopInputFile.fromPath(path, conf);
        
        Schema fooSchema = 
                // ReflectData.get().getSchema(FooDTO.class);
                AvroFoo.getClassSchema();
        
        log.info("Write parquet");
        try (ParquetWriter<AvroFoo> writer = AvroParquetWriter.<AvroFoo>builder(path)
                .withSchema(fooSchema)
                // .enableDictionaryEncoding()
                // .withDataModel(model)
                .withCompressionCodec(CompressionCodecName.GZIP)
                // .withConf(conf)
                .withWriteMode(Mode.OVERWRITE)
                .withPageSize(4 * 1024 * 1024) // For compression
                .withRowGroupSize(16 * 1024 * 1024) // For write buffering (Page size)
                .build()) {
            
            for(int i = 0; i < 100; i++) {
                AvroFoo foo = new AvroFoo();
                foo.setIntField(i);
                
                writer.write(foo);
            }
            
        } // writer.close();
        
        log.info("reading parquet");
        try (ParquetReader<AvroFoo> reader = AvroParquetReader.<AvroFoo>builder(inputFile)
                .build()) {
    
            for(int i = 0; i < 100; i++) {
                AvroFoo foo = reader.read();
                if (foo.getIntField() != i) {
                    throw new RuntimeException("");
                }
            }
        } // reader.close();
        
        log.info("Finished");
    }
}
