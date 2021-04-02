package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

@Data
public class ParquetSortingColumnDTO {

	/** The column index (in this row group) **/
	int column_idx;

	/** If true, indicates this column is sorted in descending order. **/
	boolean descending;

	/**
	 * If true, nulls will come before non-null values, otherwise, nulls go at the
	 * end.
	 */
	boolean nulls_first;

}
