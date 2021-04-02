package fr.an.tests.parquetmetadata.dto;

/** 
 * The hash function used in Bloom filter. This function takes the hash of a column value
 * using plain encoding.
 **/
public class ParquetBloomFilterHashDTO {
	// union
	
	/** xxHash Strategy. **/
	ParquetXxHashDTO XXHASH;
	
	public static class ParquetXxHashDTO  {} 
}
