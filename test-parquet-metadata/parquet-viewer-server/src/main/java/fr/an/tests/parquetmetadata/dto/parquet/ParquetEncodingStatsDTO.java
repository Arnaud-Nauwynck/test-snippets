package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.parquet.column.Encoding;

import java.util.Map;

/**
 * cf {@link org.apache.parquet.column.EncodingStats}
 */
@Data
@NoArgsConstructor @AllArgsConstructor
public class ParquetEncodingStatsDTO {
    Map</*Encoding*/String, Number> dictStats;
    Map</*Encoding*/String, Number> dataStats;
    boolean usesV2Pages;

}
