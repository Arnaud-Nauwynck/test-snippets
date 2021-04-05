package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;

import lombok.Data;

/**
 * Description for ColumnIndex. Each <array-field>[i] refers to the page at
 * OffsetIndex.pageLocations[i]
 */
@Data
public class ParquetColumnIndexDTO {

	/**
	 * A list of Boolean values to determine the validity of the corresponding min
	 * and max values. If true, a page contains only null values, and writers have
	 * to set the corresponding entries in minValues and maxValues to byte[0], so
	 * that all lists have the same length. If false, the corresponding entries in
	 * minValues and maxValues must be valid.
	 */
	List<Boolean> nullPages;

	/**
	 * Two lists containing lower and upper bounds for the values of each page.
	 * These may be the actual minimum and maximum values found on a page, but can
	 * also be (more compact) values that do not exist on a page. For example,
	 * instead of storing ""Blart Versenwald III", a writer may set
	 * minValues[i]="B", maxValues[i]="C". Such more compact values must still be
	 * valid values within the column's logical type. Readers must make sure that
	 * list entries are populated before using them by inspecting nullPages.
	 */
	List<byte[]> minValues;
	List<byte[]> maxValues;

	/**
	 * Stores whether both minValues and maxValues are orderd and if so, in which
	 * direction. This allows readers to perform binary searches in both lists.
	 * Readers cannot assume that maxValues[i] <= minValues[i+1], even if the
	 * lists are ordered.
	 */
	ParquetBoundaryOrder boundaryOrder;

	/** A list containing the number of null values for each page **/
	List<Long> nullCounts;

}
