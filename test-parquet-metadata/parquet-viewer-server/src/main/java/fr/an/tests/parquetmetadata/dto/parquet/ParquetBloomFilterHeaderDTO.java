package fr.an.tests.parquetmetadata.dto.parquet;

/**
 * Bloom filter header is stored at beginning of Bloom filter data of each column
 * and followed by its bitset.
 **/
public class ParquetBloomFilterHeaderDTO {

	/** The size of bitset in bytes **/
	int numBytes;
	
	/** The algorithm for setting bits. **/
	ParquetBloomFilterAlgorithmDTO algorithm;

	/** The hash function used for Bloom filter. **/
	ParquetBloomFilterHashDTO hash;

	/** The compression used in the Bloom filter **/
	ParquetBloomFilterCompressionDTO compression;
	
}
