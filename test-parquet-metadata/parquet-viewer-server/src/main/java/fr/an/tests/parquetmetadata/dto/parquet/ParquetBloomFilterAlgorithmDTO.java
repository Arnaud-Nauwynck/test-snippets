package fr.an.tests.parquetmetadata.dto.parquet;

/** The algorithm used in Bloom filter. **/
public class ParquetBloomFilterAlgorithmDTO {
	// union

	/** Block-based Bloom filter. **/
	ParquetSplitBlockAlgorithmDTO BLOCK;
	
	/** Block-based algorithm type annotation. **/
	public static class ParquetSplitBlockAlgorithmDTO {}
}
