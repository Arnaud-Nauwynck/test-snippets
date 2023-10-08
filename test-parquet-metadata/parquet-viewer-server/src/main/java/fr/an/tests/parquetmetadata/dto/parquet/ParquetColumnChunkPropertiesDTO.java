package fr.an.tests.parquetmetadata.dto.parquet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for {@link org.apache.parquet.hadoop.metadata.ColumnChunkProperties}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParquetColumnChunkPropertiesDTO {

    ParquetCompressionCodecName codec;
    /*ColumnPath*/ String path;
    // TODO redundant?? PrimitiveType type;
    Set<ParquetEncoding> encodings;

}
