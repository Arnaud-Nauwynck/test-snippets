package fr.an.tests.parquetmetadata.dto.parquet;

/**
 * Representation of Schemas
 */
public enum ParquetFieldRepetitionType {

	/** This field is required (can not be null) and each record has exactly 1 value. */
	REQUIRED(0),

	/** The field is optional (can be null) and each record has 0 or 1 values. */
	OPTIONAL(1),

	/** The field is repeated and can contain 0 or more values */
	REPEATED(2);
	

  	public final int value;

	private ParquetFieldRepetitionType(int value) {
		this.value = value;
	}

}
