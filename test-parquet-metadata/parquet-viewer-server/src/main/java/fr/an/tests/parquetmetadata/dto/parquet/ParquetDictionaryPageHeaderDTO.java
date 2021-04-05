package fr.an.tests.parquetmetadata.dto.parquet;

public class ParquetDictionaryPageHeaderDTO {

	/** Number of values in the dictionary **/
	int numValues;

	/** Encoding using this dictionary page **/
	ParquetEncoding encoding;

	/** If true, the entries in the dictionary are sorted in ascending order **/
	Boolean isSorted;
}
