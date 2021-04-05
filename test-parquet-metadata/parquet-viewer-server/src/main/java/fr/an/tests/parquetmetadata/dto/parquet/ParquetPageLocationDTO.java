package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.Data;

@Data
public class ParquetPageLocationDTO {

	/** Offset of the page in the file **/
	long offset;

	/**
	 * Size of the page, including header. Sum of compressedPageSize and header
	 * length
	 */
	int compressedPageSize;

	/**
	 * Index within the RowGroup of the first row of the page; this means pages
	 * change on record boundaries (r = 0).
	 */
	long firstRowIndex;
}