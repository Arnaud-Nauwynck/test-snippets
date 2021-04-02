package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

@Data
public class ParquetColumnChunkDTO {

	/** File where column data is stored.  If not set, assumed to be same file as
	 * metadata.  This path is relative to the current file.
	 **/
	String file_path;

	/** Byte offset in file_path to the ColumnMetaData **/
	long file_offset;

	/** Column metadata for this chunk. This is the same content as what is at
	 * file_path/file_offset.  Having it here has it replicated in the file
	 * metadata.
	 **/
	ParquetColumnMetaDataDTO meta_data;

	/** File offset of ColumnChunk's OffsetIndex **/
	Long offset_index_offset;

	/** Size of ColumnChunk's OffsetIndex, in bytes **/
	Integer offset_index_length;

	/** File offset of ColumnChunk's ColumnIndex **/
	Long column_index_offset;

	/** Size of ColumnChunk's ColumnIndex, in bytes **/
	Integer column_index_length;

	/** Crypto metadata of encrypted columns **/
	ParquetColumnCryptoMetaDataDTO crypto_metadata;

	/** Encrypted column metadata for this chunk **/
	byte[] encrypted_column_metadata;

}
