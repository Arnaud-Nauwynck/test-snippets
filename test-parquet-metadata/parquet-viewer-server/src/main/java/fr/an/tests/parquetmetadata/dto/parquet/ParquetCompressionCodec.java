package fr.an.tests.parquetmetadata.dto.parquet;

/**
 * Supported compression algorithms.
 *
 * Codecs added in 2.4 can be read by readers based on 2.4 and later.
 * Codec support may vary between readers based on the format version and
 * libraries available at runtime. Gzip, Snappy, and LZ4 codecs are
 * widely available, while Zstd and Brotli require additional libraries.
 */
public enum ParquetCompressionCodec {

	UNCOMPRESSED (0),
	SNAPPY (1),
	GZIP (2),
	LZO (3),
	BROTLI (4), // Added in 2.4
	LZ4 (5),    // Added in 2.4
	ZSTD (6);   // Added in 2.4


	public final int value;

	private ParquetCompressionCodec(int value) {
		this.value = value;
	}
	
}
