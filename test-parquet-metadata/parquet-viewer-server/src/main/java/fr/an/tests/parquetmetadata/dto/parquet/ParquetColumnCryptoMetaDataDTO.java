package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;

import lombok.Data;

@Data
public class ParquetColumnCryptoMetaDataDTO {

	// union
	ParquetEncryptionWithFooterKeyDTO ENCRYPTION_WITHFOOTERKEY;
	ParquetEncryptionWithColumnKeyDTO ENCRYPTION_WITHCOLUMNKEY;

	@Data
	public static class ParquetEncryptionWithFooterKeyDTO {
	}

	@Data
	public static class ParquetEncryptionWithColumnKeyDTO {
		
	  /** Column path in schema **/
	  List<String> pathInSchema;
	  
	  /** Retrieval metadata of column encryption key **/
	  byte[] keyMetadata;
	}

}
