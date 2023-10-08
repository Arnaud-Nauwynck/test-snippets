package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * DTO for {@link org.apache.parquet.hadoop.metadata.BlockMetaData}
 */
@Data
public class ParquetBlockMetadataDTO {

	/**
	 * Metadata for each column chunk in this row group. This list must have the
	 * same order as the SchemaElement list in FileMetaData.
	 **/
	List<ParquetColumnChunkMetaDataDTO> columns;

	/** Number of rows in this row group **/
	long rowCount;

	/** Total byte size of all the uncompressed column data in this row group **/
	long totalByteSize;

	String path;

	/** Row group ordinal in the file **/
	int ordinal;

	long rowIndexOffset;

}
