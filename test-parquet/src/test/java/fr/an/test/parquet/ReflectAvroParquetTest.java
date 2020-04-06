package fr.an.test.parquet;

import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.junit.Test;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectAvroParquetTest {

    @Test
    public void test() throws Exception {
        Configuration conf = new Configuration();
        Path path = new Path("target/test-reflect.gz.parquet");

        log.info("Write DTO (avro reflect) -> parquet");
        val outputFile = HadoopOutputFile.fromPath(path, conf);
        try (ParquetWriter<FooDTO> writer = AvroParquetWriter.<FooDTO>builder(outputFile)
            .withSchema(ReflectData.AllowNull.get().getSchema(FooDTO.class))
            .withDataModel(ReflectData.get())
            .withConf(conf)
            // .withCompressionCodec(CompressionCodecName.GZIP)
            .withWriteMode(Mode.OVERWRITE)
            .build()) {

            for(int i = 0; i < 100; i++) {
                FooDTO foo = new FooDTO();
                foo.setIntField(i);
                
                writer.write(foo);
            }
        }

        log.info("Read parquet -> avro reflect -> DTO");
        val inputFile = HadoopInputFile.fromPath(path, conf);
        try (ParquetReader<FooDTO> reader = AvroParquetReader.<FooDTO>builder(inputFile)
            .withDataModel(new ReflectData(FooDTO.class.getClassLoader()))
            .disableCompatibility() // always use this (since this is a new project)
            .withConf(conf)
            .build()) {

            for(int i = 0; i < 100; i++) {
                FooDTO foo = reader.read();
                if (foo.getIntField() != i) {
                    throw new IllegalStateException();
                }
            }
        }
        
        log.info("finished read parquet");
    }    
}
