package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import org.apache.parquet.column.EncodingStats;
import org.apache.parquet.column.statistics.Statistics;
import org.apache.parquet.hadoop.metadata.ColumnChunkProperties;
import org.apache.parquet.internal.hadoop.metadata.IndexReference;

/**
 * Column Chunk Metadata
 * cf {@link org.apache.parquet.hadoop.metadata.ColumnChunkMetaData}
 */
@Data
public class ParquetColumnChunkMetaDataDTO {

	int rowGroupOrdinal;

	ParquetEncodingStatsDTO encodingStats;

	// we save 3 references by storing together the column properties that have few distinct values
	ParquetColumnChunkPropertiesDTO properties;

	ParquetIndexReferenceDTO columnIndexReference;
	ParquetIndexReferenceDTO offsetIndexReference;

	long bloomFilterOffset = -1;

	// field used in sub-class IntColumnChunkMetaData and LongColumnChunkMetaData
	// cf also EncryptedColumnChunkMetaData
	long firstDataPageOffset;
	long dictionaryPageOffset;
	long valueCount;
	long totalSize;
	long totalUncompressedSize;
	ParquetStatisticsDTO<?> statistics;

}
