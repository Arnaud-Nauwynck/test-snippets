package fr.an.tests.parquetmetadata.dto;

public class ParquetDictionaryPageHeaderDTO {

	/** Number of values in the dictionary **/
	int num_values;

	/** Encoding using this dictionary page **/
	ParquetEncoding encoding;

	/** If true, the entries in the dictionary are sorted in ascending order **/
	Boolean is_sorted;
}
