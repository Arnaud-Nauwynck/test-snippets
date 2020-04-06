package fr.an.test.parquet;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type.Repetition;

public class FooDTOParquetWriterBuilder extends ParquetWriter.Builder<FooDTO, FooDTOParquetWriterBuilder> {

    public FooDTOParquetWriterBuilder(Path file) {
        super(file);
    }

    @Override
    protected FooDTOParquetWriterBuilder self() {
        return this;
    }

    @Override
    protected WriteSupport<FooDTO> getWriteSupport(Configuration conf) {
        return new FooDTOWriteSupport();
    }
}

class FooDTOWriteSupport extends WriteSupport<FooDTO> {

    private RecordConsumer recordConsumer;

    @Override
    public WriteContext init(Configuration configuration) {
        MessageType schema = new MessageType("fr.an.test.parquet.FooDTO",
//                  new GroupType(Repetition.REQUIRED, "FooDTO",
                new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.INT32, "intField")
//                      )
        );

        Map<String, String> extraMetaData = new HashMap<String, String>();
        return new WriteContext(schema, extraMetaData);
    }

    @Override
    public void prepareForWrite(RecordConsumer recordConsumer) {
        this.recordConsumer = recordConsumer;
    }

    @Override
    public void write(FooDTO src) {
        recordConsumer.startMessage();

        // recordConsumer.startGroup();

        recordConsumer.startField("intField", 0);
        recordConsumer.addInteger(src.getIntField());
        recordConsumer.endField("intField", 0);

        // recordConsumer.endGroup();
        
        recordConsumer.endMessage();
    }
}
