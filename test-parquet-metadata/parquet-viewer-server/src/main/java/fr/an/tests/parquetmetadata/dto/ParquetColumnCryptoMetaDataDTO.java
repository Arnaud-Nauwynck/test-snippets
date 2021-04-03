package fr.an.tests.parquetmetadata.dto;

import java.util.List;

import lombok.Data;

@Data
public class ParquetColumnCryptoMetaDataDTO {

	// union
	ParquetEncryptionWithFooterKeyDTO ENCRYPTION_WITH_FOOTER_KEY;
	ParquetEncryptionWithColumnKeyDTO ENCRYPTION_WITH_COLUMN_KEY;

	@Data
	public static class ParquetEncryptionWithFooterKeyDTO {
	}

	@Data
	public static class ParquetEncryptionWithColumnKeyDTO {
		
	  /** Column path in schema **/
	  List<String> path_in_schema;
	  
	  /** Retrieval metadata of column encryption key **/
	  byte[] key_metadata;
	}

}
