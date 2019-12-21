package fr.an.test.parquet;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FooDTOParquetTest {

    @Test
    public void test() throws Exception {
      Path path = new Path("target/test-purejava.parquet");
      
      log.info("Write DTO -> pure java writerbulder -> parquet");
      try (ParquetWriter<FooDTO> writer = new FooDTOParquetWriterBuilder(path)
              .withWriteMode(Mode.OVERWRITE)
              .build()) {
          for(int i = 0; i < 100; i++) {
              FooDTO foo = new FooDTO();
              foo.setIntField(i);
              writer.write(foo);
          }
      } // writer.close();

      log.info("Read parquet -> DTO");
      try (ParquetReader<FooDTO> reader = ParquetReader.builder(new FooDTOReadSupport(), path)
              .build()) {
          for(int i = 0; i < 100; i++) {
              FooDTO foo = reader.read();
              if (foo.getIntField() != i) {
                  throw new IllegalStateException();
              }
          }
      } // reader.close()      
    
      log.info("finished Read parquet");
    }
}
