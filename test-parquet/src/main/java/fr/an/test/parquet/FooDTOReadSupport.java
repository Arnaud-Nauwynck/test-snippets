package fr.an.test.parquet;

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.InitContext;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;


public class FooDTOReadSupport extends ReadSupport<FooDTO> {

    @Override
    public ReadContext init(InitContext context) {
      MessageType fileSchema = context.getFileSchema();
      return new ReadContext(fileSchema);
    }

    @Override
    public RecordMaterializer<FooDTO> prepareForRead(Configuration configuration, Map<String, String> keyValueMetaData, MessageType fileSchema, ReadContext readContext) {
      return new FooDTOMaterializer();
    }
}

class FooDTOMaterializer extends RecordMaterializer<FooDTO> {

    private FooDTOBuilder fooBuilder = new FooDTOBuilder();
    private FooDTOConverter fooConverter = new FooDTOConverter(fooBuilder);

    @Override
    public FooDTO getCurrentRecord() {
      return fooBuilder.build();
    }

    @Override
    public GroupConverter getRootConverter() {
      return fooConverter;
    }
}
    
class FooDTOBuilder {
    private FooDTO curr = new FooDTO();
    
    public void addIntField(int v) {
        curr.setIntField(v);
    }
    
    public FooDTO build() {
        FooDTO res = curr;
        curr = new FooDTO();
        return res;
    }
}

class FooDTOConverter extends GroupConverter {

    private final FooIntFieldConverter intFieldConverter;

    public FooDTOConverter(FooDTOBuilder fooBuilder) {
      this.intFieldConverter = new FooIntFieldConverter(fooBuilder);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
      switch (fieldIndex) {
        case 0:
          return intFieldConverter;
        default:
          throw new IllegalStateException("got: " + fieldIndex);
      }
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }
}

class FooIntFieldConverter extends PrimitiveConverter {
    private FooDTOBuilder fooBuilder;

    public FooIntFieldConverter(FooDTOBuilder fooBuilder) {
        this.fooBuilder = fooBuilder;
    }

    @Override
    public void addInt(int value) {
        fooBuilder.addIntField(value);
    }
}