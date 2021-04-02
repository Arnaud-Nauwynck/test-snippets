package fr.an.tests.parquetmetadata.dto;

/**
 * Enum to annotate whether lists of min/max elements inside ColumnIndex are
 * ordered and if so, in which direction.
 */
public enum ParquetBoundaryOrder {

	UNORDERED(0), ASCENDING(1), DESCENDING(2);

	public final int value;

	private ParquetBoundaryOrder(int value) {
		this.value = value;
	}

}
