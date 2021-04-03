package fr.an.tests.parquetmetadata.dto;

import java.util.List;

import lombok.Data;

/**
 * Description for ColumnIndex. Each <array-field>[i] refers to the page at
 * OffsetIndex.page_locations[i]
 */
@Data
public class ParquetColumnIndexDTO {

	/**
	 * A list of Boolean values to determine the validity of the corresponding min
	 * and max values. If true, a page contains only null values, and writers have
	 * to set the corresponding entries in min_values and max_values to byte[0], so
	 * that all lists have the same length. If false, the corresponding entries in
	 * min_values and max_values must be valid.
	 */
	List<Boolean> null_pages;

	/**
	 * Two lists containing lower and upper bounds for the values of each page.
	 * These may be the actual minimum and maximum values found on a page, but can
	 * also be (more compact) values that do not exist on a page. For example,
	 * instead of storing ""Blart Versenwald III", a writer may set
	 * min_values[i]="B", max_values[i]="C". Such more compact values must still be
	 * valid values within the column's logical type. Readers must make sure that
	 * list entries are populated before using them by inspecting null_pages.
	 */
	List<byte[]> min_values;
	List<byte[]> max_values;

	/**
	 * Stores whether both min_values and max_values are orderd and if so, in which
	 * direction. This allows readers to perform binary searches in both lists.
	 * Readers cannot assume that max_values[i] <= min_values[i+1], even if the
	 * lists are ordered.
	 */
	ParquetBoundaryOrder boundary_order;

	/** A list containing the number of null values for each page **/
	List<Long> null_counts;

}
