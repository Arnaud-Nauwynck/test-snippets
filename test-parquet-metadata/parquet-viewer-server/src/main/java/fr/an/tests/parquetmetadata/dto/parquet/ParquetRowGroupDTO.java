package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ParquetRowGroupDTO {

	/**
	 * Metadata for each column chunk in this row group. This list must have the
	 * same order as the SchemaElement list in FileMetaData.
	 **/
	List<ParquetColumnChunkDTO> colChunks;

	/** Total byte size of all the uncompressed column data in this row group **/
	long totalByteSize;

	/** Number of rows in this row group **/
	long numRows;

	/**
	 * If set, specifies a sort ordering of the rows in this RowGroup. The sorting
	 * columns can be a subset of all the columns.
	 */
	@JsonInclude(Include.NON_NULL)
	List<ParquetSortingColumnDTO> sortingCols;

	/**
	 * Byte offset from beginning of file to first page (data or dictionary) in this
	 * row group
	 **/
	Long fileOffset;

	/**
	 * Total byte size of all compressed (and potentially encrypted) column data in
	 * this row group
	 **/
	Long totalCompressedSize;

	/** Row group ordinal in the file **/
	Integer ordinal;

}
