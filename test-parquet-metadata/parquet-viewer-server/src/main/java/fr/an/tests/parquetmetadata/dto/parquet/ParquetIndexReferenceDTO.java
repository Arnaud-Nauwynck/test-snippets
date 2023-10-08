package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link org.apache.parquet.internal.hadoop.metadata.IndexReference}
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class ParquetIndexReferenceDTO {
    long offset;
    int length;

}
