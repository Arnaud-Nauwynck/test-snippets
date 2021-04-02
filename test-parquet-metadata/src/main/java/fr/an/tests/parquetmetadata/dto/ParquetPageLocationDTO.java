package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

@Data
public class ParquetPageLocationDTO {

	/** Offset of the page in the file **/
	long offset;

	/**
	 * Size of the page, including header. Sum of compressed_page_size and header
	 * length
	 */
	int compressed_page_size;

	/**
	 * Index within the RowGroup of the first row of the page; this means pages
	 * change on record boundaries (r = 0).
	 */
	long first_row_index;
}