package fr.an.tests.parquetmetadata.dto.parquet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ParquetColumnChunkDTO {

	String colName;
	
	/** File where column data is stored.  If not set, assumed to be same file as
	 * metadata.  This path is relative to the current file.
	 **/
	@JsonInclude(Include.NON_NULL)
	String filePath;

	/** Byte offset in filePath to the ColumnMetaData **/
	long fileOffset;

	/** Column metadata for this chunk. This is the same content as what is at
	 * filePath/fileOffset.  Having it here has it replicated in the file
	 * metadata.
	 **/
	ParquetColumnChunkMetaDataDTO metaData;

	/** File offset of ColumnChunk's OffsetIndex **/
	Long offsetIndexOffset;

	/** Size of ColumnChunk's OffsetIndex, in bytes **/
	Integer offsetIndexLength;

	/** File offset of ColumnChunk's ColumnIndex **/
	Long colIndexOffset;

	/** Size of ColumnChunk's ColumnIndex, in bytes **/
	Integer colIndexLength;

	/** Crypto metadata of encrypted columns **/
	@JsonInclude(Include.NON_NULL)
	ParquetColumnCryptoMetaDataDTO cryptoMetadata;

	/** Encrypted column metadata for this chunk **/
	@JsonInclude(Include.NON_NULL)
	byte[] encryptedColMetadata;

}
